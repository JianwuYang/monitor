package top.yangjianwu.monitor.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import io.prometheus.metrics.core.metrics.Gauge;
import top.yangjianwu.monitor.factory.BaseMonitorScheduleFactory;

public class ContainerOverviewMonitor implements Runnable {

    public static void init() {
        BaseMonitorScheduleFactory.INSTANCE.scheduleAtFixedRate(new ContainerOverviewMonitor(), 0, 10,
                TimeUnit.MINUTES);
    }

    private static final ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "-a");

    // 0, "container_id"
    // 1, "image"
    // 2, "command"
    // 3, "created"
    // 4, "status"
    // 5, "ports"
    // 6, "names"

    private static final Gauge overview = Gauge.builder()
            .name("my_container_overview")
            .register();

    @Override
    public void run() {
        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 将首行读取丢弃
            reader.readLine();

            String line;

            int all = 0;
            int running = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    if (i == 4) {
                        all++;
                        String token = tokens[i];
                        if (token.contains("UP")) {
                            running++;
                        }
                    }
                }
            }

            overview.set(all);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}