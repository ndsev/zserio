package constraints;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import constraints.array_lengthof_constraint.ArrayLengthofConstraint;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class ArrayLengthofConstraintTest
{
    @Test
    public void readConstructorCorrectLength() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer = writeArrayLengthofConstraintToBitBuffer(CORRECT_LENGTH);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint(reader);
        assertEquals(CORRECT_LENGTH, arrayLengthofConstraint.getArray().length);
    }

    @Test
    public void readConstructorWrongLengthLess() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer = writeArrayLengthofConstraintToBitBuffer(WRONG_LENGTH_LESS);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new ArrayLengthofConstraint(reader));
    }

    @Test
    public void readConstructorWrongLengthGreater() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer = writeArrayLengthofConstraintToBitBuffer(WRONG_LENGTH_GREATER);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new ArrayLengthofConstraint(reader));
    }

    @Test
    public void writeReadCorrectLength() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[CORRECT_LENGTH];
        for (int i = 0; i < CORRECT_LENGTH; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final BitBuffer bitBuffer = SerializeUtil.serialize(arrayLengthofConstraint);
        final ArrayLengthofConstraint readArrayLengthofConstraint = SerializeUtil.deserialize(
                ArrayLengthofConstraint.class, bitBuffer);
        assertEquals(CORRECT_LENGTH, readArrayLengthofConstraint.getArray().length);
        assertTrue(arrayLengthofConstraint.equals(readArrayLengthofConstraint));
    }

    @Test
    public void writeWrongLengthLess() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[WRONG_LENGTH_LESS];
        for (int i = 0; i < WRONG_LENGTH_LESS; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> arrayLengthofConstraint.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongLengthGreater() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[WRONG_LENGTH_GREATER];
        for (int i = 0; i < WRONG_LENGTH_GREATER; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> arrayLengthofConstraint.write(writer));
        writer.close();
    }

    private BitBuffer writeArrayLengthofConstraintToBitBuffer(int length)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(length, 8); // all lengths in this test fits in a single byte
            for (int i = 0; i < length; ++i)
                writer.writeBits(i, 32);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final int CORRECT_LENGTH = 6;
    private static final int WRONG_LENGTH_LESS = 3;
    private static final int WRONG_LENGTH_GREATER = 12;
}
