package zserio.runtime.io;

import java.io.IOException;

/**
 * A closeable interface for a bit stream reader and writer.
 *
 * @throws IOException If the bit stream cannot be closed.
 */
public interface BitStreamCloseable
{
    void close() throws IOException;
}
