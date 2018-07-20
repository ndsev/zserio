package file_encoding_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class FileEncodingWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void nonUtf8Characters()
    {
        final String warning = "Found non-UTF8 encoded characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void tabCharacters()
    {
        final String warning = "Found tab characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void nonPrintableAsciiCharacters()
    {
        final String warning = "Found non-printable ASCII characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 3;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
