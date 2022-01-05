package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

public class ByteArrayBitStreamVarUIntTest
{
    @Test
    public void readWriteZero() throws IOException
    {
        readWriteTest(BigInteger.ZERO, 1);
    }

    @Test
    public void readWriteLongMax() throws IOException
    {
        readWriteTest(BigInteger.valueOf(Long.MAX_VALUE), 9);
    }

    @Test
    public void readWriteOne() throws IOException
    {
        readWriteTest(BigInteger.ONE, 1);
    }

    @Test
    public void readWriteByte1Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(7).subtract(BigInteger.ONE), 1);
    }

    @Test
    public void readWriteByte2Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(7), 2);
    }

    @Test
    public void readWriteByte2Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE), 2);
    }

    @Test
    public void readWriteByte3Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(14), 3);
    }

    @Test
    public void readWriteByte3Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(21).subtract(BigInteger.ONE), 3);
    }

    @Test
    public void readWriteByte4Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(21), 4);
    }

    @Test
    public void readWriteByte4Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE), 4);
    }

    @Test
    public void readWriteByte5Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(28), 5);
    }

    @Test
    public void readWriteByte5Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(35).subtract(BigInteger.ONE), 5);
    }

    @Test
    public void readWriteByte6Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(35), 6);
    }

    @Test
    public void readWriteByte6Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(42).subtract(BigInteger.ONE), 6);
    }

    @Test
    public void readWriteByte7Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(42), 7);
    }

    @Test
    public void readWriteByte7Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(49).subtract(BigInteger.ONE), 7);
    }

    @Test
    public void readWriteByte8Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(49), 8);
    }

    @Test
    public void readWriteByte8Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE), 8);
    }

    @Test
    public void readWriteByte9Min() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(56), 9);
    }

    @Test
    public void readWriteByte9Max() throws IOException
    {
        readWriteTest(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE), 9);
    }

    @Test
    public void writeOneAboveMaxThrows() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(IOException.class, () -> writer.writeVarUInt(BigInteger.ONE.shiftLeft(64)));
    }

    @Test
    public void writeMinusOneThrows() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(IOException.class, () -> writer.writeVarUInt(BigInteger.valueOf(-1)));
    }

    @Test
    public void writeLongMinThrows() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(IOException.class, () -> writer.writeVarUInt(BigInteger.valueOf(Long.MIN_VALUE)));
    }

    private void readWriteTest(BigInteger value, int expectedNumBytes) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeVarUInt(value);
        assertEquals(0, writer.getBitPosition() % 8);
        assertEquals(expectedNumBytes, writer.getBytePosition());
        byte[] buffer = writer.toByteArray();
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
        BigInteger readValue = reader.readVarUInt();
        assertEquals(value, readValue);
    }
}
