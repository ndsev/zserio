package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.array_type.ArrayTypeExpression;

public class ArrayTypeTest
{
    @Test
    public void bitSizeOfWithOptional()
    {
        final byte[] array = new byte[] { 0, 0 };
        final ArrayTypeExpression arrayTypeExpression = new ArrayTypeExpression(array, true);

        assertEquals(ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, arrayTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final ArrayTypeExpression arrayTypeExpression = new ArrayTypeExpression();
        final byte[] array = new byte[] { 1, 1 };
        arrayTypeExpression.setArray(array);

        assertEquals(ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, arrayTypeExpression.bitSizeOf());
    }

    private static final int ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 17;
    private static final int ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 16;
}
