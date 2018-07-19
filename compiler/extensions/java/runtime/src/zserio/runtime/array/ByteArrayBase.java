package zserio.runtime.array;

import java.util.Arrays;
import java.util.List;

import zserio.runtime.Mapping;
import zserio.runtime.Util;

/**
 * Implements base class for Zserio zserio int8 array and for int:1...int:8 arrays.
 */
abstract class ByteArrayBase extends NumericArrayBase<Byte>
{
    /**
     * Empty constructor.
     */
    public ByteArrayBase()
    {}

    /**
     * Constructs array from byte array.
     *
     * @param data   byte array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public ByteArrayBase(byte[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("ByteArrayBase: Requested array sequence ends beyond " +
                    "the end of the given array. Requested offset is " + offset + ", length is " + length +
                    "but array length is " + data.length + ".");

        this.data = Arrays.copyOfRange(data, offset, offset + length);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be zero.
     *
     * @param length Number of elements for created object.
     */
    public ByteArrayBase(int length)
    {
        data = new byte[length];
    }

    /**
     * Returns element at the given position.
     *
     * @param i Index of element to return.
     *
     * @return Element at the given position.
     */
    public byte elementAt(int i)
    {
        return data[i];
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(byte value, int i)
    {
        data[i] = value;
    }

    @Override
    public int length()
    {
        return data.length;
    }

    @Override
    public int hashCode()
    {
        int result = Util.HASH_SEED;

        for (byte value : data)
        {
            result = result * Util.HASH_PRIME_NUMBER + value;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ByteArrayBase)
        {
            final ByteArrayBase that = (ByteArrayBase)obj;

            if (length() != that.length())
                return false;

            for (int index = 0; index < length(); ++index)
            {
                if (data[index] != that.data[index])
                    return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Sums up all values of an array.
     *
     * @return Sum of all array values.
     */
    public int sum()
    {
        int sumValue = 0;
        for (final byte value : data)
        {
            sumValue += value;
        }
        return sumValue;
    }

    @Override
    protected Byte boxedElementAt(int index)
    {
        return elementAt(index);
    }

    @Override
    protected void setFromList(List<Byte> list)
    {
        data = new byte[list.size()];

        for (int i = 0; i < data.length; ++i)
        {
            data[i] = list.get(i);
        }
    }

    /**
     * Fills given array by mapped values of each element.
     *
     * @param mapping The mapping to use.
     * @param result  Array to which to fill mapped values.
     */
    protected void mapImpl(Mapping<Byte> mapping, ByteArrayBase result)
    {
        for (int i = 0; i < data.length; i++)
        {
            result.setElementAt(mapping.map(elementAt(i)), i);
        }
    }

    /**
     * The underlying byte array.
     */
    protected byte[] data;
}
