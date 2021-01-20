package ar.ne.rcs.server.repo;

import ar.ne.rcs.proto.DeviceConnection;
import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.server.apiClient.OldApiClient;
import ar.ne.rcs.server.dbKey.MongoK;
import ar.ne.rcs.server.dbKey.RedisK;
import ar.ne.rcs.server.serivce.NextSequenceService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DeviceRepo {

    public static final ConcurrentHashMap<String, StreamObserver<RCS.Command>> REMOTE_COMMAND_CLIENT_MAP = new ConcurrentHashMap<>();
    final MongoTemplate mongo;
    final NextSequenceService nextSequence;
    private final ValueOperations<String, String> heartbeatCache;
    private final StringRedisTemplate redisTemplate;
    @Value("${device.connection.heartbeat.ttl}")
    long heartbeatTTL;

    public DeviceRepo(MongoTemplate mongo, StringRedisTemplate redisTemplate, NextSequenceService nextSequence) {
        this.mongo = mongo;
        this.redisTemplate = redisTemplate;
        this.heartbeatCache = redisTemplate.opsForValue();
        this.nextSequence = nextSequence;
    }

    //token Mapper: token->DeviceDescription
    public RCS.Token getToken(RCS.DeviceDescription description) {
        return RCS.Token.newBuilder().setToken(description.toString()).build();
    }

    public boolean deviceValid(RCS.DeviceDescription description) {
        return new OldApiClient().deviceInfo(description.getImei()) != null;
    }

    public DeviceConnection.LoginResponse login(RCS.DeviceDescription description) {
        DeviceConnection.LoginResponse.Builder resp = DeviceConnection.LoginResponse.newBuilder()
                .setToken(getToken(description))
                .setHeartbeatInterval(heartbeatTTL / 4)
                .setResult(DeviceConnection.LoginResult.REJECT);

        if (deviceValid(description)) {
            resp.setResult(DeviceConnection.LoginResult.ACCEPT);
        }

        return resp.build();
    }

    public void startListing(String token, StreamObserver<RCS.Command> streamObserver) {
        REMOTE_COMMAND_CLIENT_MAP.put(token, streamObserver);
    }

    public void beat(DeviceConnection.Heartbeat heartbeat) {
        heartbeatCache.set(RedisK.DEVICE_HEARTBEAT + heartbeat.getToken(), heartbeat.getToken(), heartbeatTTL);
    }

    public long exec(String token, String command, RCS.CommandType type) {
        log.debug(token, command, type);
        if (REMOTE_COMMAND_CLIENT_MAP.containsKey(token)) {
            RCS.Command c = RCS.Command.newBuilder()
                    .setId(nextSequence.getNextSequence(MongoK.COMMANDS))
                    .setCommand(command)
                    .setTarget(RCS.Token.newBuilder().setToken(token).build())
                    .setType(type)
                    .setTime(new Date().getTime())
                    .setStatus(RCS.CommandStatus.ADDED)
                    .build();
            mongo.insert(c);
            mongo.save(c);
            REMOTE_COMMAND_CLIENT_MAP.get(token).onNext(c);
            return c.getId();
        }
        return -1;
    }

    public void onCommandSubmit(RCS.CommandResult result) {
        RCS.Command.Builder c = result.getCommand().toBuilder();
        c.setStatus(RCS.CommandStatus.RUNNING);
        c.build();
    }

    @Scheduled(cron = "0 0/5 * * * ? ")//every 5 minute
    public void gc() {
        Set<String> keys = redisTemplate.keys(RedisK.DEVICE_HEARTBEAT + "*");
        if (keys != null) {
            keys.stream().filter(REMOTE_COMMAND_CLIENT_MAP::containsKey).forEach(REMOTE_COMMAND_CLIENT_MAP::remove);
        }
    }
}
