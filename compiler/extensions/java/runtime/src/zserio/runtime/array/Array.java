package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.OffsetChecker;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Array wrapper which is used for mapping of all zserio arrays.
 *
 * Array wrapper is defined by:
 * - raw array
 * - array traits which define zserio methods for array elements of corresponding Java native types
 * - array type (normal, auto, implicit)
 * - offset initializer to set offsets for indexed offsets arrays
 * - offset checker to check offsets for indexed offsets arrays
 */
public class Array
{
    /**
     * Constructor.
     *
     * @param rawArray    Raw array to construct from.
     * @param arrayTraits Array traits to construct from.
     * @param arrayType   Array type to construct from.
     */
    public Array(RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType)
    {
        this(rawArray, arrayTraits, arrayType, null, null);
    }

    /**
     * Constructor.
     *
     * @param rawArray      Raw array to construct from.
     * @param arrayTraits   Array traits to construct from.
     * @param arrayType     Array type to construct from.
     * @param offsetChecker Offset checker to construct from.
     */
    public Array(RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType, OffsetChecker offsetChecker)
    {
        this(rawArray, arrayTraits, arrayType, offsetChecker, null);
    }

    /**
     * Constructor.
     *
     * @param rawArray          Raw array to construct from.
     * @param arrayTraits       Array traits to construct from.
     * @param arrayType         Array type to construct from.
     * @param offsetChecker     Offset checker to construct from.
     * @param offsetInitializer Offset initializer to construct from.
     */
    public Array(RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType, OffsetChecker offsetChecker,
            OffsetInitializer offsetInitializer)
    {
        this.rawArray = rawArray;
        this.arrayTraits = arrayTraits;
        this.arrayType = arrayType;
        this.offsetChecker = offsetChecker;
        this.offsetInitializer = offsetInitializer;
    }

    /**
     * Constructor.
     *
     * @param reader      Bit stream reader to read from.
     * @param rawArray    Raw array to construct from.
     * @param arrayTraits Array traits to construct from.
     * @param arrayType   Array type to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType)
            throws IOException, ZserioError
    {
        this(reader, rawArray, arrayTraits, arrayType, null, null);
    }

    /**
     * Constructor.
     *
     * @param reader        Bit stream reader to read from.
     * @param rawArray      Raw array to construct from.
     * @param arrayTraits   Array traits to construct from.
     * @param arrayType     Array type to construct from.
     * @param offsetChecker Offset checker to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType,
            OffsetChecker offsetChecker) throws IOException, ZserioError
    {
        this(reader, rawArray, arrayTraits, arrayType, offsetChecker, null);
    }

    /**
     * Constructor.
     *
     * @param reader            Bit stream reader to read from.
     * @param rawArray          Raw array to construct from.
     * @param arrayTraits       Array traits to construct from.
     * @param arrayType         Array type to construct from.
     * @param offsetChecker     Offset checker to construct from.
     * @param offsetInitializer Offset initializer to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, RawArray rawArray, ArrayTraits arrayTraits, ArrayType arrayType,
            OffsetChecker offsetChecker, OffsetInitializer offsetInitializer) throws IOException, ZserioError
    {
        this(rawArray, arrayTraits, arrayType, offsetChecker, offsetInitializer);

        read(reader);
    }

    /**
     * Constructor.
     *
     * @param reader      Bit stream reader to read from.
     * @param size        Number of elements stored in the array which shall be read.
     * @param rawArray    Raw array to construct from.
     * @param arrayTraits Array traits to construct from.
     * @param arrayType   Array type to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, int size, RawArray rawArray, ArrayTraits arrayTraits,
            ArrayType arrayType) throws IOException, ZserioError
    {
        this(reader, size, rawArray, arrayTraits, arrayType, null, null);
    }

    /**
     * Constructor.
     *
     * @param reader        Bit stream reader to read from.
     * @param size          Number of elements stored in the array which shall be read.
     * @param rawArray      Raw array to construct from.
     * @param arrayTraits   Array traits to construct from.
     * @param arrayType     Array type to construct from.
     * @param offsetChecker Offset checker to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, int size, RawArray rawArray, ArrayTraits arrayTraits,
            ArrayType arrayType, OffsetChecker offsetChecker) throws IOException, ZserioError
    {
        this(reader, size, rawArray, arrayTraits, arrayType, offsetChecker, null);
    }

    /**
     * Constructor.
     *
     * @param reader            Bit stream reader to read from.
     * @param size              Number of elements stored in the array which shall be read.
     * @param rawArray          Raw array to construct from.
     * @param arrayTraits       Array traits to construct from.
     * @param arrayType         Array type to construct from.
     * @param offsetChecker     Offset checker to construct from.
     * @param offsetInitializer Offset initializer to construct from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public Array(BitStreamReader reader, int size, RawArray rawArray, ArrayTraits arrayTraits,
            ArrayType arrayType, OffsetChecker offsetChecker, OffsetInitializer offsetInitializer)
                    throws IOException, ZserioError
    {
        this(rawArray, arrayTraits, arrayType, offsetChecker, offsetInitializer);

        read(reader, size);
    }

    @Override
    public boolean equals(java.lang.Object obj)
    {
        return (obj instanceof Array) ? rawArray.equals(((Array)obj).rawArray) : false;
    }

    @Override
    public int hashCode()
    {
        return rawArray.hashCode();
    }

    /**
     * Gets the underlying raw array.
     *
     * @return Underlying raw array.
     */
    public <T> T getRawArray()
    {
        return rawArray.getRawArray();
    }

    /**
     * Gets the underlying raw array size. !@# why TODO[mikir]
     *
     * @return The number of elements stored in the underlying raw array.
     */
    public int size()
    {
        return rawArray.size();
    }

    /**
     * Gets the bit size of the array if it is stored in the bit stream.
     *
     * @param bitPosition Current bit position in the bit stream.
     *
     * @return Bit size of the array if it is stored in the bit stream.
     */
    public int bitSizeOf(long bitPosition)
    {
        long endBitPosition = bitPosition;
        final int size = rawArray.size();
        if (arrayType == ArrayType.AUTO)
            endBitPosition += BitSizeOfCalculator.getBitSizeOfVarSize(size);

        if (arrayTraits.isBitSizeOfConstant() && size > 0)
        {
            final int elementSize = arrayTraits.bitSizeOf(endBitPosition, ArrayElement.Dummy);
            if (offsetInitializer == null)
            {
                endBitPosition += size * elementSize;
            }
            else
            {
                endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, endBitPosition);
                endBitPosition += elementSize + (size - 1) * BitPositionUtil.alignTo(Byte.SIZE, elementSize);
            }
        }
        else
        {
            for (int index = 0; index < size; ++index)
            {
                if (offsetInitializer != null)
                {
                    endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, endBitPosition);
                }
                endBitPosition += arrayTraits.bitSizeOf(endBitPosition, rawArray.getElement(index));
            }
        }

        return (int)(endBitPosition - bitPosition);
    }

    /**
     * Initializes indexed offsets for the array.
     *
     * @param bitPosition Current bit position in the bit stream.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsets(long bitPosition)
    {
        long endBitPosition = bitPosition;
        final int size = rawArray.size();
        if (arrayType == ArrayType.AUTO)
            endBitPosition += BitSizeOfCalculator.getBitSizeOfVarSize(size);

        for (int index = 0; index < size; ++index)
        {
            if (offsetInitializer != null)
            {
                endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, endBitPosition);
                offsetInitializer.setOffset(index, BitPositionUtil.bitsToBytes(endBitPosition));
            }
            endBitPosition = arrayTraits.initializeOffsets(endBitPosition, rawArray.getElement(index));
        }

        return endBitPosition;
    }

    /**
     * Reads the array from the bit stream.
     *
     * @param reader Bit stream reader to read from.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void read(BitStreamReader reader) throws IOException, ZserioError
    {
        read(reader, -1);
    }

    /**
     * Reads the array from the bit stream.
     *
     * @param reader Bit stream reader to read from.
     * @param size   Number of elements stored in the array which shall be read.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void read(BitStreamReader reader, int size) throws IOException, ZserioError
    {
        int readSize = size;
        if (arrayType == ArrayType.IMPLICIT)
        {
            if (!arrayTraits.isBitSizeOfConstant())
                throw new UnsupportedOperationException(
                        "Array: Implicit array elements must have constant bit size!");

            final long readerBitPosition = reader.getBitPosition();
            final int elementSize = arrayTraits.bitSizeOf(readerBitPosition, ArrayElement.Dummy);
            final long remainingBits = reader.getBufferBitSize() - readerBitPosition;
            readSize = (int)(remainingBits / elementSize);
        }
        else if (arrayType == ArrayType.AUTO)
        {
            readSize = reader.readVarSize();
        }

        rawArray.reset(readSize);

        for (int index = 0; index < readSize; ++index)
        {
            if (offsetChecker != null)
            {
                reader.alignTo(Byte.SIZE);
                offsetChecker.checkOffset(index, reader.getBytePosition());
            }
            final ArrayElement element = arrayTraits.read(reader, index);
            rawArray.setElement(element, index);
        }
    }

    /**
     * Writes the array element to the bit stream.
     *
     * @param writer Bit stream write to write to.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void write(BitStreamWriter writer) throws IOException, ZserioError
    {
        final int size = rawArray.size();
        if (arrayType == ArrayType.AUTO)
            writer.writeVarSize(size);

        for (int index = 0; index < size; ++index)
        {
            if (offsetChecker != null)
            {
                writer.alignTo(Byte.SIZE);
                offsetChecker.checkOffset(index, writer.getBytePosition());
            }
            arrayTraits.write(writer, rawArray.getElement(index));
        }
    }

    private final RawArray rawArray;
    private final ArrayTraits arrayTraits;
    private final ArrayType arrayType;
    private final OffsetChecker offsetChecker;
    private final OffsetInitializer offsetInitializer;
}
