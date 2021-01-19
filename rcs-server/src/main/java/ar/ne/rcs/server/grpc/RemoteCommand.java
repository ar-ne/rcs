package ar.ne.rcs.server.grpc;

import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.proto.server.RemoteCommandGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;

public class RemoteCommand extends RemoteCommandGrpc.RemoteCommandImplBase {
    public static final ConcurrentHashMap<RCS.DeviceDescription, StreamObserver<RCS.Command>> deviceMap = new ConcurrentHashMap<>();

    @Override
    public void list(Empty request, StreamObserver<RCS.Command> responseObserver) {
    }

    @Override
    public void submit(RCS.CommandResult request, StreamObserver<Empty> responseObserver) {
        super.submit(request, responseObserver);
    }

    @Override
    public StreamObserver<RCS.CommandResult> outputStreaming(StreamObserver<Empty> responseObserver) {
        return super.outputStreaming(responseObserver);
    }
}
