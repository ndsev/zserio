package default_values_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class DefaultValuesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void arrayInitializerError()
    {
        final String error = "array_initializer_error.zs:5:24: Wrong type of value expression "
                + "(integer cannot be assigned to array type)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldInitializerError()
    {
        final String error = "field_initializer_error.zs:6:32: Initializer must be a constant expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void rangeDecimalInitializerError()
    {
        final String error = "range_decimal_initializer_error.zs:5:39: Initializer value '57005' of "
                + "'wrongDecimalInitializer' exceeds the bounds of its type 'int8'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongBoolInitializer()
    {
        final String error = "wrong_bool_initializer_error.zs:5:36: Wrong type of value expression "
                + "(integer cannot be assigned to bool)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongDecimalInitializer()
    {
        final String error = "wrong_decimal_initializer_error.zs:5:39: Wrong type of value expression "
                + "(boolean cannot be assigned to int32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFloatInitializer()
    {
        final String error = "wrong_float_initializer_error.zs:5:37: Wrong type of value expression "
                + "(string cannot be assigned to float64)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongStringInitializer()
    {
        final String error = "wrong_string_initializer_error.zs:5:38: Wrong type of value expression "
                + "(boolean cannot be assigned to string)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
