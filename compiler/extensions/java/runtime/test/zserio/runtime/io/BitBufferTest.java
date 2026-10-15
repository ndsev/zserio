package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final BitBuffer bitBuffer1 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, bitSize);
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xF0}, bitSize);
        assertTrue(bitBuffer1.equals(bitBuffer2));

        final BitBuffer bitBuffer3 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xFF}, bitSize);
        assertTrue(bitBuffer1.equals(bitBuffer3));

        final BitBuffer bitBuffer4 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xC0}, bitSize);
        assertFalse(bitBuffer1.equals(bitBuffer4));

        final BitBuffer bitBuffer5 = new BitBuffer(new byte[] {(byte)0xBA, (byte)0xE0}, bitSize);
        assertFalse(bitBuffer1.equals(bitBuffer5));

        final BitBuffer bitBuffer6 = new BitBuffer(new byte[] {(byte)0xAB});
        assertFalse(bitBuffer1.equals(bitBuffer6));

        final BitBuffer bitBuffer7 = new BitBuffer(new byte[] {});
        assertFalse(bitBuffer1.equals(bitBuffer7));
    }

    @Test
    public void compareToMethod()
    {
        final BitBuffer bitBufferEmpty1 = new BitBuffer(new byte[] {});
        final BitBuffer bitBufferEmpty2 = new BitBuffer(new byte[] {});
        assertEquals(0, bitBufferEmpty1.compareTo(bitBufferEmpty2));

        final BitBuffer bitBufferByte1 = new BitBuffer(new byte[] {(byte)0xAB}, 8);
        final BitBuffer bitBufferByte2 = new BitBuffer(new byte[] {(byte)0xAC}, 8);
        assertTrue(bitBufferByte1.compareTo(bitBufferByte2) < 0);
        assertTrue(bitBufferByte2.compareTo(bitBufferByte1) > 0);

        final BitBuffer bitBuffer1 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11);
        assertTrue(bitBufferEmpty1.compareTo(bitBuffer1) < 0);
        assertTrue(bitBufferByte1.compareTo(bitBuffer1) < 0);
        assertTrue(bitBuffer1.compareTo(bitBufferEmpty1) > 0);
        assertTrue(bitBuffer1.compareTo(bitBufferByte1) > 0);

        final BitBuffer bitBuffer1Copy = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11);
        assertEquals(0, bitBuffer1.compareTo(bitBuffer1Copy));

        final BitBuffer bitBuffer2 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xF0}, 11);
        assertEquals(0, bitBuffer1.compareTo(bitBuffer2));
        assertEquals(0, bitBuffer2.compareTo(bitBuffer1));

        final BitBuffer bitBuffer3 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0x00}, 11);
        assertTrue(bitBuffer3.compareTo(bitBuffer1) < 0);
        assertTrue(bitBuffer1.compareTo(bitBuffer3) > 0);

        final BitBuffer bitBuffer4 = new BitBuffer(new byte[] {(byte)0x00, (byte)0x00}, 11);
        assertTrue(bitBuffer4.compareTo(bitBuffer1) < 0);
        assertTrue(bitBuffer1.compareTo(bitBuffer4) > 0);

        final BitBuffer bitBuffer5 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0, (byte)0x00}, 20);
        assertTrue(bitBuffer1.compareTo(bitBuffer5) < 0);
        assertTrue(bitBuffer5.compareTo(bitBuffer1) > 0);

        final BitBuffer bitBuffer6 = new BitBuffer(new byte[] {(byte)0xA0}, 3);
        final BitBuffer bitBuffer7 = new BitBuffer(new byte[] {(byte)0xA0}, 4);
        assertFalse(bitBuffer6.equals(bitBuffer7));
        assertTrue(bitBuffer6.compareTo(bitBuffer7) < 0);
        assertTrue(bitBuffer7.compareTo(bitBuffer6) > 0);
    }

    @Test
    public void hashCodeMethod()
    {
        final long bitSize = 11;
        final BitBuffer bitBuffer1 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, bitSize);
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xF0}, bitSize);
        assertTrue(bitBuffer1.hashCode() == bitBuffer2.hashCode());

        final BitBuffer bitBuffer3 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xFF}, bitSize);
        assertTrue(bitBuffer1.hashCode() == bitBuffer3.hashCode());

        final BitBuffer bitBuffer4 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xC0}, bitSize);
        assertFalse(bitBuffer1.hashCode() == bitBuffer4.hashCode());

        final BitBuffer bitBuffer5 = new BitBuffer(new byte[] {(byte)0xBA, (byte)0xE0}, bitSize);
        assertFalse(bitBuffer1.hashCode() == bitBuffer5.hashCode());

        final BitBuffer bitBuffer6 = new BitBuffer(new byte[] {(byte)0xAB});
        assertFalse(bitBuffer1.hashCode() == bitBuffer6.hashCode());

        final BitBuffer bitBuffer7 = new BitBuffer(new byte[] {});
        assertFalse(bitBuffer1.hashCode() == bitBuffer7.hashCode());
    }

    @Test
    public void getBuffer()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[] {(byte)0xAB, (byte)0xE0};
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertTrue(java.util.Arrays.equals(buffer, bitBuffer.getBuffer()));
    }

    @Test
    public void getBitSize()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[] {(byte)0xAB, (byte)0xE0};
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertEquals(bitSize, bitBuffer.getBitSize());
    }

    @Test
    public void getByteSize()
    {
        final long bitSize = 11;
        final byte[] buffer = new byte[] {(byte)0xAB, (byte)0xE0};
        final int byteSize = buffer.length;
        final BitBuffer bitBuffer = new BitBuffer(buffer, bitSize);
        assertEquals(byteSize, bitBuffer.getByteSize());
    }
}
