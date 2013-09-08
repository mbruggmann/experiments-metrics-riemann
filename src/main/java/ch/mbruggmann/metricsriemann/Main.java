package ch.mbruggmann.metricsriemann;

import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.RiemannReporter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String... args) throws InterruptedException, IOException {
    final MetricsRegistry metrics = new MetricsRegistry();

    RiemannReporter.Config reporterConfig = RiemannReporter.Config.newBuilder()
        .metricsRegistry(metrics)
        .localHost("localhost")
        .port(5555)
        .period(2)
        .unit(TimeUnit.SECONDS)
        .printVMMetrics(false).build();
    RiemannReporter.enable(reporterConfig);

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    SimulatedHost host = new SimulatedHost(executorService, metrics, 100, 20, 20);
    host.start();

    Thread.sleep(5*60*1000);

    host.stop();
    metrics.shutdown();
    executorService.shutdown();
  }

}
