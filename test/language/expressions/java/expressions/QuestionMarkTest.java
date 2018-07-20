package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.question_mark.QuestionMarkExpression;

public class QuestionMarkTest
{
    @Test
    public void firstValue()
    {
        final QuestionMarkExpression questionMarkExpression = new QuestionMarkExpression(FIRST_VALUE,
                SECOND_VALUE, true);

        assertEquals(FIRST_VALUE, questionMarkExpression.validValue());
    }

    @Test
    public void secondValue()
    {
        final QuestionMarkExpression questionMarkExpression = new QuestionMarkExpression(FIRST_VALUE,
                SECOND_VALUE, false);

        assertEquals(SECOND_VALUE, questionMarkExpression.validValue());
    }

    private static final byte FIRST_VALUE = 0x11;
    private static final byte SECOND_VALUE = 0x22;
}
