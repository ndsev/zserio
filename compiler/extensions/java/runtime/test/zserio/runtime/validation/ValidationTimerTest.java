package zserio.runtime.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ValidationTimerTest
{
    @Test
    public void tenSecondsTimer() throws InterruptedException
    {
        final ValidationTimer validationTimer = new ValidationTimer();
        validationTimer.start();
        Thread.sleep(10000);
        validationTimer.stop();
        final long minTolerance = 9000;
        final long maxTolerance = 11000;
        final long duration = validationTimer.getDuration();
        assertTrue(duration >= minTolerance, "Duration '" + duration + "' should be >= '" + minTolerance + "'");
        assertTrue(duration <= maxTolerance, "Duration '" + duration + "' should be <= '" + maxTolerance + "'");
    }
}
