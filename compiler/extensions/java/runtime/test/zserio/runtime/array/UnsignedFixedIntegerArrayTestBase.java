package zserio.runtime.array;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public abstract class UnsignedFixedIntegerArrayTestBase extends NumericArrayTestBase
{
    public UnsignedFixedIntegerArrayTestBase(ArrayFactory factory)
    {
        super(factory);
    }

    @Test
    public void constructorRead() throws IOException
    {
        final long[] data = getReadWriteTestData();
        final int numBits = getReadWriteTestNumBits();

        final ByteArrayBitStreamReader in = createReader(data, numBits);
        final ArrayWrapper array = factory.create(in, data.length, numBits);

        assertEquals(data.length, array.length());

        for (int i = 0; i < data.length; i++)
            assertEquals(data[i], array.elementAt(i));
    }

    @Test
    public void write() throws IOException
    {
        final long[] data = getReadWriteTestData();
        final int numBits = getReadWriteTestNumBits();

        final ArrayWrapper array = factory.create(data.length);

        for (int i = 0; i < data.length; i++)
            array.setElementAt(data[i], i);

        compareArray(array, numBits, data);
    }

    protected ByteArrayBitStreamReader createReader(long[] data, int numBits) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (long value : data)
            writer.writeBits(value, numBits);
        writer.close();

        return new ByteArrayBitStreamReader(writer.toByteArray());
    }

    protected void compareArray(ArrayWrapper array, int numBits, long[] data) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        array.write(writer, numBits);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());

        for (long value : data)
        {
            assertEquals(value, reader.readBits(numBits));
        }
    }

    protected abstract int getReadWriteTestNumBits();
    protected abstract long[] getReadWriteTestData();
}
