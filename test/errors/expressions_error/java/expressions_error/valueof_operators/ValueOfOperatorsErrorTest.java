package expressions_error.valueof_operators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class ValueOfOperatorsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void arrayType()
    {
        final String error = "array_type_error.zs:6:30: 'offsets' is not an enumeration or bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumType()
    {
        final String error = "enum_type_error.zs:6:30: 'Colour' is not an enumeration or bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void integerType()
    {
        final String error = "integer_type_error.zs:6:30: 'value' is not an enumeration or bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
