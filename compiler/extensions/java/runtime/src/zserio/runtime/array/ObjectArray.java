package zserio.runtime.array;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.VarUInt64Util;
import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.SizeOf;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.InitializeOffsetsWriter;

/**
 * Implements Zserio object arrays.
 *
 * Zserio object arrays can be structure, choice or enum arrays. They are mapped to Java
 * List<E> type.
 *
 * @param <E> The type of elements (objects) maintained by this array.
 */
public class ObjectArray<E extends SizeOf> implements Array<E>
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param factory Element factory to create elements.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public ObjectArray(BitStreamReader reader, int length, ElementFactory<E> factory)
            throws IOException, ZserioError
    {
        this(reader, length, factory, null);
    }

    /**
     * Constructs array from bit stream applying offset checking.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param factory Element factory to create elements.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public ObjectArray(BitStreamReader reader, int length, ElementFactory<E> factory,
            OffsetChecker checker) throws IOException, ZserioError
    {
        data = new ArrayList<E>();
        read(reader, length, factory, checker);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be null.
     *
     * @param length Number of elements for created object.
     */
    public ObjectArray(int length)
    {
        data = new ArrayList<E>(Collections.nCopies(length, (E)null));
    }

    /**
     * Constructs array from given list of objects.
     *
     * @param data List of objects to construct from.
     */
    public ObjectArray(List<E> data)
    {
        this.data = data;
    }

    /**
     * Returns element at the given position.
     *
     * @param i Index of element to return.
     *
     * @return Element at the given position.
     */
    public E elementAt(int i)
    {
        return data.get(i);
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(E value, int i)
    {
        data.set(i, value);
    }

    /**
     * Maps the array with the given mapping.
     *
     * @param mapping The mapping to use.
     *
     * @return New array with mapped elements.
     */
    public ObjectArray<E> typedMap(Mapping<E> mapping)
    {
        final List<E> result = new ArrayList<E>(data.size());
        for (int i = 0; i < data.size(); i++)
        {
            result.add(i, mapping.map(data.get(i)));
        }
        return new ObjectArray<E>(result);
    }

    @Override
    public Array<E> map(Mapping<E> mapping)
    {
        return typedMap(mapping);
    }

    @Override
    public Array<E> subRange(int offset, int length)
    {
        return new ObjectArray<E>(data.subList(offset, offset + length));
    }

    @Override
    public int length()
    {
        return data.size();
    }

    @Override
    public int hashCode()
    {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ObjectArray<?>)
        {
            final ObjectArray<?> that = (ObjectArray<?>)obj;
            return this.data.equals(that.data);
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
     * @throws IOException     Failure during bit stream manipulation.
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
     * @throws IOException     Failure during bit stream manipulation.
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
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Calling on object with writing interface
     */
    public void writeAlignedAuto(BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarUInt64(data.size());
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
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking or calling on object with writing interface.
     */
    public void writeAligned(BitStreamWriter writer, OffsetChecker checker) throws IOException, ZserioError
    {
        for (int index = 0; index < data.size(); index++)
        {
            if (checker != null)
            {
                writer.alignTo(Byte.SIZE);
                checker.checkOffset(index, writer.getBytePosition());
            }

            final E element = data.get(index);
            // This exception cannot occur in Zserio generated code because write method is not called when
            // writing interface is disabled.
            if (!(element instanceof InitializeOffsetsWriter))
                throw new ZserioError("Called ObjectArray write on object without writing interface!");
            ((InitializeOffsetsWriter)element).write(writer, false);
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
        for (E value : data)
            endBitPosition += value.bitSizeOf(endBitPosition);

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
        return BitSizeOfCalculator.getBitSizeOfVarUInt64(length()) + bitSizeOf(bitPosition);
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
        return BitSizeOfCalculator.getBitSizeOfVarUInt64(length()) + bitSizeOfAligned(bitPosition);
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
        long endBitPosition = bitPosition;
        for (E value : data)
        {
            endBitPosition = BitPositionUtil.alignTo(Byte.SIZE, endBitPosition);
            endBitPosition += value.bitSizeOf(endBitPosition);
        }

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
        long currentBitPosition = bitPosition;
        for (E element : data)
        {
            // This exception cannot occur in Zserio generated code if this method is called when writing
            // interface is disabled.
            if (!(element instanceof InitializeOffsetsWriter))
                throw new ZserioError("Called ObjectArray initializeOffsets on object without writing " +
                        "interface!");
            currentBitPosition = ((InitializeOffsetsWriter)element).initializeOffsets(currentBitPosition);
        }

        return currentBitPosition;
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
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarUInt64(data.size());

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
        final long currentBitPosition = bitPosition + BitSizeOfCalculator.getBitSizeOfVarUInt64(data.size());

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
        for (int index = 0; index < data.size(); index++)
        {
            currentBitPosition = BitPositionUtil.alignTo(Byte.SIZE, currentBitPosition);
            setter.setOffset(index, BitPositionUtil.bitsToBytes(currentBitPosition));

            final E element = data.get(index);
            // This exception cannot occur in Zserio generated code if this method is called when writing
            // interface is disabled.
            if (!(element instanceof InitializeOffsetsWriter))
                throw new ZserioError("Called ObjectArray initializeOffsetsAligned on object without " +
                        "writing interface!");
            currentBitPosition = ((InitializeOffsetsWriter)element).initializeOffsets(currentBitPosition);
        }

        return currentBitPosition;
    }

    /**
     * Removes the given object from the object array.
     *
     * @param obj Object to remove.
     */
    public void remove(Object obj)
    {
        data.remove(obj);
    }

    /**
     * Gets the underlying data list which holds object array.
     *
     * @return Data list which contain all objects stored in array.
     */
    public List<E> getData()
    {
        return data;
    }

    @Override
    public Iterator<E> iterator()
    {
        return data.iterator();
    }

    private void read(BitStreamReader reader, int length, ElementFactory<E> factory, OffsetChecker checker)
            throws IOException, ZserioError
    {
        if (length == IMPLICIT_LENGTH)
        {
            // indexed offsets don't make sense for implicit-length arrays
            if (checker != null)
                throw new ZserioError("ObjectArray: Implicit arrays can't have indexed offsets.");

            readAll(reader, factory);
        }
        else
        {
            final int realLength = (length != AUTO_LENGTH) ? length :
                VarUInt64Util.convertVarUInt64ToArraySize(reader.readVarUInt64());
            // check offsets if checker != null
            readN(reader, realLength, factory, checker);
        }
    }

    private void readAll(BitStreamReader reader, ElementFactory<E> factory) throws IOException, ZserioError
    {
        long bitPosition = 0;
        int index = 0;

        try
        {
            while (true)
            {
                bitPosition = reader.getBitPosition();
                data.add(factory.create(reader, index++));
            }
        }
        catch (IOException e)
        {
            reader.setBitPosition(bitPosition);
        }
    }

    private void readN(BitStreamReader reader, int length, ElementFactory<E> factory,
            OffsetChecker checker) throws IOException, ZserioError
    {
        // would have to specifically use ArrayList: data.ensureCapacity(length);
        for (int index = 0; index < length; index++)
        {
            if (checker != null)
            {
                reader.alignTo(Byte.SIZE);
                checker.checkOffset(index, reader.getBytePosition());
            }
            data.add(factory.create(reader, index));
        }
    }

    private List<E> data;
}
