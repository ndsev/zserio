package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.left_shift_operator.LeftShiftOperator;

public class LeftShiftOperatorTest
{
    @Test
    public void defaultValues()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(40, leftShiftOperator.getU32());
        assertEquals(-40, leftShiftOperator.getI32());
        assertEquals(32, leftShiftOperator.getU32Complex());
        assertEquals(-32, leftShiftOperator.getI32Complex());
        assertEquals(24, leftShiftOperator.getU32Plus());
        assertEquals(-64, leftShiftOperator.getI32Minus());
        assertEquals(12, leftShiftOperator.getU32PlusRhsExpr());
        assertEquals(-24, leftShiftOperator.getI32MinusRhsExpr());
        assertEquals(11534336, leftShiftOperator.getU63Complex());
        assertEquals(-9216, leftShiftOperator.getI64Complex());
    }

    @Test
    public void getU63LShift3()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(104, leftShiftOperator.funcGetU63LShift3());
    }

    @Test
    public void getI64LShift4()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(-208, leftShiftOperator.funcGetI64LShift4());
    }

    @Test
    public void getU63LShift()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(13312, leftShiftOperator.funcGetU63LShift());
    }

    @Test
    public void getI64LShift()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(-13312, leftShiftOperator.funcGetI64LShift());
    }

    @Test
    public void getPositiveI32LShift()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(13312, leftShiftOperator.funcGetPositiveI32LShift());
    }

    @Test
    public void getI64ComplexLShift()
    {
        final LeftShiftOperator leftShiftOperator = new LeftShiftOperator();
        assertEquals(-3072, leftShiftOperator.funcGetI64ComplexLShift());
    }
}
