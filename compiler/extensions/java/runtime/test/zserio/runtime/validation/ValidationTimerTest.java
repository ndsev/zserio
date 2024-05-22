package zserio.runtime.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ValidationTimerTest
{
    @Test
    public void oneSecondTimer() throws InterruptedException
    {
        final ValidationTimer validationTimer = new ValidationTimer();
        validationTimer.start();
        Thread.sleep(1000);
        validationTimer.stop();
        final long minTolerance = 750;
        final long maxTolerance = 1250;
        final long duration = validationTimer.getDuration();
        assertTrue(duration >= minTolerance, "Duration '" + duration + "' should be >= '" + minTolerance + "'");
        assertTrue(duration <= maxTolerance, "Duration '" + duration + "' should be <= '" + maxTolerance + "'");
    }
}
