package nl.avans.wordcrex.util;

public class Console {
    public static Runnable log(String log) {
        return () -> System.out.println(log);
    }
}
