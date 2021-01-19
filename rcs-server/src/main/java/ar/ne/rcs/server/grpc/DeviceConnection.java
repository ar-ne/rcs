package ar.ne.rcs.server.grpc;


import ar.ne.rcs.proto.DeviceConnection.LoginResponse;
import ar.ne.rcs.proto.DeviceConnection.LoginResult;
import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.proto.server.DeviceConnectionGrpc;
import ar.ne.rcs.server.apiClient.OldApiClient;
import ar.ne.rcs.server.utils.RedisK;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;

import java.util.Map;

@Slf4j
@GRpcService
public class DeviceConnection extends DeviceConnectionGrpc.DeviceConnectionImplBase {
    private final SetOperations<String, String> loginRecord;
    private final HashOperations<String, Object, Object> deviceInfos;
    private final ValueOperations<String, String> heartbeatCache;
    @Value("${device.connection.heartbeat.ttl}")
    int heartbeatTTL;

    public DeviceConnection(StringRedisTemplate stringRedisTemplate, RedisTemplate<String, Map<String, String>> mapRedisTemplate) {
        this.loginRecord = stringRedisTemplate.opsForSet();
        this.deviceInfos = mapRedisTemplate.opsForHash();
        this.heartbeatCache = stringRedisTemplate.opsForValue();
    }

    @Override
    public void login(RCS.DeviceDescription description, StreamObserver<LoginResponse> responseObserver) {
        String token = description.getImei() + description.getSerialNo() + description.getAndroidId();
        LoginResult result = LoginResult.REJECT;
        //if already login
        if (Boolean.TRUE.equals(loginRecord.isMember(RedisK.DEVICE_LOGIN_RECORD, token))) {
            result = LoginResult.ACCEPT;
        } else {
            //TODO: Verify device in database
            Map<String, String> deviceInfo = new OldApiClient().deviceInfo(description.getImei());
            if (deviceInfo != null) {
                loginRecord.add(RedisK.DEVICE_LOGIN_RECORD, token);
                deviceInfos.put(RedisK.DEVICE_INFO_CACHE, description.getImei(), deviceInfo);
                result = LoginResult.ACCEPT;
            }
        }
        result = LoginResult.ACCEPT;
        responseObserver.onNext(LoginResponse.newBuilder()
                .setResult(result)
                .setToken(token)
                .setHeartbeatInterval(heartbeatTTL / 4)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ar.ne.rcs.proto.DeviceConnection.Heartbeat> beat(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<ar.ne.rcs.proto.DeviceConnection.Heartbeat>() {
            @Override
            public void onNext(ar.ne.rcs.proto.DeviceConnection.Heartbeat value) {
                log.debug("onNext: {}", value);
                responseObserver.onNext(Empty.getDefaultInstance());
                heartbeatCache.set(RedisK.DEVICE_HEARTBEAT + value.getToken(), "", heartbeatTTL);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.debug("onComplete");
                responseObserver.onCompleted();
            }
        };
    }
}
