package indexed_offsets_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class IndexedOffsetsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void alignmentExpression()
    {
        final String error =
                "alignment_expression_error.zs:5:7: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constantExpression()
    {
        final String error =
                "constant_expression_error.zs:3:28: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constraintExpression()
    {
        final String error =
                "constraint_expression_error.zs:5:31: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void defaultValueExpression()
    {
        final String error =
                "default_value_expression_error.zs:5:23: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumValueExpression()
    {
        final String error =
                "enum_value_expression_error.zs:5:14: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noArray()
    {
        final String error = "no_array_error.zs:6:9: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void optionalExpression()
    {
        final String error =
                "optional_expression_error.zs:5:24: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void packedComplexOffsetArray()
    {
        final String error = "packed_complex_offset_array_error.zs:19:1: "
                + "Packed array cannot be used as offset array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void packedParamOffsetArray()
    {
        final String error = "packed_param_offset_array_error.zs:18:1: "
                + "Packed array cannot be used as offset array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void packedSimpleOffsetArray()
    {
        final String error = "packed_simple_offset_array_error.zs:7:1: "
                + "Packed array cannot be used as offset array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterNoArray()
    {
        final String error = "parameter_no_array_error.zs:10:19: "
                + "Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void varuintOffsetArray()
    {
        final String error = "varuint_offset_array_error.zs:6:1: "
                + "Offset expression for field 'fields' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
