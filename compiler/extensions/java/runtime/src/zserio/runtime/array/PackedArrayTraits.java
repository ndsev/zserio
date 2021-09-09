package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.SizeOf;
import zserio.runtime.array.ArrayElement.IntegralArrayElement;
import zserio.runtime.array.ArrayTraits.IntegralArrayTraits;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.InitializeOffsetsWriter;

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
    public PackingContextNode createContext();

    /**
     * Calls context initialization step for the current element.
     *
     * @param contextNode Packing context node which keeps the context.
     * @param element Current element.
     */
    public void initContext(PackingContextNode contextNode, ArrayElement element);

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * @param contextNode Packing context node.
     * @param bitPosition Current bit stream position.
     * @param element Current element.
     *
     * @return Length of the array element stored in the bit stream in bits.
     */
    public int bitSizeOf(PackingContextNode contextNode, long bitPosition, ArrayElement element);

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * @param contextNode Packing context node.
     * @param bitPosition Current bit stream position.
     * @param element Current element.
     *
     * @return Updated bit stream position which points to the first bit after this element.
     */
    public long initializeOffsets(PackingContextNode contextNode, long bitPosition, ArrayElement element);

    /**
     * Reads an element from the bit stream.
     *
     * @param contextNode Packing context node.
     * @param reader Bit stream reader.
     *
     * @return Read element.
     *
     * @throws Failure during bit stream manipulation.
     */
    public ArrayElement read(PackingContextNode contextNode, BitStreamReader reader, int index)
            throws IOException;

    /**
     * Writes the element to the bit stream.
     *
     * @param contextNode Packing context node.
     * @param writer Bit stream writer.
     * @param element Element to write.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public void write(PackingContextNode contextNode, BitStreamWriter writer, ArrayElement element)
            throws IOException;

    /**
     * Packed array traits for arrays of integral types.
     *
     * Works with single DeltaContext.
     */
    public static class IntegralPackedArrayTraits implements PackedArrayTraits
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
        public PackingContextNode createContext()
        {
            final PackingContextNode contextNode = new PackingContextNode();
            contextNode.createContext();
            return contextNode;
        }

        @Override
        public void initContext(PackingContextNode contextNode, ArrayElement element)
        {
            contextNode.getContext().init((IntegralArrayElement)element);
        }

        @Override
        public int bitSizeOf(PackingContextNode contextNode, long bitPosition, ArrayElement element)
        {
            return contextNode.getContext().bitSizeOf(arrayTraits, bitPosition, (IntegralArrayElement)element);
        }

        @Override
        public long initializeOffsets(PackingContextNode contextNode, long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(contextNode, bitPosition, element);
        }

        @Override
        public IntegralArrayElement read(PackingContextNode contextNode, BitStreamReader reader, int index)
                throws IOException
        {
            return contextNode.getContext().read(arrayTraits, reader);
        }

        @Override
        public void write(PackingContextNode contextNode, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            contextNode.getContext().write(arrayTraits, writer, (IntegralArrayElement)element);
        }

        private final IntegralArrayTraits arrayTraits;
    }

    /**
     * Packed array traits for zserio object arrays (without writer part).
     */
    public static class ObjectPackedArrayTraits<E extends SizeOf> implements PackedArrayTraits
    {
        /**
         * Constructor.
         *
         * @param elementFactory Element factory to construct from.
         */
        public ObjectPackedArrayTraits(ElementFactory<E> elementFactory)
        {
            this.elementFactory = elementFactory;
        }

        @Override
        public PackingContextNode createContext()
        {
            final PackingContextNode contextNode = new PackingContextNode();
            elementFactory.createPackingContext(contextNode);
            return contextNode;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initContext(PackingContextNode contextNode, ArrayElement element)
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().initPackingContext(contextNode);
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(PackingContextNode contextNode, long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().bitSizeOf(contextNode, bitPosition);
        }

        @Override
        public long initializeOffsets(PackingContextNode contextNode, long bitPosition, ArrayElement element)
        {
            throw new UnsupportedOperationException("PackedArrayTraits: " +
                    "initializeOffsets is not implemented for read only PackedObjectArrayTraits!");
        }

        @Override
        public ArrayElement read(PackingContextNode contextNode, BitStreamReader reader, int index)
                throws IOException
        {
            element.set(elementFactory.create(contextNode, reader, index));
            return element;
        }

        @Override
        public void write(PackingContextNode contextNode, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            throw new UnsupportedOperationException(
                    "PackedArrayTraits: write is not implemented for read only PackedObjectArrayTraits!");
        }

        private final ElementFactory<E> elementFactory;
        private final ArrayElement.ObjectArrayElement<E> element = new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Packed array traits for zserio object arrays (with writer part).
     */
    public static class WriteObjectPackedArrayTraits<E extends InitializeOffsetsWriter & SizeOf>
            extends ObjectPackedArrayTraits<E>
    {
        public WriteObjectPackedArrayTraits(ElementFactory<E> elementFactory)
        {
            super(elementFactory);
        }

        @SuppressWarnings("unchecked")
        @Override
        public long initializeOffsets(PackingContextNode contextNode, long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().initializeOffsets(
                    contextNode, bitPosition);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(PackingContextNode contextNode, BitStreamWriter writer, ArrayElement element)
                throws IOException
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().write(contextNode, writer);
        }
    }
}