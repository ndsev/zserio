package expressions_error.valueof_operators;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ValueOfOperatorsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
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

    private static ZserioErrors zserioErrors;
}
