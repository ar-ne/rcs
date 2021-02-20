package ar.ne.rcs.android.utils;

import ar.ne.rcs.client.communication.ws.WSConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import lombok.extern.java.Log;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import java.util.function.Function;

@Log
public class SPAConnection extends WSConnection {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    ObjectMapper mapper = new ObjectMapper();
    private StompClient mStompClient;


    /**
     * @param uri the uri to connect, make sure it's a STOMP uri
     */
    public SPAConnection(String uri) {
        super(uri);
    }

    @Override
    public void connect() {
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, endpoint);
        Disposable lifecycleDisposable = mStompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            new Thread(onConnected).start();
                            log.info("Stomp connection opened");
                            break;
                        case ERROR:
                            new Thread(onError).start();
                            log.warning("Stomp connection error");
                            lifecycleEvent.getException().printStackTrace();
                            break;
                        case CLOSED:
                            new Thread(onError).start();
                            log.info("Stomp connection closed");
                            compositeDisposable.dispose();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            new Thread(onError).start();
                            log.warning("Stomp failed server heartbeat");
                            break;
                    }
                });
        compositeDisposable.add(lifecycleDisposable);
        mStompClient.connect();
    }

    @Override
    public void reconnect() {
        compositeDisposable.clear();
        mStompClient.disconnect();
    }

    @Override
    public <T> void subscribe(String dest, boolean toUser, Class<T> t, Function<T, Void> onEvent) {
        compositeDisposable.add(
                mStompClient
                        .topic(String.format("%s/topic/%s", toUser ? "/user" : "", dest))
                        .subscribe(stompMessage -> {
                            T dat = mapper.readValue(stompMessage.getPayload(), t);
                            onEvent.apply(dat);
                        })
        );
    }

    @Override
    public void send(String dest, Object data) {
        try {
            mStompClient.send(String.format("/topic/%s", dest), mapper.writeValueAsString(data)).blockingAwait();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
