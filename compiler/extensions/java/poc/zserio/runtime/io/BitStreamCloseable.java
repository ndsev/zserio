package zserio.runtime.io;

import java.io.IOException;

/**
 * A closeable interface for a bit stream reader and writer.
 */
public interface BitStreamCloseable
{
    /**
     * Closes the bit stream.
     *
     * @throws IOException If the bit stream cannot be closed.
     */
    void close() throws IOException;
}
