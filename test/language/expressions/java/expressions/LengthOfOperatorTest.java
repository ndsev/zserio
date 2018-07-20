package expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import zserio.runtime.array.UnsignedByteArray;

import expressions.lengthof_operator.LengthOfFunctions;

public class LengthOfOperatorTest
{
    @Test
    public void getLengthOfFixedArray()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        final int fixedArrayLength = 10;
        final UnsignedByteArray fixedArray = new UnsignedByteArray(fixedArrayLength);
        lengthOfFunctions.setFixedArray(fixedArray);
        assertEquals(fixedArrayLength, lengthOfFunctions.getLengthOfFixedArray());
    }

    @Test
    public void getLengthOfVariableArray()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        final int variableArrayLength = 11;
        final UnsignedByteArray variableArray = new UnsignedByteArray(variableArrayLength);
        lengthOfFunctions.setNumElements((short)variableArrayLength);
        lengthOfFunctions.setVariableArray(variableArray);
        assertEquals(variableArrayLength, lengthOfFunctions.getLengthOfVariableArray());
    }

    @Test
    public void getLengthOfImplicitArray()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        final int implicitArrayLength = 12;
        final UnsignedByteArray implicitArray = new UnsignedByteArray(implicitArrayLength);
        lengthOfFunctions.setImplicitArray(implicitArray);
        assertEquals(implicitArrayLength, lengthOfFunctions.getLengthOfImplicitArray());
    }
}
