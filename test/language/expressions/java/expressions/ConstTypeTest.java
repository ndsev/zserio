package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.const_type.ConstTypeExpression;

public class ConstTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final ConstTypeExpression constTypeExpression = new ConstTypeExpression(VALID_VALUE, ADDITIONAL_VALUE);

        assertEquals(CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, constTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final ConstTypeExpression constTypeExpression = new ConstTypeExpression();
        constTypeExpression.setValue(INVALID_VALUE);

        assertEquals(CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, constTypeExpression.bitSizeOf());
    }

    private static final int CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 10;
    private static final int CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7;

    private static final byte VALID_VALUE = 0x01;
    private static final byte INVALID_VALUE = 0x00;

    private static final byte ADDITIONAL_VALUE = 0x03;
}
