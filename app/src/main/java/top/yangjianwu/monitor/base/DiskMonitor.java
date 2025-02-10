package top.yangjianwu.monitor.base;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import io.prometheus.metrics.core.metrics.Gauge;
import top.yangjianwu.monitor.factory.BaseMonitorScheduleFactory;

public class DiskMonitor implements Runnable {

  private static final Gauge disk = Gauge.builder()
      .name("pin_disk_useage")
      .labelNames("dir")
      .register();

  public static void init() {
    BaseMonitorScheduleFactory.INSTANCE.scheduleAtFixedRate(new DiskMonitor(), 0, 1, TimeUnit.HOURS);
  }

  @Override
  public void run() {

    for (Path root : FileSystems.getDefault().getRootDirectories()) {
      try {
        FileStore store = Files.getFileStore(root);

        long totalSpace = store.getTotalSpace();
        long usableSpace = store.getUsableSpace();

        double percent = usableSpace * 1.0 / totalSpace;

        disk.labelValues(root.toString()).set(1 - percent);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
