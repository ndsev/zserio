package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class BitBufferTest
{
    @Test
    public void bufferConstructor()
    {
        final int byteSize = 2;
        final byte[] buffer = new byte[byteSize];
        final BitBuffer bitBuffer = new BitBuffer(buffer);
        assertEquals(8 * byteSize, bitBuffer.getBitSize());

        final long emptyBitSize = 0;
        final byte[] emptyBuffer = new byte[0];
        final BitBuffer emptyBitBuffer = new BitBuffer(emptyBuffer);
        assertEquals(emptyBitSize, emptyBitBuffer.getBitSize());
    }

    @Test
    public void bufferBitSizeConstructor()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[(int)((bitSize + 7) / 8)];
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertEquals(bitSize, bitBuffer.getBitSize());

        final long emptyBitSize = 0;
        final byte[] emptyBuffer = new byte[0];
        final BitBuffer emptyBitBuffer = new BitBuffer(emptyBuffer, emptyBitSize);
        assertEquals(emptyBitSize, emptyBitBuffer.getBitSize());

        final long outOfRangeBitSize = 9;
        final byte[] outOfRangeBuffer = new byte[1];
        assertThrows(IllegalArgumentException.class, () -> new BitBuffer(outOfRangeBuffer, outOfRangeBitSize));
    }

    @Test
    public void equalsMethod()
    {
        final long bitSize = 11;
        final BitBuffer bitBuffer1 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xE0}, bitSize);
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xF0}, bitSize);
        assertTrue(bitBuffer1.equals(bitBuffer2));

        final BitBuffer bitBuffer3 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xFF}, bitSize);
        assertTrue(bitBuffer1.equals(bitBuffer3));

        final BitBuffer bitBuffer4 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xC0}, bitSize);
        assertFalse(bitBuffer1.equals(bitBuffer4));

        final BitBuffer bitBuffer5 = new BitBuffer(new byte[]{(byte)0xBA, (byte)0xE0}, bitSize);
        assertFalse(bitBuffer1.equals(bitBuffer5));

        final BitBuffer bitBuffer6 = new BitBuffer(new byte[]{(byte)0xAB});
        assertFalse(bitBuffer1.equals(bitBuffer6));

        final BitBuffer bitBuffer7 = new BitBuffer(new byte[]{});
        assertFalse(bitBuffer1.equals(bitBuffer7));
    }

    @Test
    public void hashCodeMethod()
    {
        final long bitSize = 11;
        final BitBuffer bitBuffer1 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xE0}, bitSize);
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xF0}, bitSize);
        assertTrue(bitBuffer1.hashCode() == bitBuffer2.hashCode());

        final BitBuffer bitBuffer3 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xFF}, bitSize);
        assertTrue(bitBuffer1.hashCode() == bitBuffer3.hashCode());

        final BitBuffer bitBuffer4 = new BitBuffer(new byte[]{(byte)0xAB, (byte)0xC0}, bitSize);
        assertFalse(bitBuffer1.hashCode() == bitBuffer4.hashCode());

        final BitBuffer bitBuffer5 = new BitBuffer(new byte[]{(byte)0xBA, (byte)0xE0}, bitSize);
        assertFalse(bitBuffer1.hashCode() == bitBuffer5.hashCode());

        final BitBuffer bitBuffer6 = new BitBuffer(new byte[]{(byte)0xAB});
        assertFalse(bitBuffer1.hashCode() == bitBuffer6.hashCode());

        final BitBuffer bitBuffer7 = new BitBuffer(new byte[]{});
        assertFalse(bitBuffer1.hashCode() == bitBuffer7.hashCode());
    }

    @Test
    public void getBuffer()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[]{(byte)0xAB, (byte)0xE0};
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertTrue(java.util.Arrays.equals(buffer, bitBuffer.getBuffer()));
    }

    @Test
    public void getBitSize()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[]{(byte)0xAB, (byte)0xE0};
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertEquals(bitSize, bitBuffer.getBitSize());
    }

    @Test
    public void getByteSize()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[]{(byte)0xAB, (byte)0xE0};
        final int byteSize = buffer.length;
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertEquals(byteSize, bitBuffer.getByteSize());
    }
}
