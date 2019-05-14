package constraints_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class ConstraintsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void fieldInFunctionNotAvailableError()
    {
        final String error = "field_in_function_not_available_error.zs:17:16: Unresolved symbol " +
                "'specialMinValue' within expression scope! Found in function 'checkSpecial' called from here:";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldNotAvailableError()
    {
        final String error = "field_not_available_error.zs:7:41: Unresolved symbol 'specialMinValue' " +
            "within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noneBooleanExpression()
    {
        final String error = "none_boolean_expression_error.zs:6:35: Constraint expression for field " +
                "'constraintValue' is not boolean!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
