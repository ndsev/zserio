package expressions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import expressions.lengthof_operator.LengthOfFunctions;

public class LengthOfOperatorTest
{
    @Test
    public void getLengthOfFixedArray()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        final int fixedArrayLength = 10;
        final short[] fixedArray = new short[fixedArrayLength];
        lengthOfFunctions.setFixedArray(fixedArray);
        assertEquals(fixedArrayLength, lengthOfFunctions.funcGetLengthOfFixedArray());
    }

    @Test
    public void getLengthOfVariableArray()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        final int variableArrayLength = 11;
        final short[] variableArray = new short[variableArrayLength];
        lengthOfFunctions.setNumElements((short)variableArrayLength);
        lengthOfFunctions.setVariableArray(variableArray);
        assertEquals(variableArrayLength, lengthOfFunctions.funcGetLengthOfVariableArray());
    }
}
