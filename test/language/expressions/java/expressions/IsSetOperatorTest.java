package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.isset_operator.IsSetOperator;
import expressions.isset_operator.Parameterized;
import expressions.isset_operator.TestBitmask;

public class IsSetOperatorTest
{
    @Test
    public void hasNone()
    {
        final TestBitmask testBitmask = new TestBitmask();
        final Parameterized parameterized = new Parameterized(testBitmask);
        final IsSetOperator isSetOperator = new IsSetOperator(testBitmask, parameterized);

        assertFalse(isSetOperator.funcHasInt());
        assertFalse(isSetOperator.funcHasString());
        assertFalse(isSetOperator.funcHasBoth());
        assertFalse(isSetOperator.getParameterized().funcHasInt());
        assertFalse(isSetOperator.getParameterized().funcHasString());
        assertFalse(isSetOperator.getParameterized().funcHasBoth());

        assertFalse(isSetOperator.getParameterized().isIntFieldUsed());
        assertFalse(isSetOperator.getParameterized().isIntFieldSet());
        assertFalse(isSetOperator.getParameterized().isStringFieldUsed());
        assertFalse(isSetOperator.getParameterized().isStringFieldSet());
    }

    @Test
    public void hasInt()
    {
        final TestBitmask testBitmask = TestBitmask.Values.INT;
        final Parameterized parameterized = new Parameterized(testBitmask);
        parameterized.setIntField((long)13);
        final IsSetOperator isSetOperator = new IsSetOperator(testBitmask, parameterized);

        assertTrue(isSetOperator.funcHasInt());
        assertFalse(isSetOperator.funcHasString());
        assertFalse(isSetOperator.funcHasBoth());
        assertTrue(isSetOperator.getParameterized().funcHasInt());
        assertFalse(isSetOperator.getParameterized().funcHasString());
        assertFalse(isSetOperator.getParameterized().funcHasBoth());

        assertTrue(isSetOperator.getParameterized().isIntFieldUsed());
        assertTrue(isSetOperator.getParameterized().isIntFieldSet());
        assertFalse(isSetOperator.getParameterized().isStringFieldUsed());
        assertFalse(isSetOperator.getParameterized().isStringFieldSet());
    }

    @Test
    public void hasString()
    {
        final TestBitmask testBitmask = TestBitmask.Values.STRING;
        final Parameterized parameterized = new Parameterized(testBitmask);
        parameterized.setStringField("test");
        final IsSetOperator isSetOperator = new IsSetOperator(testBitmask, parameterized);

        assertFalse(isSetOperator.funcHasInt());
        assertTrue(isSetOperator.funcHasString());
        assertFalse(isSetOperator.funcHasBoth());
        assertFalse(isSetOperator.getParameterized().funcHasInt());
        assertTrue(isSetOperator.getParameterized().funcHasString());
        assertFalse(isSetOperator.getParameterized().funcHasBoth());

        assertFalse(isSetOperator.getParameterized().isIntFieldUsed());
        assertFalse(isSetOperator.getParameterized().isIntFieldSet());
        assertTrue(isSetOperator.getParameterized().isStringFieldUsed());
        assertTrue(isSetOperator.getParameterized().isStringFieldSet());
    }

    @Test
    public void hasBoth()
    {
        final TestBitmask testBitmask = TestBitmask.Values.BOTH;
        final Parameterized parameterized = new Parameterized(testBitmask, (long)13, "test");
        final IsSetOperator isSetOperator = new IsSetOperator(testBitmask, parameterized);

        assertTrue(isSetOperator.funcHasInt());
        assertTrue(isSetOperator.funcHasString());
        assertTrue(isSetOperator.funcHasBoth());
        assertTrue(isSetOperator.getParameterized().funcHasInt());
        assertTrue(isSetOperator.getParameterized().funcHasString());
        assertTrue(isSetOperator.getParameterized().funcHasBoth());

        assertTrue(isSetOperator.getParameterized().isIntFieldUsed());
        assertTrue(isSetOperator.getParameterized().isIntFieldSet());
        assertTrue(isSetOperator.getParameterized().isStringFieldUsed());
        assertTrue(isSetOperator.getParameterized().isStringFieldSet());
    }
}
