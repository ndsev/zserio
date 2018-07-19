package zserio.runtime.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationTimerTest
{
    @Test
    public void oneSecondTimer() throws InterruptedException
    {
        final ValidationTimer validationTimer = new ValidationTimer();
        validationTimer.start();
        Thread.sleep(1000);
        validationTimer.stop();
        final long minTolerance = 950;
        final long maxTolerance = 1050;
        assertTrue(validationTimer.getDuration() >= minTolerance);
        assertTrue(validationTimer.getDuration() <= maxTolerance);
    }
}
