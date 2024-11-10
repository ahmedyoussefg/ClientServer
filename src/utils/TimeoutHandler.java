package utils;

public class TimeoutHandler {
    // Base timeout in milliseconds
    private static final int BASE_TIMEOUT = 30000;
    
    // Minimum timeout allowed in milliseconds
    private static final int MIN_TIMEOUT = 15000;

    // ThreadGroup to keep track of active client threads
    private ThreadGroup threadGroup;
    public TimeoutHandler(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    /**
     * Calculates the timeout duration based on the number of active connections.
     * If only one connection is active, the base timeout is used.
     * If multiple connections are active, the timeout is reduced proportionally
     * to a minimum of MIN_TIMEOUT.
     * @return The calculated timeout duration in milliseconds.
     */
    public synchronized int calculateTimeout() {
        int activeConnections = this.threadGroup.activeCount();
        if (activeConnections <= 1) {
            return BASE_TIMEOUT;
        }
        return Math.max(BASE_TIMEOUT / activeConnections, MIN_TIMEOUT);
    }
}