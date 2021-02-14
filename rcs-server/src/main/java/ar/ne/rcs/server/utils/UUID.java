package ar.ne.rcs.server.utils;

public class UUID {
    public static String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
