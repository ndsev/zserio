package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class ByteArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new ByteArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new ByteArray(convertDataArray(data), offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new ByteArray(reader, length, numBits));
    }

    private byte[] convertDataArray(long[] data)
    {
        byte[] intData = new byte[data.length];
        for (int i = 0; i < data.length; i++)
            intData[i] = (byte)data[i];

        return intData;
    }

    private ArrayWrapper wrap(ByteArray array)
    {
        return new ByteArrayWrapper(array);
    }

    private static class ByteArrayWrapper implements ArrayWrapper
    {
        public ByteArrayWrapper(ByteArray array)
        {
            this.array = array;
        }

        @Override
        public void setElementAt(long value, int index)
        {
            array.setElementAt((byte)value, index);
        }

        @Override
        public long elementAt(int index)
        {
            return array.elementAt(index);
        }

        @Override
        public int length()
        {
            return array.length();
        }

        public int bitSizeOf(long bitPosition, int numBits)
        {
            return array.bitSizeOf(bitPosition, numBits);
        }

        public ArrayWrapper subRange(int offset, int length)
        {
            return new ByteArrayWrapper((ByteArray)array.subRange(offset, length));
        }

        public int hashCode()
        {
            return array.hashCode();
        }

        public boolean equals(Object other)
        {
            return array.equals(unwrap(other));
        }

        public Iterator<Long> iterator()
        {
            return new IteratorWrapper<Byte>(array.iterator());
        }

        public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
        {
            array.write(writer, numBits);
        }

        private Object unwrap(Object obj)
        {
            // if other is IntArrayWrapper, unwrap the array
            if (obj instanceof ByteArrayWrapper)
            {
                obj = ((ByteArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final ByteArray array;
    }
}
