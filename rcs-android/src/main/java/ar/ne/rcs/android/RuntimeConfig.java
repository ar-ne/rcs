package ar.ne.rcs.android;

import android.app.Activity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class RuntimeConfig {
    private static final HashSet<String> pwd;
    private static final AtomicBoolean deviceLocked = new AtomicBoolean(false);
    public static Activity activity;

    static {
        pwd = new HashSet<>(Arrays.asList("18998118989", "3545928261", "1174601828"));
    }

    public static boolean isLocked() {
        return deviceLocked.get();
    }

    public static void setLocked(boolean locked) {
        deviceLocked.set(locked);
    }

    public static HashSet<String> getPwd() {
        return pwd;
    }
}