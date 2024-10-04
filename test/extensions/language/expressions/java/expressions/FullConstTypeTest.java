package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.full_const_type.FullConstTypeExpression;

public class FullConstTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final FullConstTypeExpression fullConstTypeExpression =
                new FullConstTypeExpression(FULL_VALID_VALUE, FULL_ADDITIONAL_VALUE);

        assertEquals(FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, fullConstTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final FullConstTypeExpression fullConstTypeExpression = new FullConstTypeExpression();
        fullConstTypeExpression.setValue(FULL_INVALID_VALUE);

        assertEquals(FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, fullConstTypeExpression.bitSizeOf());
    }

    private static final int FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 10;
    private static final int FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7;

    private static final byte FULL_VALID_VALUE = 0x01;
    private static final byte FULL_INVALID_VALUE = 0x00;

    private static final byte FULL_ADDITIONAL_VALUE = 0x03;
}
