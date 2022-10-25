package functions_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class TemplatesWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void defaultInstantiation()
    {
        final String warning1 = "default_instantiation_warning.zs:15:5: " +
                "Default instantiation of 'Template' as 'Template_uint32.";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning2 = "default_instantiation_warning.zs:17:5: " +
                "Default instantiation of 'Subpackage1Template' as 'Subpackage1Template_string.";
        assertTrue(zserioWarnings.isPresent(warning2));

        final String warning3 = "default_instantiation_warning.zs:19:5: " +
                "Default instantiation of 'Subpackage2Template' as 'Subpackage2Template_string.";
        assertTrue(zserioWarnings.isPresent(warning3));

        final String warnings4[] =
        {
            "default_instantiation_warning.zs:20:5: " +
                    "    In instantiation of 'Subpackage3Template' required from here",
            "default_instantiation_subpackage3.zs:10:5: " +
                    "Default instantiation of 'Subpackage3InnerTemplate' as 'Subpackage3InnerTemplate_uint32."
        };
        assertTrue(zserioWarnings.isPresent(warnings4));
    }

    private static ZserioErrorOutput zserioWarnings;
}
