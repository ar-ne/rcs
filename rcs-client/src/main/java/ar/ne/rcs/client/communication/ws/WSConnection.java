package ar.ne.rcs.client.communication.ws;

import java.util.function.Function;

public abstract class WSConnection {
    protected String endpoint;
    protected Runnable onConnected;
    protected Runnable onError;

    /**
     * @param uri the uri to connect, make sure it's a STOMP uri
     */
    public WSConnection(String uri) {
        this.endpoint = uri;
    }

    /**
     * @param onConnected callback when connected
     * @param onError     callback when anything happens
     */
    public void connect(Runnable onConnected, Runnable onError) {
        this.onConnected = onConnected;
        this.onError = onError;
        this.connect();
    }

    public abstract void connect();

    public abstract void reconnect();

    public abstract <T> void subscribe(String dest, boolean toUser, Class<T> t, Function<T, Void> onEvent);

    public abstract void send(String dest, Object data);
}
