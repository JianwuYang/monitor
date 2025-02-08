package top.yangjianwu.monitor.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum BaseMonitorScheduleFactory {

    INSTANCE;

    private final ScheduledExecutorService scheduler;

    BaseMonitorScheduleFactory() {
        this.scheduler = Executors.newScheduledThreadPool(4);
    }

    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

}
