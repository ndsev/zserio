package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.BuiltInOperators;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

import expressions.lengthof_operator.LengthOfFunctions;
import expressions.lengthof_operator.STR_CONSTANT;

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

    @Test
    public void getLengthOfStrConstant()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        // check that it's length in bytes (UTF-8)
        assertEquals(11, BuiltInOperators.lengthOf(STR_CONSTANT.STR_CONSTANT));
        assertEquals(BuiltInOperators.lengthOf(STR_CONSTANT.STR_CONSTANT),
                lengthOfFunctions.funcGetLengthOfStrConstant());
    }

    @Test
    public void getLengthOfLiteral()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        assertEquals(10, BuiltInOperators.lengthOf("€literal")); // check that it's length in bytes (UTF-8)
        assertEquals(BuiltInOperators.lengthOf("€literal"), lengthOfFunctions.funcGetLengthOfLiteral());
    }

    @Test
    public void literalLengthFieldDefault()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        assertEquals(10, BuiltInOperators.lengthOf("€literal")); // check that it's length in bytes (UTF-8)
        assertEquals(BuiltInOperators.lengthOf("€literal"), lengthOfFunctions.getLiteralLengthField());
    }

    @Test
    public void getLengthOfString()
    {
        final String strField = "€test";
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        lengthOfFunctions.setStrField(strField);
        assertEquals(7, BuiltInOperators.lengthOf(strField)); // check that it's length in bytes (UTF-8)
        assertEquals(BuiltInOperators.lengthOf(strField), lengthOfFunctions.funcGetLengthOfString());
    }

    @Test
    public void getLengthOfBytes()
    {
        final byte[] bytesField = new byte[] {0x00, 0x01, 0x02};
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        lengthOfFunctions.setBytesField(bytesField);
        assertEquals(bytesField.length, lengthOfFunctions.funcGetLengthOfBytes());
    }

    @Test
    public void getLengthOfFirstStrInArray()
    {
        final String[] strArray = new String[] {"€", "$"};
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        lengthOfFunctions.setStrArray(strArray);
        assertEquals(3, BuiltInOperators.lengthOf(strArray[0])); // check that it's length in bytes (UTF-8)
        assertEquals(
                BuiltInOperators.lengthOf(strArray[0]), lengthOfFunctions.funcGetLengthOfFirstStrInArray());
    }

    @Test
    public void getLengthOfFirstBytesInArray()
    {
        final byte[][] bytesArray = new byte[][] {{0x00, 0x01}, {}};
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        lengthOfFunctions.setBytesArray(bytesArray);
        assertEquals(bytesArray[0].length, lengthOfFunctions.funcGetLengthOfFirstBytesInArray());
    }

    @Test
    public void writeRead()
    {
        final LengthOfFunctions lengthOfFunctions = new LengthOfFunctions();
        lengthOfFunctions.setFixedArray(
                new short[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});
        lengthOfFunctions.setNumElements((short)3);
        lengthOfFunctions.setVariableArray(new short[] {0x03, 0x02, 0x01});
        lengthOfFunctions.setStrField("longer than constant");
        lengthOfFunctions.setBytesField(new byte[] {0x01, 0x02, 0x03});
        lengthOfFunctions.setStrArray(new String[] {});
        lengthOfFunctions.setBytesArray(new byte[][] {});

        final BitBuffer bitBuffer = SerializeUtil.serialize(lengthOfFunctions);
        final LengthOfFunctions readLengthOfFunctions =
                SerializeUtil.deserialize(LengthOfFunctions.class, bitBuffer);
        assertEquals(lengthOfFunctions, readLengthOfFunctions);
    }
}
