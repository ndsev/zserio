package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class LongArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new LongArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new LongArray(data, offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new LongArray(reader, length, numBits));
    }

    private ArrayWrapper wrap(LongArray array)
    {
        return new LongArrayWrapper(array);
    }

    private static class LongArrayWrapper implements ArrayWrapper
    {
        public LongArrayWrapper(LongArray array)
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
            return new LongArrayWrapper((LongArray)array.subRange(offset, length));
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
            // if other is LongArrayWrapper, unwrap the array
            if (obj instanceof LongArrayWrapper)
            {
                obj = ((LongArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final LongArray array;
    }
}
