package offsets_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class OffsetsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void arrayNoIndex()
    {
        final String error = "array_no_index_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void builtinTypeParameter()
    {
        final String error = "builtin_type_parameter_error.zs:6:1: " +
                "Built-in type parameter 'param' cannot be used as an offset!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constant()
    {
        final String error = "constant_error.zs:8:1: Constant 'CONST' cannot be used as an offset!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void floatError()
    {
        final String error = "float_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void functionError()
    {
        final String error = "function_error.zs:12:1: Function call cannot be used in offset expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void signed_bitfield()
    {
        final String error = "signed_bitfield_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void signed_integer()
    {
        final String error = "signed_integer_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void string()
    {
        final String error = "string_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void varint()
    {
        final String error = "varint_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void varuint()
    {
        final String error = "varuint_error.zs:6:1: " +
                "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
