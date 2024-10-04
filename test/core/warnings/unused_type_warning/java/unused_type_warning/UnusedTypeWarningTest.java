package unused_type_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class UnusedTypeWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void unusedEnumeration()
    {
        final String warning = "unused_type_warning.zs:4:12: "
                + "Type 'unused_type_warning.UnusedEnumeration' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedSubtype()
    {
        final String warning = "unused_type_warning.zs:18:15: "
                + "Type 'unused_type_warning.UnusedSubtype' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedChoice()
    {
        final String warning = "unused_type_warning.zs:28:8: "
                + "Type 'unused_type_warning.UnusedChoice' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedUnion()
    {
        final String warning = "unused_type_warning.zs:48:7: "
                + "Type 'unused_type_warning.UnusedUnion' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedStructure()
    {
        final String warning = "unused_type_warning.zs:62:8: "
                + "Type 'unused_type_warning.UnusedStructure' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedTable()
    {
        final String warning = "unused_type_warning.zs:76:11: "
                + "Type 'unused_type_warning.UnusedTable' is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
