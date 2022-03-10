package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ByteArrayBitStreamVarIntTest
{
    @Test
    public void readWriteZero() throws IOException
    {
        readWriteTest(0, 1);
    }

    @Test
    public void readWriteLongMin() throws IOException
    {
        readWriteTest(Long.MIN_VALUE, 1); // special case, encoded as -0
    }

    @Test
    public void readWriteLongMax() throws IOException
    {
        readWriteTest(Long.MAX_VALUE, 9);
    }

    @Test
    public void readWriteMinusOne() throws IOException
    {
        readWriteTest(-1L, 1);
    }

    @Test
    public void readWriteOne() throws IOException
    {
        readWriteTest(1L, 1);
    }

    @Test
    public void readWriteByte1NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 6) + 1, 1);
    }

    @Test
    public void readWriteByte1PositiveMax() throws IOException
    {
        readWriteTest((1L << 6) - 1, 1);
    }

    @Test
    public void readWriteByte2NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 6), 2);
    }

    @Test
    public void readWriteByte2PositiveMin() throws IOException
    {
        readWriteTest((1L << 6), 2);
    }

    @Test
    public void readWriteByte2NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 13) + 1, 2);
    }

    @Test
    public void readWriteByte2PositiveMax() throws IOException
    {
        readWriteTest((1L << 13) - 1, 2);
    }

    @Test
    public void readWriteByte3NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 13), 3);
    }

    @Test
    public void readWriteByte3PositiveMin() throws IOException
    {
        readWriteTest((1L << 13), 3);
    }

    @Test
    public void readWriteByte3NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 20) + 1, 3);
    }

    @Test
    public void readWriteByte3PositiveMax() throws IOException
    {
        readWriteTest((1L << 20) - 1, 3);
    }

    @Test
    public void readWriteByte4NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 20), 4);
    }

    @Test
    public void readWriteByte4PositiveMin() throws IOException
    {
        readWriteTest((1L << 20), 4);
    }

    @Test
    public void readWriteByte4NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 27) + 1, 4);
    }

    @Test
    public void readWriteByte4PositiveMax() throws IOException
    {
        readWriteTest((1L << 27) - 1, 4);
    }

    @Test
    public void readWriteByte5NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 27), 5);
    }

    @Test
    public void readWriteByte5PositiveMin() throws IOException
    {
        readWriteTest((1L << 27), 5);
    }

    @Test
    public void readWriteByte5NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 34) + 1, 5);
    }

    @Test
    public void readWriteByte5PositiveMax() throws IOException
    {
        readWriteTest((1L << 34) - 1, 5);
    }

    @Test
    public void readWriteByte6NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 34), 6);
    }

    @Test
    public void readWriteByte6PositiveMin() throws IOException
    {
        readWriteTest((1L << 34), 6);
    }

    @Test
    public void readWriteByte6NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 41) + 1, 6);
    }

    @Test
    public void readWriteByte6PositiveMax() throws IOException
    {
        readWriteTest((1L << 41) - 1, 6);
    }

    @Test
    public void readWriteByte7NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 41), 7);
    }

    @Test
    public void readWriteByte7PositiveMin() throws IOException
    {
        readWriteTest((1L << 41), 7);
    }

    @Test
    public void readWriteByte7NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 48) + 1, 7);
    }

    @Test
    public void readWriteByte7PositiveMax() throws IOException
    {
        readWriteTest((1L << 48) - 1, 7);
    }

    @Test
    public void readWriteByte8NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 48), 8);
    }

    @Test
    public void readWriteByte8PositiveMin() throws IOException
    {
        readWriteTest((1L << 48), 8);
    }

    @Test
    public void readWriteByte8NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 55) + 1, 8);
    }

    @Test
    public void readWriteByte8PositiveMax() throws IOException
    {
        readWriteTest((1L << 55) - 1, 8);
    }

    @Test
    public void readWriteByte9NegativeMin() throws IOException
    {
        readWriteTest(-(1L << 55), 9);
    }

    @Test
    public void readWriteByte9PositiveMin() throws IOException
    {
        readWriteTest((1L << 55), 9);
    }

    @Test
    public void readWriteByte9NegativeMax() throws IOException
    {
        readWriteTest(-(1L << 63) + 1, 9);
    }

    @Test
    public void readWriteByte9PositiveMax() throws IOException
    {
        readWriteTest((1L << 63) - 1, 9);
    }

    private void readWriteTest(long value, int expectedNumBytes) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeVarInt(value);
            assertEquals(0, writer.getBitPosition() % 8);
            assertEquals(expectedNumBytes, writer.getBytePosition());
            final byte[] buffer = writer.toByteArray();
            try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer))
            {
                final long readValue = reader.readVarInt();
                assertEquals(value, readValue);
            }
        }
    }
}
