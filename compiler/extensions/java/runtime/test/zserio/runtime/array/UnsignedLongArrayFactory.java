package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class UnsignedLongArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new UnsignedLongArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new UnsignedLongArray(data, offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new UnsignedLongArray(reader, length, numBits));
    }

    private ArrayWrapper wrap(UnsignedLongArray array)
    {
        return new UnsignedLongArrayWrapper(array);
    }

    private static class UnsignedLongArrayWrapper implements ArrayWrapper
    {
        public UnsignedLongArrayWrapper(UnsignedLongArray array)
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

        public int sum()
        {
            return array.sum();
        }

        public ArrayWrapper subRange(int offset, int length)
        {
            return new UnsignedLongArrayWrapper((UnsignedLongArray)array.subRange(offset, length));
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
            return array.iterator();
        }

        public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
        {
            array.write(writer, numBits);
        }

        private Object unwrap(Object obj)
        {
            // if other is UnsignedLongArrayWrapper, unwrap the array
            if (obj instanceof UnsignedLongArrayWrapper)
            {
                obj = ((UnsignedLongArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final UnsignedLongArray array;
    }
}
