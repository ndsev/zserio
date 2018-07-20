package unused_type_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class UnusedTypeWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void unusedEnumeration()
    {
        final String warning = ":4:1: Type unused_type_warning.UnusedEnumeration is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedSubtype()
    {
        final String warning = ":18:1: Type unused_type_warning.UnusedSubtype is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedChoice()
    {
        final String warning = ":28:1: Type unused_type_warning.UnusedChoice is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedUnion()
    {
        final String warning = ":48:1: Type unused_type_warning.UnusedUnion is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedStructure()
    {
        final String warning = ":62:1: Type unused_type_warning.UnusedStructure is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedTable()
    {
        final String warning = ":76:1: Type unused_type_warning.UnusedTable is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 6;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
