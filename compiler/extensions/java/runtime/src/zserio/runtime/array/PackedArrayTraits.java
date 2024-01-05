package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.PackableSizeOf;
import zserio.runtime.array.ArrayElement.IntegralArrayElement;
import zserio.runtime.array.ArrayTraits.IntegralArrayTraits;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.PackableWriter;

/**
 * Interface for packed array traits.
 */
public interface PackedArrayTraits
{
    /**
     * Creates packing context.
     *
     * @return Packing context node with the created context.
     */
    public PackingContext createContext();

    /**
     * Calls context initialization step for the current element.
     *
     * @param context Packing context node which keeps the context.
     * @param element Current element.
     */
    public void initContext(PackingContext context, ArrayElement element);

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * @param context Packing context node.
     * @param bitPosition Current bit stream position.
     * @param element Current element.
     *
     * @return Length of the array element stored in the bit stream in bits.
     */
    public int bitSizeOf(PackingContext context, long bitPosition, ArrayElement element);

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * @param context Packing context node.
     * @param bitPosition Current bit stream position.
     * @param element Current element.
     *
     * @return Updated bit stream position which points to the first bit after this element.
     */
    public long initializeOffsets(PackingContext context, long bitPosition, ArrayElement element);

    /**
     * Reads an element from the bit stream.
     *
     * @param context Packing context node.
     * @param reader Bit stream reader.
     * @param index Index of the current element.
     *
     * @return Read element.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public ArrayElement read(PackingContext context, BitStreamReader reader, int index) throws IOException;

    /**
     * Writes the element to the bit stream.
     *
     * @param context Packing context node.
     * @param writer Bit stream writer.
     * @param element Element to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public void write(PackingContext context, BitStreamWriter writer, ArrayElement element) throws IOException;

    /**
     * Packed array traits for arrays of integral types.
     *
     * Works with single DeltaContext.
     */
    public static final class IntegralPackedArrayTraits implements PackedArrayTraits
    {
        /**
         * Constructor.
         *
         * @param arrayTraits Standard integral array traits.
         */
        public IntegralPackedArrayTraits(IntegralArrayTraits arrayTraits)
        {
            this.arrayTraits = arrayTraits;
        }

        @Override
        public PackingContext createContext()
        {
            return new DeltaContext();
        }

        @Override
        public void initContext(PackingContext context, ArrayElement element)
        {
            final DeltaContext deltaContext = context.cast();
            deltaContext.init(arrayTraits, (IntegralArrayElement)element);
        }

        @Override
        public int bitSizeOf(PackingContext context, long bitPosition, ArrayElement element)
        {
            final DeltaContext deltaContext = context.cast();
            return deltaContext.bitSizeOf(arrayTraits, (IntegralArrayElement)element);
        }

        @Override
        public long initializeOffsets(PackingContext context, long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(context, bitPosition, element);
        }

        @Override
        public IntegralArrayElement read(PackingContext context, BitStreamReader reader, int index)
                throws IOException
        {
            final DeltaContext deltaContext = context.cast();
            return deltaContext.read(arrayTraits, reader);
        }

        @Override
        public void write(PackingContext context, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            final DeltaContext deltaContext = context.cast();
            deltaContext.write(arrayTraits, writer, (IntegralArrayElement)element);
        }

        private final IntegralArrayTraits arrayTraits;
    }

    /**
     * Packed array traits for zserio object arrays (without writer part).
     */
    public static class ObjectPackedArrayTraits<E extends PackableSizeOf> implements PackedArrayTraits
    {
        /**
         * Constructor.
         *
         * @param elementFactory Element factory to construct from.
         */
        public ObjectPackedArrayTraits(PackableElementFactory<E> elementFactory)
        {
            this.elementFactory = elementFactory;
        }

        @Override
        public PackingContext createContext()
        {
            return elementFactory.createPackingContext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initContext(PackingContext context, ArrayElement element)
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().initPackingContext(context);
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(PackingContext context, long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().bitSizeOf(context, bitPosition);
        }

        @Override
        public long initializeOffsets(PackingContext context, long bitPosition, ArrayElement element)
        {
            throw new UnsupportedOperationException("PackedArrayTraits: "
                    + "initializeOffsets is not implemented for read only PackedObjectArrayTraits!");
        }

        @Override
        public ArrayElement read(PackingContext context, BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.ObjectArrayElement<>(elementFactory.create(context, reader, index));
        }

        @Override
        public void write(PackingContext context, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            throw new UnsupportedOperationException(
                    "PackedArrayTraits: write is not implemented for read only PackedObjectArrayTraits!");
        }

        private final PackableElementFactory<E> elementFactory;
    }

    /**
     * Packed array traits for zserio object arrays (with writer part).
     */
    public static final class WriteObjectPackedArrayTraits<E extends PackableWriter & PackableSizeOf>
            extends ObjectPackedArrayTraits<E>
    {
        /**
         * Constructor.
         *
         * @param elementFactory Element factory to construct from.
         */
        public WriteObjectPackedArrayTraits(PackableElementFactory<E> elementFactory)
        {
            super(elementFactory);
        }

        @SuppressWarnings("unchecked")
        @Override
        public long initializeOffsets(PackingContext context, long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().initializeOffsets(context, bitPosition);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(PackingContext context, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().write(context, writer);
        }
    }
}