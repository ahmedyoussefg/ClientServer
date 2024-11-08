package utils;

public class TimeoutHandler {
    // Base timeout in milliseconds
    private static final int BASE_TIMEOUT = 7000;
    // Minimum timeout allowed in milliseconds
    private static final int MIN_TIMEOUT = 1000;

    public synchronized static int calculateTimeout() {
        int activeConnections = Thread.activeCount() - 1;
        if (activeConnections <= 1) {
            return BASE_TIMEOUT;
        }
        return Math.max(BASE_TIMEOUT / activeConnections, MIN_TIMEOUT);
    }
}