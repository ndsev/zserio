package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio varuint64 arrays.
 *
 * Zserio varuint64 arrays are mapped to Java long[] type.
 * <p>
 * <b>Note:</b> Zserio varuint64 type can hold only 57-bits unsigned value at maximum (for more information about
 * variable integer format, please consult the chapter 3.4 in Zserio Language Overview). Therefore
 * mapping to Java long type is enough (Java long type can address 57-bits unsigned value).
 * </p>
 */
public class VarUInt64Array extends LongArrayBase
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader Bit stream reader to construct from.
     * @param length Number of elements to read from given bit stream. "-1" specifies implicit-length array.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Cannot occured because indexed offsets are not used.
     */
    public VarUInt64Array(BitStreamReader reader, int length) throws IOException, ZserioError
    {
        this(reader, length, null);
    }

    /**
     * Constructs array from bit stream applying offset checking.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream. "-1" specifies implicit-length array.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public VarUInt64Array(BitStreamReader reader, int length, OffsetChecker checker)
            throws IOException, ZserioError
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
    public VarUInt64Array(int length)
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
    public VarUInt64Array(long[] data, int offset, int length)
    {
        super(data, offset, length);
    }

    @Override
    public Array<Long> map(Mapping<Long> mapping)
    {
        final VarUInt64Array result = new VarUInt64Array(data.length);
        mapImpl(mapping, result);
        return result;
    }

    @Override
    public Array<Long> subRange(int offset, int length)
    {
        return new VarUInt64Array(data, offset, length);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VarUInt64Array)
        {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Writes array to bit stream.
     *
     * @param writer Bit stream where to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void write(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAligned(writer, null);
    }

    /**
     * Writes auto length array to bit stream.
     *
     * @param writer  Bit stream where to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void writeAuto(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAlignedAuto(writer, null);
    }

    /**
     * Writes aligned auto length array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param checker Offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAlignedAuto(BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarSize(data.length);
        writeAligned(writer, checker);
    }

    /**
     * Writes aligned array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param checker offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAligned(BitStreamWriter writer, OffsetChecker checker) throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeVarUInt64(data[index]);
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
        for (long value : data)
            endBitPosition += BitSizeOfCalculator.getBitSizeOfVarUInt64(value);

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
        return BitSizeOfCalculator.getBitSizeOfVarSize(data.length) + bitSizeOf(bitPosition);
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
        return bitSizeOfAuto(bitPosition);
    }

    /**
     * Returns length of aligned array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAligned(long bitPosition)
    {
        long endBitPosition = bitPosition;
        endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, endBitPosition);
        endBitPosition += bitSizeOf(endBitPosition);

        return (int)(endBitPosition - bitPosition);
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
        currentBitPosition = BitPositionUtil.alignTo(Byte.SIZE, currentBitPosition);
        for (int index = 0; index < data.length; index++)
        {
            setter.setOffset(index, BitPositionUtil.bitsToBytes(currentBitPosition));
            currentBitPosition += BitSizeOfCalculator.getBitSizeOfVarUInt64(data[index]);
        }

        return currentBitPosition;
    }

    @Override
    protected Long readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readVarUInt64();
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new long[length];

        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = reader.readVarUInt64();
        }
    }
}
