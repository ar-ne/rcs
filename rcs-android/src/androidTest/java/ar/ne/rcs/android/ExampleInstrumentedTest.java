package ar.ne.rcs.android;

import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.telephony.TelephonyManager;
import ar.ne.rcs.android.functions.PkgManager;
import ar.ne.rcs.android.utils.DeviceIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ar.ne.rcs.test", appContext.getPackageName());
    }

    @Test
    public void iTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TelephonyManager telephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);

        AndroidFeatureManager.init(appContext.getApplicationContext());
    }


    @SuppressWarnings("deprecation")
    @Test
    public void serialTest() {
        iTest();
        System.out.println(DeviceIdentifier.getIdentifier());
        System.out.println(Build.SERIAL);
    }

    @Test
    public void installTest(){
        iTest();
        PkgManager.install(new File("/data/local/tmp/2_wxt_classBrand.apk"));
    }
}