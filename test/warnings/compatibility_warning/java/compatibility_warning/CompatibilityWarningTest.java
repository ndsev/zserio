package functions_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class CompatibilityWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void rootWithoutCompatibility()
    {
        final String warning = "subpackage.zs:1:30: " +
                "Package compatibility version '2.4.2' doesn't match to '2.5.0' specified in root package!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void rootWithDiffCompatibility()
    {
        final String warning = "subpackage.zs:1:30: " +
                "Package specifies compatibility version '2.4.2' while root package specifies nothing!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
