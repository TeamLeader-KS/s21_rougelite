package rougelite.PresentationLayer.ui;

public class Messages {
    private static String currentMessage = null;
    private static long expireTime = 0;

    public static void show(String message, int durationMs) {
        currentMessage = message;
        expireTime = System.currentTimeMillis() + durationMs;
    }

    public static String getCurrent() {
        if (currentMessage != null && System.currentTimeMillis() < expireTime) {
            return currentMessage;
        }
        return null;
    }

    public static void clear() {
        currentMessage = null;
        expireTime = 0;
    }
}