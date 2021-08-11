package zserio.runtime.array;

import java.io.IOException;

import zserio.runtime.ZserioError;
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
     * @throws ZserioError Failure during element creation.
     */
    E create(BitStreamReader reader, int index) throws IOException, ZserioError;
}
