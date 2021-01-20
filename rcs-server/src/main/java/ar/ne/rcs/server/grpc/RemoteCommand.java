package ar.ne.rcs.server.grpc;

import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.proto.server.RemoteCommandGrpc;
import ar.ne.rcs.server.repo.DeviceRepo;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

@Slf4j
@GRpcService
public class RemoteCommand extends RemoteCommandGrpc.RemoteCommandImplBase {

    final DeviceRepo repo;

    public RemoteCommand(DeviceRepo repo) {
        this.repo = repo;
    }

    @Override
    public void list(RCS.Token request, StreamObserver<RCS.Command> responseObserver) {
        repo.startListing(request.getToken(), responseObserver);
    }
}
