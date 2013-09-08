package ch.mbruggmann.metricsriemann;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

  public static void main(String... args) throws InterruptedException {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    SimulatedHost host = new SimulatedHost(executorService, 100, 20, 20);
    host.start();
    Thread.sleep(3000);
    host.stop();
    executorService.shutdown();
  }

}
