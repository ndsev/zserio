package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.float_type.FloatTypeExpression;

public class FloatTypeTest
{
    @Test
    public void result()
    {
        final FloatTypeExpression floatTypeExpression = new FloatTypeExpression(FLOAT_VALUE);

        final boolean result = (FLOAT_VALUE * 2.0f + 1.0f / 0.5f > 1.0f);
        assertEquals(result, floatTypeExpression.funcResult());
    }

    private static final float FLOAT_VALUE = 15.5f;
}
