package zserio.runtime.array;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio varuint arrays.
 *
 * Zserio varuint arrays are mapped to Java BigInteger[] type.
 */
public class VarUIntArray extends NumericArrayBase<BigInteger>
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader Bit stream reader to construct from.
     * @param length Number of elements to read from given bit stream. "-1" specifies implicit-length array.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError     Cannot occur because indexed offsets are not used.
     */
    public VarUIntArray(BitStreamReader reader, int length) throws IOException, ZserioError
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
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError     Failure during offset checking.
     */
    public VarUIntArray(BitStreamReader reader, int length, OffsetChecker checker)
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
    public VarUIntArray(int length)
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
    public VarUIntArray(BigInteger[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("VarUIntArray: Requested array sequence ends beyond " +
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
        final VarUIntArray result = new VarUIntArray(data.length);
        for (int i = 0; i < data.length; i++)
        {
            result.setElementAt(mapping.map(elementAt(i)), i);
        }
        return result;
    }

    @Override
    public Array<BigInteger> subRange(int offset, int length)
    {
        return new VarUIntArray(data, offset, length);
    }

    @Override
    public int length()
    {
        return data.length;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VarUIntArray)
        {
            final VarUIntArray that = (VarUIntArray)obj;

            return Arrays.equals(data, that.data);
        }

        return false;
    }

    /**
     * Writes array to bit stream.
     *
     * @param writer Bit stream where to write.
     *
     * @throws IOException     Failure during bit stream manipulation.
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
     * @throws IOException     Failure during bit stream manipulation.
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
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAlignedAuto(BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarUInt64(data.length);
        writeAligned(writer, checker);
    }

    /**
     * Writes aligned array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param checker offset checker to use.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAligned(BitStreamWriter writer, OffsetChecker checker) throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeVarUInt(data[index]);
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
        for (BigInteger value : data)
            endBitPosition += BitSizeOfCalculator.getBitSizeOfVarUInt(value);

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
        return BitSizeOfCalculator.getBitSizeOfVarUInt64(data.length) + bitSizeOf(bitPosition);
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
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarUInt64(data.length);

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
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarUInt64(data.length);

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
            currentBitPosition += BitSizeOfCalculator.getBitSizeOfVarUInt(data[index]);
        }

        return currentBitPosition;
    }

    @Override
    protected BigInteger boxedElementAt(int index)
    {
        return data[index];
    }

    @Override
    protected BigInteger readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readVarUInt();
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
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new BigInteger[length];

        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = reader.readVarUInt();
        }
    }

    private BigInteger[] data;
}
