package offsets_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class OffsetsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void arithmeticAddOperatorError()
    {
        final String error = "arithmetic_add_operator_error.zs:6:8: "
                + "Arithmetic operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void arithmeticMultOperatorError()
    {
        final String error = "arithmetic_mult_operator_error.zs:6:8: "
                + "Arithmetic operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void arrayNoIndex()
    {
        final String error = "array_no_index_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitwiseAndOperatorError()
    {
        final String error = "bitwise_and_operator_error.zs:6:8: "
                + "Bitwise operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitwiseOrOperatorError()
    {
        final String error = "bitwise_or_operator_error.zs:6:8: "
                + "Bitwise operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void bitwiseXorOperatorError()
    {
        final String error = "bitwise_xor_operator_error.zs:6:8: "
                + "Bitwise operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void builtinTypeParameter()
    {
        final String error = "builtin_type_parameter_error.zs:6:1: "
                + "Built-in type parameter 'param' cannot be used as an offset!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constant()
    {
        final String error = "constant_error.zs:8:1: Constant 'CONST' cannot be used as an offset!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedOffsetField()
    {
        final String[] errors = {
                "duplicated_offset_field_error.zs:12:1:     First used here!",
                "duplicated_offset_field_error.zs:16:1: Duplicated offset expression!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void duplicatedOffsetViaParameter()
    {
        final String[] errors = {
                "duplicated_offset_via_parameter_error.zs:11:1:     First used here!",
                "duplicated_offset_via_parameter_error.zs:15:1: Duplicated offset expression!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void equalityOperator()
    {
        final String error = "equality_operator_error.zs:6:8: "
                + "Relational operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void floatError()
    {
        final String error = "float_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void functionError()
    {
        final String error = "function_error.zs:12:15: Function call is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void indexError()
    {
        final String error = "index_error.zs:5:1: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void issetError()
    {
        final String error = "isset_error.zs:12:1: Operator isset is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void lengthofError()
    {
        final String error = "lengthof_error.zs:6:1: Operator lengthof is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void literalError()
    {
        final String error = "literal_error.zs:5:1: Literals are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void logicalAndOperatorError()
    {
        final String error = "logical_and_operator_error.zs:6:8: "
                + "Logical operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void logicalOrOperatorError()
    {
        final String error = "logical_and_operator_error.zs:6:8: "
                + "Logical operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void numbitsError()
    {
        final String error = "numbits_error.zs:7:1: Operator numbits is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parenthesisError()
    {

        final String error = "parenthesis_error.zs:6:1: Parenthesis are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void relationalOperatorError()
    {

        final String error = "relational_operator_error.zs:6:8: "
                + "Relational operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void shiftOperatorError()
    {

        final String error = "shift_operator_error.zs:6:8: "
                + "Shift operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void signed_bitfield()
    {
        final String error = "signed_bitfield_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void signed_integer()
    {
        final String error = "signed_integer_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void string()
    {
        final String error = "string_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryOperatorError()
    {
        final String error = "ternary_operator_error.zs:8:19: "
                + "Ternary operator is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unaryOperatorError()
    {
        final String error = "unary_operator_error.zs:6:1: "
                + "Unary operators are not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void valueofError()
    {
        final String error = "valueof_error.zs:11:1: Operator valueof is not allowed in offset expressions!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void varint()
    {
        final String error = "varint_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void varuint()
    {
        final String error = "varuint_error.zs:6:1: "
                + "Offset expression for field 'values' is not an unsigned fixed sized integer type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
