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

//    @Test
//    @SuppressWarnings("unchecked")
//    public void clientTest() throws InterruptedException {
//        RCSAndroidManager manager = new RCSAndroidManager(
//                RCSAndroidConfigModel.builder()
//                        .communicationConfig(
//                                CommunicationConfigModel.builder()
//                                        .deviceID("deviceId")
//                                        .host("localhost")
//                                        .port(8080)
//                                        .httpEndpoint("/")
//                                        .wsEndpoint("ws/websocket")
//                                        .httpSSL(false)
//                                        .wsSSL(false)
//                                        .build()
//                        )
//                        .build()
//        );
//        manager.enableFeature(RemoteShell.class,);
//    }
}