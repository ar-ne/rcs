package ar.ne.rcs.client.grpc;

import ar.ne.rcs.client.Cronjob;
import ar.ne.rcs.proto.DeviceConnection;
import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.proto.server.DeviceConnectionGrpc;
import ar.ne.rcs.proto.server.RemoteCommandGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GRpcClient {
    ManagedChannel channel;
    DeviceConnection.LoginResponse loginResponse;

    public GRpcClient() {
        channel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .enableRetry()
                .usePlaintext()
                .build();
        connect();
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public DeviceConnection.LoginResponse getLoginResponse() {
        return loginResponse;
    }

    private void connect() {
        login();
        beats();
        listCMD();
    }

    public void login() {
        DeviceConnectionGrpc.DeviceConnectionBlockingStub stub = DeviceConnectionGrpc.newBlockingStub(channel);
        loginResponse = stub.login(RCS.DeviceDescription.newBuilder()
                .setImei("imei")
                .setAndroidId("AndroidId")
                .setSerialNo("SerialNo")
                .build());
    }

    public void beats() {
        DeviceConnectionGrpc.DeviceConnectionStub stub = DeviceConnectionGrpc.newStub(channel);
        StreamObserver<DeviceConnection.Heartbeat> beat = stub.beat(new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
            }
        });
        new Cronjob(loginResponse.getHeartbeatInterval(), () -> {
            beat.onNext(DeviceConnection.Heartbeat.newBuilder().setToken(loginResponse.getToken().getToken()).build());
        });
    }

    public void listCMD() {
        RemoteCommandGrpc.RemoteCommandStub stub = RemoteCommandGrpc.newStub(channel);
        stub.list(loginResponse.getToken(), new StreamObserver<RCS.Command>() {
            @Override
            public void onNext(RCS.Command value) {
                log.info("New command: {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
            }
        });
    }
}
