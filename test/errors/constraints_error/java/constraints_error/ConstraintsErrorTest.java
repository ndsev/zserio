package constraints_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.util.Arrays;

import test_utils.ZserioErrorOutput;

public class ConstraintsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void fieldInFunctionNotAvailableError()
    {
        final String errors[] =
        {
            "field_in_function_not_available_error.zs:7:26:     In function 'checkSpecial' called from here",
            "field_in_function_not_available_error.zs:17:31: " +
                    "Unresolved symbol 'specialMinValue' within expression scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
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

    private static ZserioErrorOutput zserioErrors;
}
