package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;

/**
 * Interface used to construct elements from the bit stream.
 */
public interface ElementFactory<E>
{
    /**
     * Creates array elements from the bit stream.
     *
     * @param reader Bit stream to read from.
     * @param index  Index of element to create.
     *
     * @return Created element.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    E create(BitStreamReader reader, int index) throws IOException;

    // TODO[Mi-L@]: Should we split this to separate interface?
    /**
     * Creates packing context for the array element.
     *
     * @param contextNode Packing context node.
     */
    void createPackingContext(PackingContextNode contextNode);

    /**
     * Creates packed array elements from the bit stream.
     *
     * @param contextNode Packing context node.
     * @param reader Bit stream to read from.
     * @param index Index of the element to create.
     *
     * @return Created elements.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    E create(PackingContextNode contextNode, BitStreamReader reader, int index) throws IOException;
}
