package top.yangjianwu.monitor.base;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import com.sun.management.OperatingSystemMXBean;
import top.yangjianwu.monitor.factory.BaseMonitorScheduleFactory;

import io.prometheus.metrics.core.metrics.Gauge;

public class CPUMonitor implements Runnable {

    private static final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    private static final Gauge cpu = Gauge.builder()
            .name("my_cpu_useage")
            .register();

    public static void init() {
        BaseMonitorScheduleFactory.INSTANCE.scheduleAtFixedRate(new CPUMonitor(), 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        double cpuLoad = systemMXBean.getCpuLoad();
        cpu.set(cpuLoad);
    }

}
