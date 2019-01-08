package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.string_type.StringTypeExpression;

public class StringTypeTest
{
    @Test
    public void append()
    {
        final StringTypeExpression stringTypeExpression = new StringTypeExpression(FIRST_VALUE, SECOND_VALUE);

        assertEquals(FIRST_VALUE + SECOND_VALUE + "_appendix", stringTypeExpression.funcAppend());
    }

    private static final String FIRST_VALUE = "first";
    private static final String SECOND_VALUE = "second";
}
