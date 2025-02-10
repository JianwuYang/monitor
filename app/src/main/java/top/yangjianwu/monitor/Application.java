package top.yangjianwu.monitor;

import java.io.IOException;

import top.yangjianwu.monitor.base.CPUMonitor;
import top.yangjianwu.monitor.base.DiskMonitor;
import top.yangjianwu.monitor.base.IOMonitor;
import top.yangjianwu.monitor.base.MemoryMonitor;

import io.prometheus.metrics.exporter.httpserver.HTTPServer;

public class Application {

  public static void main(String[] args) throws IOException {

    initMonitor();

    HTTPServer server = HTTPServer.builder()
        .port(9400)
        .buildAndStart();

    System.out.println("HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");
  }

  private static void initMonitor() {
    CPUMonitor.init();
    MemoryMonitor.init();
    DiskMonitor.init();
    IOMonitor.init();
  }
}
