package zserio.runtime.array;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import zserio.runtime.ZserioError;
import zserio.runtime.VarUInt64Util;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * This class provides basic implementation for arrays.
 *
 * @param <E> The type of elements maintained by this array.
 */
abstract class ArrayBase<E> implements Array<E>
{
    @Override
    public Iterator<E> iterator()
    {
        return new ArrayBaseIterator();
    }

    /**
     * Retrieves a boxed (non-primitive) element by its index.
     *
     * @param index The index of the element to read.
     *
     * @return The element value at the given index.
     */
    protected abstract E boxedElementAt(int index);

    /**
     * Reads from bit stream a single element as a boxed type.
     *
     * @param reader  Bit stream to read from.
     * @param numBits Length of element in bits.
     *
     * @return Read element value.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    protected abstract E readBoxedElement(BitStreamReader reader, int numBits) throws IOException;

    /**
     * Sets the contents of the array from a given list.
     *
     * @param list List to use for setting.
     */
    protected abstract void setFromList(List<E> list);

    /**
     * Reads a given number of elements from bit stream. length is always non-negative.
     *
     * @param reader  Bit stream to read from.
     * @param length  Number of element to read (it is always non-negative).
     * @param numBits Length of element in bits.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    protected abstract void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError;

    /**
     * Reads a given number of elements from bit stream applying offset checking.
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
    protected void readImpl(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        if (length == IMPLICIT_LENGTH)
        {
            // indexed offsets don't make sense for implicit-length arrays
            if (checker != null)
                throw new ZserioError("ArrayBase: Implicit arrays can't have indexed offsets.");

            readAll(reader, numBits);
        }
        else
        {
            final int realLength = (length != AUTO_LENGTH) ? length :
                    VarUInt64Util.convertVarUInt64ToArraySize(reader.readVarUInt64());
            readN(reader, realLength, numBits, checker);
        }
    }

    /**
     * Aligns bit stream writer and checks offsets.
     *
     * @param index   Index of element for which to check offset.
     * @param writer  Bit stream writer to use.
     * @param checker Offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    protected void alignAndCheckOffset(int index, BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        if (checker != null)
        {
            writer.alignTo(Byte.SIZE);
            checker.checkOffset(index, writer.getBytePosition());
        }
        // else: elements are not indexed, so they are not aligned
    }

    /**
     * Aligns bit stream reader and checks offsets.
     *
     * @param index   Index of element for which to check offset.
     * @param reader  Bit stream reader to use.
     * @param checker Offset checker to use.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    protected void alignAndCheckOffset(int index, BitStreamReader reader, OffsetChecker checker)
            throws IOException, ZserioError
    {
        if (checker != null)
        {
            reader.alignTo(Byte.SIZE);
            checker.checkOffset(index, reader.getBytePosition());
        }
        // else: elements are not indexed, so they are not aligned
    }

    /**
     * The iterator for the zserio arrays.
     */
    protected class ArrayBaseIterator implements Iterator<E>
    {
        @Override
        public boolean hasNext()
        {
            return index < length();
        }

        @Override
        public E next()
        {
            if (!hasNext())
                throw new NoSuchElementException("ArrayBase: No next element for iterator.");

            return boxedElementAt(index++);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("ArrayBase: Iterator method remove() is not supported.");
        }

        private int index;
    }

    private void readAll(BitStreamReader reader, int numBits) throws IOException
    {
        long bitPosition = 0;
        final List<E> list = new ArrayList<E>();

        try
        {
            while (true)
            {
                bitPosition = reader.getBitPosition();
                list.add(readBoxedElement(reader, numBits));
            }
        }
        catch (IOException e)
        {
            reader.setBitPosition(bitPosition);
        }

        setFromList(list);
    }
}
