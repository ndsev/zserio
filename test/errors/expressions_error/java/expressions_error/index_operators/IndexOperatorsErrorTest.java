package expressions_error.index_operators;

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
    public void alignmentExpression()
    {
        final String error = "alignment_expression_error.zs:5:7: " +
                "mismatched input '@index' expecting DECIMAL_LITERAL ('@index' is a reserved keyword)!";
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
    public void parameterNoArray()
    {
        final String error = "parameter_no_array_error.zs:10:19: " +
                "Index operator is not allowed in this context!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
