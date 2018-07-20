package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.parenthesis.ParenthesisExpression;

public class ParenthesisTest
{
    @Test
    public void result()
    {
        final ParenthesisExpression parenthesisExpression = new ParenthesisExpression(FIRST_VALUE,
                SECOND_VALUE);

        assertEquals(FIRST_VALUE * (SECOND_VALUE + 1), parenthesisExpression.result());
    }

    private static final byte FIRST_VALUE = 0x11;
    private static final byte SECOND_VALUE = 0x22;
}
