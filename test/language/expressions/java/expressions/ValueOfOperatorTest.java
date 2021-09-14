package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import expressions.valueof_operator.ValueOfFunctions;
import expressions.valueof_operator.Color;

public class ValueOfOperatorTest
{
    @Test
    public void getValueOfWhiteColor()
    {
        final ValueOfFunctions valueOfFunctions = new ValueOfFunctions(Color.WHITE);
        final short whiteColorValue = 1;
        assertEquals(whiteColorValue, valueOfFunctions.funcGetValueOfColor());
        assertEquals(whiteColorValue, valueOfFunctions.funcGetValueOfWhiteColor());
    }

    @Test
    public void getValueOfBlackColor()
    {
        final ValueOfFunctions valueOfFunctions = new ValueOfFunctions(Color.BLACK);
        final short blackColorValue = 2;
        assertEquals(blackColorValue, valueOfFunctions.funcGetValueOfColor());
        assertEquals(blackColorValue, valueOfFunctions.funcGetValueOfBlackColor());
    }
}
