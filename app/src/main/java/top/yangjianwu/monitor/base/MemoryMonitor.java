package top.yangjianwu.monitor.base;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import com.sun.management.OperatingSystemMXBean;
import top.yangjianwu.monitor.factory.BaseMonitorScheduleFactory;

import io.prometheus.metrics.core.metrics.Gauge;

public class MemoryMonitor implements Runnable {

    private static final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    private static final Gauge memory = Gauge.builder()
            .name("my_memory_useage")
            .register();

    public static void init() {
        BaseMonitorScheduleFactory.INSTANCE.scheduleAtFixedRate(new MemoryMonitor(), 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        double totalMemory = systemMXBean.getTotalMemorySize();
        double freeMemory = systemMXBean.getFreeMemorySize();
        double percent = freeMemory / totalMemory;
        memory.set(1 - percent);
    }

}
