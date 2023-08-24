package zserio.runtime.array;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.array.ArrayElement.IntegralArrayElement;
import zserio.runtime.array.ArrayTraits.IntegralArrayTraits;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Context for delta packing created for each packable field.
 *
 * Contexts are always newly created for each array operation (bitSizeOfPacked, initializeOffsetsPacked,
 * readPacked, writePacked). They must be initialized at first via calling the init method for each packable
 * element present in the array. After the full initialization, only a single method (bitSizeOf, read, write)
 * can be repeatedly called for exactly the same sequence of packable elements.
 */
public class DeltaContext extends PackingContext
{
    /**
     * Calls the initialization step for a single element.
     *
     * @param arrayTraits Standard array traits.
     * @param element Current element.
     */
    public void init(IntegralArrayTraits arrayTraits, IntegralArrayElement element)
    {
        numElements++;
        unpackedBitSize += bitSizeOfUnpacked(arrayTraits, element);

        if (previousElement == null)
        {
            previousElement = element.toBigInteger();
            firstElementBitSize = (byte)unpackedBitSize;
        }
        else
        {
            if (maxBitNumber <= MAX_BIT_NUMBER_LIMIT)
            {
                isPacked = true;
                final BigInteger bigElement = element.toBigInteger();
                final BigInteger delta = bigElement.subtract(previousElement);
                final byte maxBitNumber = bitLength(delta);
                if (maxBitNumber > this.maxBitNumber)
                {
                    this.maxBitNumber = maxBitNumber;
                    if (maxBitNumber > MAX_BIT_NUMBER_LIMIT)
                        isPacked = false;
                }
                previousElement = bigElement;
            }
        }
    }

    /**
     * Returns length of the packed element stored in the bit stream in bits.
     *
     * @param arrayTraits Standard array traits.
     * @param element Value of the current element.
     *
     * @return Length of the packed element stored in the bit stream in bits.
     */
    public int bitSizeOf(IntegralArrayTraits arrayTraits, IntegralArrayElement element)
    {
        if (!processingStarted)
        {
            processingStarted = true;
            finishInit();

            return bitSizeOfDescriptor() + bitSizeOfUnpacked(arrayTraits, element);
        }
        else if (!isPacked)
        {
            return bitSizeOfUnpacked(arrayTraits, element);
        }
        else
        {
            return maxBitNumber + (maxBitNumber > 0 ? 1 : 0);
        }
    }

    /**
     * Reads a packed element from the bit stream.
     *
     * @param arrayTraits Standard array traits.
     * @param reader Bit stream reader.
     *
     * @return Packed element.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public IntegralArrayElement read(IntegralArrayTraits arrayTraits, BitStreamReader reader) throws IOException
    {
        if (!processingStarted)
        {
            processingStarted = true;
            readDescriptor(reader);

            return readUnpacked(arrayTraits, reader);
        }
        else if (!isPacked)
        {
            return readUnpacked(arrayTraits, reader);
        }
        else
        {
            if (maxBitNumber > 0)
            {
                final BigInteger delta = BigInteger.valueOf(reader.readSignedBits(maxBitNumber + 1));
                previousElement = previousElement.add(delta);
            }

            return arrayTraits.fromBigInteger(previousElement);
        }
    }

    /**
     * Writes the packed element to the bit stream.
     *
     * @param arrayTraits Standard array traits.
     * @param writer Bit stream writer.
     * @param element Current element.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public void write(IntegralArrayTraits arrayTraits, BitStreamWriter writer, IntegralArrayElement element)
            throws IOException
    {
        if (!processingStarted)
        {
            processingStarted = true;
            finishInit();
            writeDescriptor(writer);

            writeUnpacked(arrayTraits, writer, element);
        }
        else if (!isPacked)
        {
            writeUnpacked(arrayTraits, writer, element);
        }
        else
        {
            if (maxBitNumber > 0)
            {
                final BigInteger bigElement = element.toBigInteger();
                final BigInteger delta = bigElement.subtract(previousElement);
                writer.writeSignedBits(delta.longValue(), maxBitNumber + 1);
                previousElement = bigElement;
            }
        }
    }

    private void finishInit()
    {
        if (isPacked)
        {
            final int deltaBitSize = maxBitNumber + (maxBitNumber > 0 ? 1 : 0);
            final int packedBitSizeWithDescriptor = 1 + MAX_BIT_NUMBER_BITS + // descriptor
                    firstElementBitSize + (numElements - 1) * deltaBitSize;
            final int unpackedBitSizeWithDescriptor = 1 + unpackedBitSize;
            if (packedBitSizeWithDescriptor >= unpackedBitSizeWithDescriptor)
                isPacked = false;
        }
    }

    private int bitSizeOfDescriptor()
    {
        return isPacked ? 1 + MAX_BIT_NUMBER_BITS : 1;
    }

    private static int bitSizeOfUnpacked(IntegralArrayTraits arrayTraits, IntegralArrayElement element)
    {
        return arrayTraits.bitSizeOf(element);
    }

    private void readDescriptor(BitStreamReader reader) throws IOException
    {
        isPacked = reader.readBool();
        if (isPacked)
            maxBitNumber = (byte)reader.readBits(MAX_BIT_NUMBER_BITS);
    }

    private IntegralArrayElement readUnpacked(IntegralArrayTraits arrayTraits, BitStreamReader reader)
            throws IOException
    {
        final IntegralArrayElement element = arrayTraits.read(reader);
        previousElement = element.toBigInteger();
        return element;
    }

    private void writeDescriptor(BitStreamWriter writer) throws IOException
    {
        writer.writeBool(isPacked);
        if (isPacked)
            writer.writeBits(maxBitNumber, MAX_BIT_NUMBER_BITS);
    }

    private void writeUnpacked(IntegralArrayTraits arrayTraits, BitStreamWriter writer,
            IntegralArrayElement element) throws IOException
    {
        previousElement = element.toBigInteger();
        arrayTraits.write(writer,  element);
    }

    private static byte bitLength(BigInteger element)
    {
        // need to call abs() first to get the same behavior as in Python and C++
        return (byte)element.abs().bitLength();
    }

    private static final byte MAX_BIT_NUMBER_BITS = 6;
    private static final byte MAX_BIT_NUMBER_LIMIT = 62;

    private BigInteger previousElement; // BigInteger covers all integral array element values
    private byte maxBitNumber = 0;
    private boolean isPacked = false;
    private boolean processingStarted = false;

    private byte firstElementBitSize = 0;
    private int numElements = 0;
    private int unpackedBitSize = 0;
}
