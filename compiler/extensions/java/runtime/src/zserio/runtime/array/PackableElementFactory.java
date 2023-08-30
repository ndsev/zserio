package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;

/**
 * Interface used to construct packable elements from the bit stream.
 *
 * This interface is used only internally for generated code.
 */
public interface PackableElementFactory<E> extends ElementFactory<E>
{
    /**
     * Creates packing context for the array element.
     *
     * @return Created packing context.
     */
    PackingContext createPackingContext();

    /**
     * Creates packed array elements from the bit stream.
     *
     * @param context Packing context node.
     * @param reader Bit stream to read from.
     * @param index Index of the element to create.
     *
     * @return Created elements.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    E create(PackingContext context, BitStreamReader reader, int index) throws IOException;
}
