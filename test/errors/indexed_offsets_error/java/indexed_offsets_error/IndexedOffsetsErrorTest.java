package indexed_offsets_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.function.Function;

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
    public void arithmeticAddOperatorInArrayExpression()
    {
        final String[] errors = {"arithmetic_add_operator_in_array_expression_error.zs:6:16: "
                        + "Arithmetic operators are not allowed in offset expressions!",
                "arithmetic_add_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void arithmeticMultOperatorInArrayExpression()
    {
        final String[] errors = {"arithmetic_mult_operator_in_array_expression_error.zs:6:16: "
                        + "Arithmetic operators are not allowed in offset expressions!",
                "arithmetic_mult_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void arrayInArrayExpression()
    {
        final String[] errors = {"array_in_array_expression_error.zs:7:14: "
                        + "Array expression is not allowed in offset array expressions!",
                "array_in_array_expression_error.zs:7:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void bitwiseAndOperatorInArrayExpression()
    {
        final String[] errors = {"bitwise_and_operator_in_array_expression_error.zs:6:16: "
                        + "Bitwise operators are not allowed in offset expressions!",
                "bitwise_and_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void bitwiseOrOperatorInArrayExpression()
    {
        final String[] errors = {"bitwise_or_operator_in_array_expression_error.zs:6:16: "
                        + "Bitwise operators are not allowed in offset expressions!",
                "bitwise_or_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void bitwiseXorOperatorInArrayExpression()
    {
        final String[] errors = {"bitwise_xor_operator_in_array_expression_error.zs:6:16: "
                        + "Bitwise operators are not allowed in offset expressions!",
                "bitwise_xor_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
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
    public void dotExpressionInArrayExpression()
    {
        final String[] errors = {"dot_expression_in_array_expression_error.zs:12:17: "
                        + "Dot expression is not allowed in offset array expressions!",
                "dot_expression_in_array_expression_error.zs:12:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void enumValueExpression()
    {
        final String error =
                "enum_value_expression_error.zs:5:14: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void equalityOperatorInArrayExpression()
    {
        final String[] errors = {"equality_operator_in_array_expression_error.zs:6:16: "
                        + "Relational operators are not allowed in offset expressions!",
                "equality_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void functionCallInArrayExpression()
    {
        final String[] errors = {"function_call_in_array_expression_error.zs:6:12: "
                        + "Function call is not allowed in offset expressions!",
                "function_call_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void id_in_array_expression()
    {
        final String[] errors = {"id_in_array_expression_error.zs:7:9: "
                        + "Identifiers are not allowed in offset array expressions!",
                "id_in_array_expression_error.zs:7:9: Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void isset_operator_in_array_expression()
    {
        final String[] errors = {"isset_in_array_expression_error.zs:13:9: "
                        + "Operator isset is not allowed in offset expressions!",
                "isset_in_array_expression_error.zs:13:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void lengthof_operator_in_array_expression()
    {
        final String[] errors = {"lengthof_in_array_expression_error.zs:6:9: "
                        + "Operator lengthof is not allowed in offset expressions!",
                "lengthof_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void literal_in_array_expression()
    {
        final String[] errors = {"literal_in_array_expression_error.zs:17:35: "
                        + "Literals are not allowed in offset expressions!",
                "literal_in_array_expression_error.zs:17:35: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void logic_and_operator_in_array_expression()
    {
        final String[] errors = {"logical_and_operator_in_array_expression_error.zs:6:16: "
                        + "Logical operators are not allowed in offset expressions!",
                "logical_and_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void logic_or_operator_in_array_expression()
    {
        final String[] errors = {"logical_or_operator_in_array_expression_error.zs:6:16: "
                        + "Logical operators are not allowed in offset expressions!",
                "logical_or_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void multipleIndexOperators()
    {
        final String error = "multiple_index_operators_error.zs:11:25: "
                + "Index operator can be used only once within an offset expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noArray()
    {
        final String error = "no_array_error.zs:6:9: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void numbits_in_array_expression()
    {
        final String[] errors = {"numbits_in_array_expression_error.zs:7:9: "
                        + "Operator numbits is not allowed in offset expressions!",
                "numbits_in_array_expression_error.zs:7:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
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
        final String error = "packed_complex_offset_array_error.zs:12:1: "
                + "Packed array cannot be used as offset array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void packedParamOffsetArray()
    {
        final String error = "packed_param_offset_array_error.zs:10:1: "
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
    public void parenthesis_in_array_expression()
    {
        final String[] errors = {"parenthesis_in_array_expression_error.zs:6:9: "
                        + "Parenthesis are not allowed in offset expressions!",
                "parenthesis_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void relational_operator_in_array_expression()
    {
        final String[] errors = {"relational_operator_in_array_expression_error.zs:6:16: "
                        + "Relational operators are not allowed in offset expressions!",
                "relational_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void shift_operator_in_array_expression()
    {
        final String[] errors = {"shift_operator_in_array_expression_error.zs:6:16: "
                        + "Shift operators are not allowed in offset expressions!",
                "shift_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void ternary_operator_in_array_expression()
    {
        final String[] errors = {"ternary_operator_in_array_expression_error.zs:6:20: "
                        + "Ternary operator is not allowed in offset expressions!",
                "ternary_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void unary_operator_in_array_expression()
    {
        final String[] errors = {"unary_operator_in_array_expression_error.zs:6:9: "
                        + "Unary operators are not allowed in offset expressions!",
                "unary_operator_in_array_expression_error.zs:6:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void valueof_in_array_expression()
    {
        final String[] errors = {"valueof_in_array_expression_error.zs:12:9: "
                        + "Operator valueof is not allowed in offset expressions!",
                "valueof_in_array_expression_error.zs:12:9: "
                        + "Only @index is allowed in offset array expressions!"};
        assertTrue(zserioErrors.isPresent(errors));
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
