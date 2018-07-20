package expressions_error.index_operator;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class IndexOperatorsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void alignmentExpressionError()
    {
        final String error = "alignment_expression_error.zs:5:7: expecting DECIMAL_LITERAL, found '@'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constantExpressionError()
    {
        final String error =
                "constant_expression_error.zs:3:29: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constraintExpressionError()
    {
        final String error =
                "constraint_expression_error.zs:5:32: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void defaultValueExpressionError()
    {
        final String error =
                "default_value_expression_error.zs:5:24: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumValueExpressionError()
    {
        final String error =
                "enum_value_expression_error.zs:5:15: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noArray()
    {
        final String error = "no_array_error.zs:6:10: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void optionalExpression()
    {
        final String error =
                "optional_expression_error.zs:5:25: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterNoArray()
    {
        final String error =
                "parameter_no_array_error.zs:10:20: Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
