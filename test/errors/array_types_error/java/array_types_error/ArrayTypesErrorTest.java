package array_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ArrayTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void arrayLengthNotAvailable()
    {
        final String error = "array_length_field_not_available_error.zs:7:18: " +
                "Unresolved symbol 'array2Size' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayNotLast()
    {
        final String error = "implicit_array_not_last_error.zs:5:5: Implicit array must be defined at the " +
                "end of structure!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayWithLength()
    {
        final String error = "implicit_array_with_length_error.zs:6:27: Length expression is not allowed " +
                "for implicit arrays!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongArrayLengthType()
    {
        final String error = "wrong_array_length_type_error.zs:6:21: Invalid length expression for array. " +
                "Length must be integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
