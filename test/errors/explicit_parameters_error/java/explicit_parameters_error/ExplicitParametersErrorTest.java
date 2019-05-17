package explicit_parameters_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ExplicitParametersErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void differentBuiltinType()
    {
        final String error = "different_builtin_type_error.zs:20:15: " +
                "Type of explicit parameter 'count' resolved to 'uint8' but first used as 'uint32' at 18:10!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void differentCompoundType()
    {
        final String error = "different_compound_type_error.zs:28:15: " +
                "Type of explicit parameter 'header' resolved to 'different_compound_type_error.OtherHeader' " +
                "but first used as 'different_compound_type_error.Header' at 26:10!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void notAllowed()
    {
        final String error = "not_allowed_error.zs:15:10: Explicit keyword is allowed only in SQL tables!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
