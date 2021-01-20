package ar.ne.rcs.server.grpc;


import ar.ne.rcs.proto.DeviceConnection.LoginResponse;
import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.proto.server.DeviceConnectionGrpc;
import ar.ne.rcs.server.repo.DeviceRepo;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

@Slf4j
@GRpcService
public class DeviceConnection extends DeviceConnectionGrpc.DeviceConnectionImplBase {
    final DeviceRepo deviceRepo;

    public DeviceConnection(DeviceRepo deviceRepo) {
        this.deviceRepo = deviceRepo;
    }

    @Override
    public void login(RCS.DeviceDescription description, StreamObserver<LoginResponse> responseObserver) {
        responseObserver.onNext(deviceRepo.login(description));
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ar.ne.rcs.proto.DeviceConnection.Heartbeat> beat(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<ar.ne.rcs.proto.DeviceConnection.Heartbeat>() {
            @Override
            public void onNext(ar.ne.rcs.proto.DeviceConnection.Heartbeat value) {
                log.debug("onNext: {}", value);
                deviceRepo.beat(value);
                responseObserver.onNext(Empty.getDefaultInstance());
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
