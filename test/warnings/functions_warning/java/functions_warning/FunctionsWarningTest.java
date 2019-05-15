package functions_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class FunctionsWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void unconditionalAutoOptionalFields()
    {
        final String warning = "unconditional_auto_optional_fields_warning.zs:10:16: Function " +
                "'suspicionFunction' contains unconditional optional fields.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unconditionalOptionalFields()
    {
        final String warning = "unconditional_optional_fields_warning.zs:10:16: Function " +
                "'suspicionFunction' contains unconditional optional fields.";
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
