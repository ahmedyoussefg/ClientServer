package utils;

public class TimeoutHandler {
    // Base timeout in milliseconds
    private static final int BASE_TIMEOUT = 7000;
    // Minimum timeout allowed in milliseconds
    private static final int MIN_TIMEOUT = 1000;
    private ThreadGroup threadGroup;
    public TimeoutHandler(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }
    public synchronized int calculateTimeout() {
        int activeConnections = this.threadGroup.activeCount();
        if (activeConnections <= 1) {
            return BASE_TIMEOUT;
        }
        return Math.max(BASE_TIMEOUT / activeConnections, MIN_TIMEOUT);
    }
}