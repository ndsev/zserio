package not_handled_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class NotHandledWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void notHandledWhite()
    {
        final String warning = "not_handled_warning.zs:15:8: " +
                "Enumeration value 'WHITE' is not handled in choice 'EnumParamChoice'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notHandledRed()
    {
        final String warning = "not_handled_warning.zs:15:8: " +
                "Enumeration value 'RED' is not handled in choice 'EnumParamChoice'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 2;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
