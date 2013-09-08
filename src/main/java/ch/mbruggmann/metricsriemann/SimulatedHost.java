package ch.mbruggmann.metricsriemann;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Preconditions.checkState;

/**
 * Simulates a host, emitting some metrics.
 */
public class SimulatedHost implements Runnable {

  private static final MetricRegistry METRICS = new MetricRegistry();
  private static final Random RAND = new Random(System.currentTimeMillis());

  private final ScheduledExecutorService executorService;
  private final int requestsPerSecond;
  private final int requestExecutionTime;
  private final int jitterPercent;

  private final Meter requests = METRICS.meter(name(SimulatedHost.class, "requests"));
  private final Histogram executionTime = METRICS.histogram(name(SimulatedHost.class, "execution-time"));

  private ScheduledFuture<?> scheduledFuture;

  public SimulatedHost(ScheduledExecutorService executorService, int requestsPerSecond, int requestExecutionTime, int jitterPercent) {
    this.executorService = executorService;
    this.requestsPerSecond = requestsPerSecond;
    this.requestExecutionTime = requestExecutionTime;
    this.jitterPercent = jitterPercent;
  }

  @Override
  public void run() {
    for (int i=0; i<requestsPerSecond; i++) {
      requests.mark();
      executionTime.update(jitteredExecutionTime());
    }
  }

  public void start() {
    checkState(scheduledFuture == null, "already running");
    scheduledFuture = executorService.schedule(this, 1, TimeUnit.SECONDS);
  }

  public void stop() {
    checkState(scheduledFuture != null, "not running");
    scheduledFuture.cancel(false);
  }

  protected int jitteredExecutionTime() {
    double range = 0.01 * jitterPercent * requestExecutionTime;
    double jitter = (range * RAND.nextFloat()) - (range/2);
    return Math.max(requestExecutionTime + (int) jitter, 0);
  }
}
