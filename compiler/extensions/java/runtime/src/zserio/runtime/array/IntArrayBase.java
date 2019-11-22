package zserio.runtime.array;

import java.util.Arrays;
import java.util.List;

import zserio.runtime.Mapping;
import zserio.runtime.Util;

/**
 * Implements base class for Zserio int32 array and for int:17...int:32 arrays and for uint16 array
 * and for bit:9..bit:16 arrays.
 */
abstract class IntArrayBase extends NumericArrayBase<Integer>
{
    /**
     * Empty constructor.
     */
    public IntArrayBase()
    {}

    /**
     * Constructs array from int array.
     *
     * @param data   int array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public IntArrayBase(int[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("IntArrayBase: Requested array sequence ends beyond " +
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
    public IntArrayBase(int length)
    {
        data = new int[length];
    }

    /**
     * Returns element at the given position.
     *
     * @param i Index of element to return.
     *
     * @return Element at the given position.
     */
    public int elementAt(int i)
    {
        return data[i];
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(int value, int i)
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

        for (int value : data)
        {
            result = result * Util.HASH_PRIME_NUMBER + value;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IntArrayBase)
        {
            final IntArrayBase that = (IntArrayBase)obj;

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

    @Override
    protected Integer boxedElementAt(int index)
    {
        return elementAt(index);
    }

    @Override
    protected void setFromList(List<Integer> list)
    {
        data = new int[list.size()];

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
    protected void mapImpl(Mapping<Integer> mapping, IntArrayBase result)
    {
        for (int i = 0; i < data.length; i++)
        {
            result.setElementAt(mapping.map(elementAt(i)), i);
        }
    }

    /**
     * The underlying int array.
     */
    protected int[] data;
}
