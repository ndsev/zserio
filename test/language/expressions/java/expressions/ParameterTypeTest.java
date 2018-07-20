package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.parameter_type.Color;
import expressions.parameter_type.ParameterTypeExpression;

public class ParameterTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final ParameterTypeExpression parameterTypeExpression = new ParameterTypeExpression(Color.RED,
                PARAMETER_TYPE_EXPRESSION_VALUE, true);

        assertEquals(PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, parameterTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final ParameterTypeExpression parameterTypeExpression = new ParameterTypeExpression(Color.BLUE);
        parameterTypeExpression.setValue(PARAMETER_TYPE_EXPRESSION_VALUE);

        assertEquals(PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                parameterTypeExpression.bitSizeOf());
    }

    private static final byte PARAMETER_TYPE_EXPRESSION_VALUE = (byte)0x7B;

    private static final int PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8;
    private static final int PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7;
}
