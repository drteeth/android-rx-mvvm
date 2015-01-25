package ca.unspace.timer.models;

public class Timer {

    private Long start;
    private Long end;
    private boolean isRunning;

    public Boolean isRunning() {
        return isRunning;
    }

    public long getMillis() {
        if (start != null) {
            if (end == null) {
                return System.currentTimeMillis() - start;
            } else {
                return end - start;
            }
        }
        return 0;
    }

    public boolean start() {
        start = System.currentTimeMillis();
        end = null;
        simulateWork();
        isRunning = true;
        return true;
    }

    public boolean stop() {
        end = System.currentTimeMillis();
        simulateWork();
        isRunning = false;
        return false;
    }

    private void simulateWork() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
