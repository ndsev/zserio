package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.negation_operator.NegationOperatorExpression;

public class NegationOperatorTest
{
    @Test
    public void negatedValue()
    {
        NegationOperatorExpression negationOperatorExpression = new NegationOperatorExpression(true);
        assertEquals(false, negationOperatorExpression.funcNegatedValue());

        negationOperatorExpression.setValue(false);
        assertEquals(true, negationOperatorExpression.funcNegatedValue());
    }
}
