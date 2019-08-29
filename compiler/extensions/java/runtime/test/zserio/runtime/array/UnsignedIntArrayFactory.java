package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class UnsignedIntArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new UnsignedIntArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new UnsignedIntArray(convertDataArray(data), offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new UnsignedIntArray(reader, length, numBits));
    }

    private long[] convertDataArray(long[] data)
    {
        long[] intData = new long[data.length];
        for (int i = 0; i < data.length; i++)
            intData[i] = data[i];

        return intData;
    }

    private ArrayWrapper wrap(UnsignedIntArray array)
    {
        return new UnsignedIntArrayWrapper(array);
    }

    private static class UnsignedIntArrayWrapper implements ArrayWrapper
    {
        public UnsignedIntArrayWrapper(UnsignedIntArray array)
        {
            this.array = array;
        }

        @Override
        public void setElementAt(long value, int index)
        {
            array.setElementAt(value, index);
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
            return new UnsignedIntArrayWrapper((UnsignedIntArray)array.subRange(offset, length));
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
            return new IteratorWrapper<Long>(array.iterator());
        }

        public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
        {
            array.write(writer, numBits);
        }

        private Object unwrap(Object obj)
        {
            // if other is UnsignedIntArrayWrapper, unwrap the array
            if (obj instanceof UnsignedIntArrayWrapper)
            {
                obj = ((UnsignedIntArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final UnsignedIntArray array;
    }
}
