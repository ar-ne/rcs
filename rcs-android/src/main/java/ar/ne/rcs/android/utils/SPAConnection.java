package ar.ne.rcs.android.utils;

import ar.ne.rcs.client.communication.ws.WSConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import java.util.function.Function;

@Log
public class SPAConnection extends WSConnection {
    static {
        io.reactivex.plugins.RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
        io.reactivex.rxjava3.plugins.RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }

    private final CompositeDisposable compositeDisposable;
    private final StompClient mStompClient;
    ObjectMapper mapper = new ObjectMapper();

    /**
     * @param uri the uri to connect, make sure it's a STOMP uri
     */
    public SPAConnection(String uri) {
        super(uri);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, endpoint);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void connect() {
        Disposable lifecycleDisposable = mStompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    log.info("Received lifecycle event: " + lifecycleEvent.getType());
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            log.info("Stomp connection opened");
                            new Thread(onConnected).start();
                            break;
                        case CLOSED:
                            log.info("Stomp connection closed");
                            Thread.sleep(100000);
                            new Thread(onError).start();
                            break;
                    }
                });
        compositeDisposable.add(lifecycleDisposable);
        mStompClient.connect();
    }

    @SneakyThrows
    @Override
    public void reconnect() {
        compositeDisposable.clear();

        log.info("Reconnecting...currently client:" + mStompClient.hashCode());
        connect();
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
            if (data.getClass().equals(String.class))
                mStompClient.send(String.format("/topic/%s", dest), (String) data).blockingAwait();
            else
                mStompClient.send(String.format("/topic/%s", dest), mapper.writeValueAsString(data)).doOnError(throwable -> onError.run()).blockingAwait();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
