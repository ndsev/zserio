package zserio.runtime.array;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.Util;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio zserio uint64 array.
 *
 * Zserio uint64 array is mapped to Java BigInteger[] type because uint64 type is not supported directly by Java.
 */
public class BigIntegerArray extends NumericArrayBase<BigInteger>
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param numBits Length of element in bits. Must be always 64. It is used only to avoid special handling
     *                for this array in Zserio code generator.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public BigIntegerArray(BitStreamReader reader, int length, int numBits) throws IOException, ZserioError
    {
        this(reader, length, numBits, null);
    }

    /**
     * Constructs array from bit stream applying offset checking.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param numBits Length of element in bits.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public BigIntegerArray(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        readImpl(reader, length, numBits, checker);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be null.
     *
     * @param length Number of elements for created object.
     */
    public BigIntegerArray(int length)
    {
        data = new BigInteger[length];
    }

    /**
     * Constructs array from BigInteger array.
     *
     * @param data   BigInteger array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public BigIntegerArray(BigInteger[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("BigIntegerArray: Requested array sequence ends beyond " +
                    "the end of the given array. Requested offset is " + offset + ", length is " + length +
                    "but array length is " + data.length + ".");

        this.data = Arrays.copyOfRange(data, offset, offset + length);
    }

    /**
     * Returns element at the given position.
     *
     * @param i Index of element to return.
     *
     * @return Element at the given position.
     */
    public BigInteger elementAt(int i)
    {
        return data[i];
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(BigInteger value, int i)
    {
        data[i] = value;
    }

    @Override
    public Array<BigInteger> map(Mapping<BigInteger> mapping)
    {
        final BigIntegerArray result = new BigIntegerArray(data.length);
        for (int i = 0; i < data.length; i++)
        {
            result.setElementAt(mapping.map(elementAt(i)), i);
        }
        return result;
    }

    @Override
    public Array<BigInteger> subRange(int offset, int length)
    {
        return new BigIntegerArray(data, offset, length);
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

        for (BigInteger value : data)
        {
            result = result * Util.HASH_PRIME_NUMBER + value.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BigIntegerArray)
        {
            final BigIntegerArray that = (BigIntegerArray)obj;

            if (length() != that.length())
                return false;

            for (int index = 0; index < length(); ++index)
            {
                if (!data[index].equals(that.data[index]))
                    return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Writes array to bit stream.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
    {
        writeAligned(writer, numBits, null);
    }

    /**
     * Writes auto length array to bit stream.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void writeAuto(BitStreamWriter writer, int numBits) throws IOException, ZserioError
    {
        writeAlignedAuto(writer, numBits, null);
    }

    /**
     * Writes aligned auto length array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     * @param checker Offset checker to use.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAlignedAuto(BitStreamWriter writer, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarUInt64(data.length);
        writeAligned(writer, numBits, checker);
    }

    /**
     * Writes aligned array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     * @param checker Offset checker to use.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAligned(BitStreamWriter writer, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeBigInteger(data[index], numBits);
        }
    }

    /**
     * Returns length of array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOf(long bitPosition, int numBits)
    {
        return bitSizeOfImpl(bitPosition, numBits);
    }

    /**
     * Returns length of auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAuto(long bitPosition, int numBits)
    {
        return bitSizeOfAutoImpl(bitPosition, numBits);
    }

    /**
     * Returns length of aligned auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAlignedAuto(long bitPosition, int numBits)
    {
        return bitSizeOfAlignedAutoImpl(bitPosition, numBits);
    }

    /**
     * Returns length of aligned array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits Length of element in bits.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAligned(long bitPosition, int numBits)
    {
        return bitSizeOfAlignedImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the array.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsets(long bitPosition, int numBits)
    {
        return initializeOffsetsImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the auto length array.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAuto(long bitPosition, int numBits)
    {
        return initializeOffsetsAutoImpl(bitPosition, numBits);
    }

    /**
     * Initializes indexed offsets for the aligned auto length array.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAlignedAuto(long bitPosition, int numBits, OffsetSetter setter)
    {
        return initializeOffsetsAlignedAutoImpl(bitPosition, numBits, setter);
    }

    /**
     * Initializes indexed offsets for the aligned length array.
     *
     * @param bitPosition Current bit stream position.
     * @param numBits     Length of element in bits.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAligned(long bitPosition, int numBits, OffsetSetter setter)
    {
        return initializeOffsetsAlignedImpl(bitPosition, numBits, setter);
    }

    @Override
    protected BigInteger boxedElementAt(int index)
    {
        return data[index];
    }

    @Override
    protected BigInteger readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readBigInteger(numBits);
    }

    @Override
    protected void setFromList(List<BigInteger> list)
    {
        data = new BigInteger[list.size()];
        for (int i = 0; i < data.length; ++i)
        {
            data[i] = list.get(i);
        }
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits,  OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new BigInteger[length];
        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = reader.readBigInteger(numBits);
        }
    }

    private BigInteger[] data;
}
