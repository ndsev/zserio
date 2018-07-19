package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio zserio uint8 and bit:1...bit:8 arrays.
 *
 * Zserio uint8 and bit:1...bit:8 arrays are mapped to Java short[] type.
 */
public class UnsignedByteArray extends ShortArrayBase
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param numBits Length of element in bits.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public UnsignedByteArray(BitStreamReader reader, int length, int numBits)
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
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public UnsignedByteArray(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
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
    public UnsignedByteArray(int length)
    {
        super(length);
    }

    /**
     * Constructs array from short array.
     *
     * @param data   short array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public UnsignedByteArray(short[] data, int offset, int length)
    {
        super(data, offset, length);
    }

    @Override
    public Array<Short> map(Mapping<Short> mapping)
    {
        final UnsignedByteArray result = new UnsignedByteArray(data.length);
        mapImpl(mapping, result);
        return result;
    }

    @Override
    public Array<Short> subRange(int offset, int length)
    {
        return new UnsignedByteArray(data, offset, length);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof UnsignedByteArray)
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
     * @param checker offset checker to use.
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
    protected Short readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return (short)reader.readBits(numBits);
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new short[length];

        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);

            data[index] = (short)reader.readBits(numBits);
        }
    }
}
