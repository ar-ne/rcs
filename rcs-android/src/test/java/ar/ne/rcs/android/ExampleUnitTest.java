package ar.ne.rcs.android;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void wsClientTest() throws InterruptedException {
//        synchronized (this) {
//            WSClient client = WSClient.init(new SPAConnection("ws://127.0.0.1:8080/ws/websocket"));
//            this.wait();
//        }
    }
}