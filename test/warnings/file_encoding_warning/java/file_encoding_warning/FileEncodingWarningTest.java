package file_encoding_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class FileEncodingWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void nonUtf8Characters()
    {
        final String warning = "file_encoding_warning.zs:1:1: Found non-UTF8 encoded characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void tabCharacters()
    {
        final String warning = "file_encoding_warning.zs:1:1: Found tab characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void nonPrintableAsciiCharacters()
    {
        final String warning = "file_encoding_warning.zs:1:1: Found non-printable ASCII characters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
