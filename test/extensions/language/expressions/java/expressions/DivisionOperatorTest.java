package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.division_operator.DivisionFunction;

public class DivisionOperatorTest
{
    @Test
    public void divideFloatByInt()
    {
        final DivisionFunction fun = new DivisionFunction((short)10, (byte)2);
        assertEquals(fun.funcDivideFloatByInt(), 3.33333, 1e-5);
    }

    @Test
    public void divideIntByFloat()
    {
        final DivisionFunction fun = new DivisionFunction((short)10, (byte)2);
        assertEquals(fun.funcDivideIntByFloat(), 3.33333, 1e-5);
    }

    @Test
    public void divideFloatByFloat()
    {
        final DivisionFunction fun = new DivisionFunction((short)10, (byte)2);
        assertEquals(fun.funcDivideFloatByFloat(), 3.33333, 1e-5);
    }

    @Test
    public void divideIntByInt()
    {
        final DivisionFunction fun = new DivisionFunction((short)10, (byte)2);
        assertEquals(fun.funcDivideIntByInt(), 3);
    }
}
