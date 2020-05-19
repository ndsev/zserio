package zserio.runtime.array;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.Util;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio string arrays.
 *
 * Zserio string arrays are mapped to Java String[] type.
 */
public class StringArray extends ArrayBase<String>
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Cannot occured because indexed offsets are not used.
     */
    public StringArray(BitStreamReader reader, int length) throws IOException
    {
        this(reader, length, null);
    }

    /**
     * Constructs array from bit stream applying offset checking.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public StringArray(BitStreamReader reader, int length, OffsetChecker checker) throws IOException
    {
        readImpl(reader, length, 0, checker);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be zero.
     *
     * @param length Number of elements for created object.
     */
    public StringArray(int length)
    {
        data = new String[length];
    }

    /**
     * Constructs array from String array.
     *
     * @param data   String array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public StringArray(String[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("StringArrayBase: Requested array sequence ends beyond " +
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
    public String elementAt(int i)
    {
        return data[i];
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(String value, int i)
    {
        data[i] = value;
    }

    @Override
    public Array<String> map(Mapping<String> mapping)
    {
        StringArray result = new StringArray(data.length);
        for (int i = 0; i < data.length; i++)
            result.setElementAt(mapping.map(elementAt(i)), i);

        return result;
    }

    @Override
    public Array<String> subRange(int offset, int length)
    {
        return new StringArray(data, offset, length);
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

        for (String value : data)
        {
            result = result * Util.HASH_PRIME_NUMBER + value.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof StringArray)
        {
            final StringArray that = (StringArray)obj;
            if (that.data.length != data.length)
            {
                return false;
            }

            for (int i = 0; i < data.length; i++)
            {
                if (!this.elementAt(i).equals(that.elementAt(i)))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Writes array to bit stream.
     *
     * This method can be called only for elements which implement InitializeOffsetsWriter interface.
     *
     * @param writer Bit stream where to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Calling on object with writing interface
     */
    public void write(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAligned(writer, null);
    }

    /**
     * Writes auto length array to bit stream.
     *
     * This method can be called only for elements which implement InitializeOffsetsWriter interface.
     *
     * @param writer Bit stream where to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Calling on object with writing interface
     */
    public void writeAuto(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAlignedAuto(writer, null);
    }

    /**
     * Writes array to bit stream.
     *
     * This method can be called only for elements which implement InitializeOffsetsWriter interface.
     *
     * @param writer  Bit stream where to write.
     * @param checker Offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Calling on object with writing interface
     */
    public void writeAlignedAuto(BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarSize(data.length);
        writeAligned(writer, checker);
    }

    /**
     * Writes array to bit stream applying offset checking.
     *
     * This method can be called only for elements which implement InitializeOffsetsWriter interface.
     *
     * @param writer  Bit stream where to write.
     * @param checker Offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking or calling on object with writing interface.
     */
    public void writeAligned(BitStreamWriter writer, OffsetChecker checker) throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeString(data[index]);
        }
    }

    /**
     * Returns length of array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOf(long bitPosition)
    {
        long endBitPosition = bitPosition;
        for (String value : data)
            endBitPosition += BitSizeOfCalculator.getBitSizeOfString(value);

        return (int)(endBitPosition - bitPosition);
    }

    /**
     * Returns length of auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAuto(long bitPosition)
    {
        return BitSizeOfCalculator.getBitSizeOfVarSize(length()) + bitSizeOf(bitPosition);
    }

    /**
     * Returns length of aligned auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAlignedAuto(long bitPosition)
    {
        return BitSizeOfCalculator.getBitSizeOfVarSize(length()) + bitSizeOfAligned(bitPosition);
    }

    /**
     * Returns length of array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAligned(long bitPosition)
    {
        return bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets for the array.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets for the auto length array.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAuto(long bitPosition)
    {
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarSize(data.length);

        return initializeOffsets(currentBitPosition);
    }

    /**
     * Initializes indexed offsets for the aligned auto length array.
     *
     * @param bitPosition Current bit stream position.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAlignedAuto(long bitPosition, OffsetSetter setter)
    {
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarSize(data.length);

        return initializeOffsetsAligned(currentBitPosition, setter);
    }

    /**
     * Initializes indexed offsets for the aligned length array.
     *
     * @param bitPosition Current bit stream position.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAligned(long bitPosition, OffsetSetter setter)
    {
        long currentBitPosition = bitPosition;
        for (int index = 0; index < data.length; index++)
        {
            currentBitPosition = BitPositionUtil.alignTo(Byte.SIZE, currentBitPosition);
            setter.setOffset(index, BitPositionUtil.bitsToBytes(currentBitPosition));
            currentBitPosition += BitSizeOfCalculator.getBitSizeOfString(data[index]);
        }

        return currentBitPosition;
    }

    @Override
    protected String boxedElementAt(int index)
    {
        return elementAt(index);
    }

    @Override
    protected String readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readString();
    }

    @Override
    protected void setFromList(List<String> list)
    {
        data = new String[list.size()];

        for (int i = 0; i < data.length; ++i)
        {
            data[i] = list.get(i);
        }
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new String[length];

        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = readBoxedElement(reader, numBits);
        }
    }

    protected String[] data;
}
