package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio bit:33...bit:63 arrays.
 *
 * Zserio bit:33...bit:63 arrays are mapped to Java long[] type.
 */
public class UnsignedLongArray extends LongArrayBase
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param numBits Length of element in bits.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public UnsignedLongArray(BitStreamReader reader, int length, int numBits)
            throws IOException, ZserioError
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
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public UnsignedLongArray(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        readImpl(reader, length, numBits, checker);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be zero.
     *
     * @param length Number of elements for created object.
     */
    public UnsignedLongArray(int length)
    {
        super(length);
    }

    /**
     * Constructs array from long array.
     *
     * @param data   long array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public UnsignedLongArray(long[] data, int offset, int length)
    {
        super(data, offset, length);
    }

    @Override
    public Array<Long> map(Mapping<Long> mapping)
    {
        final UnsignedLongArray result = new UnsignedLongArray(data.length);
        mapImpl(mapping, result);
        return result;
    }

    @Override
    public Array<Long> subRange(int offset, int length)
    {
        return new UnsignedLongArray(data, offset, length);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof UnsignedLongArray)
        {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Writes array to bit stream.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     *
     * @throws IOException Failure during bit stream manipulation.
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
     * @throws IOException Failure during bit stream manipulation.
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
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAlignedAuto(BitStreamWriter writer, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarSize(data.length);
        writeAligned(writer, numBits, checker);
    }

    /**
     * Writes aligned array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param numBits Length of element in bits.
     * @param checker offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAligned(BitStreamWriter writer, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeBits(data[index], numBits);
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
    protected Long readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readBits(numBits);
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new long[length];

        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = reader.readBits(numBits);
        }
    }
}
