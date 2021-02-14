package ar.ne.rcs.client.communication.ws;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Function;

public abstract class WSConnection {
    private static final ObjectMapper mapper = new ObjectMapper();

    protected String endpoint;
    protected Runnable onConnected;
    protected Runnable onError;

    /**
     * @param endpoint the endpoint to connect, make sure it's a STOMP endpoint
     */
    public WSConnection(String endpoint) {
        this.endpoint = endpoint;
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
