package ch.mbruggmann.metricsriemann;

import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricsRegistry;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

/**
 * Simulates a host, emitting some metrics.
 */
public class SimulatedHost implements Runnable {


  private static final Random RAND = new Random(System.currentTimeMillis());

  private final ScheduledExecutorService executorService;
  private final int requestsPerSecond;
  private final int requestExecutionTime;
  private final int jitterPercent;

  private final MetricsRegistry metrics;
  private final Meter requests;
  private final Histogram executionTime;

  private ScheduledFuture<?> scheduledFuture;

  public SimulatedHost(final ScheduledExecutorService executorService, final MetricsRegistry metrics,
                       int requestsPerSecond, int requestExecutionTime, int jitterPercent) {
    this.executorService = executorService;
    this.metrics = metrics;
    this.requestsPerSecond = requestsPerSecond;
    this.requestExecutionTime = requestExecutionTime;
    this.jitterPercent = jitterPercent;

    requests = metrics.newMeter(SimulatedHost.class, "requests", "requests", TimeUnit.SECONDS);
    executionTime = metrics.newHistogram(SimulatedHost.class, "execution-time", "ms", false);
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
