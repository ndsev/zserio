package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class IntArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new IntArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new IntArray(convertDataArray(data), offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new IntArray(reader, length, numBits));
    }

    private int[] convertDataArray(long[] data)
    {
        int[] intData = new int[data.length];
        for (int i = 0; i < data.length; i++)
            intData[i] = (int)data[i];

        return intData;
    }

    private ArrayWrapper wrap(IntArray array)
    {
        return new IntArrayWrapper(array);
    }

    private static class IntArrayWrapper implements ArrayWrapper
    {
        public IntArrayWrapper(IntArray array)
        {
            this.array = array;
        }

        @Override
        public void setElementAt(long value, int index)
        {
            array.setElementAt((int)value, index);
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
            return new IntArrayWrapper((IntArray)array.subRange(offset, length));
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
            return new IteratorWrapper<Integer>(array.iterator());
        }

        public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
        {
            array.write(writer, numBits);
        }

        private Object unwrap(Object obj)
        {
            // if other is IntArrayWrapper, unwrap the array
            if (obj instanceof IntArrayWrapper)
            {
                obj = ((IntArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final IntArray array;
    }
}
