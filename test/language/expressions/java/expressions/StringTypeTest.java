package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.string_type.StringTypeExpression;

public class StringTypeTest
{
    @Test
    public void append()
    {
        final StringTypeExpression stringTypeExpression = new StringTypeExpression(firstValue, secondValue);

        assertEquals(firstValue + secondValue + "_appendix", stringTypeExpression.append());
    }

    private static final String firstValue = "first";
    private static final String secondValue = "second";
}
