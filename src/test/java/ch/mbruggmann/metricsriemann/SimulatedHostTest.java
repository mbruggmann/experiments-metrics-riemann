package ch.mbruggmann.metricsriemann;

import com.yammer.metrics.core.MetricsRegistry;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SimulatedHostTest {

  @Test
  public void testJitterRange() {
    final int requestExecutionTime = 100;
    final int jitterPercent = 10;
    SimulatedHost host = new SimulatedHost(
        mock(ScheduledExecutorService.class), mock(MetricsRegistry.class), 500, requestExecutionTime, jitterPercent);

    int smaller = 0;
    int bigger = 0;
    for (int i=0; i<100; i++) {
      int jitteredTime = host.jitteredExecutionTime();
      assertTrue(jitteredTime >= 95);
      assertTrue(jitteredTime <= 105);
      if (jitteredTime < requestExecutionTime)
        smaller++;
      else if (jitteredTime > requestExecutionTime)
        bigger++;
    }

    assertTrue(smaller > 0);
    assertTrue(bigger > 0);
  }

}
