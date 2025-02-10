package top.yangjianwu.monitor.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.prometheus.metrics.core.metrics.Gauge;
import top.yangjianwu.monitor.factory.BaseMonitorScheduleFactory;

public class IOMonitor implements Runnable {

  public static void init() {
    BaseMonitorScheduleFactory.INSTANCE.scheduleAtFixedRate(new IOMonitor(), 0, 5, TimeUnit.SECONDS);
  }

  private static final ProcessBuilder processBuilder = new ProcessBuilder("iostat", "-dx", "1", "1");

  private static final int headLine = 3;

  private static final Map<String, String> focus = Map.of(
      "w_await", "write_await",
      "r_await", "read_await",
      "%util", "usege_percent",
      "r/s", "read_count_persecond",
      "rkB/s", "read_kb_persecond",
      "w/s", "write_count_persecond",
      "wkB/s", "write_kb_persecond");

  private static final Map<Integer, String> indexMap = new HashMap<>();

  private static final Gauge io = Gauge.builder()
      .name("pin_io_stats")
      .labelNames("tag", "device")
      .register();

  @Override
  public void run() {

    try {
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      int cur = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        cur++;

        if (cur == headLine && indexMap.isEmpty()) {

          String[] tokens = line.split("\\s+");

          for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (focus.containsKey(token)) {
              indexMap.put(i, focus.get(token));
            }
          }
        }

        if (cur > headLine) {
          String[] tokens = line.split("\\s+");

          String device = tokens[0];

          for (int i = 0; i < tokens.length; i++) {
            if (indexMap.containsKey(i)) {
              io.labelValues(indexMap.get(i), device).set(Double.valueOf(tokens[i]));
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
