package zserio.runtime.io;

import java.io.IOException;

/**
 * An interface for bit stream manipulator (reader or writer).
 */
public interface BitStreamConsumer
{
    /**
     * Gets the current bit position.
     *
     * @return Current bit position counted from zero.
     */
    long getBitPosition();

    /**
     * Sets the bit position to the given value.
     *
     * @param position Bit position counted from zero to set.
     *
     * @throws IOException If the position cannot be set.
     */
    void setBitPosition(long position) throws IOException;

    /**
     * Closes bit stream.
     *
     * @throws IOException If the bit stream cannot be closed.
     */
    void close() throws IOException;

    /**
     * Gets the current byte position.
     *
     * @return Current byte position counted from zero.
     */
    int getBytePosition();

    /**
     * Seeks to the given byte position.
     *
     * @param bytePosition Byte position where to seek.
     *
     * @throws IOException If the position cannot be accessed.
     */
    void seek(final int bytePosition) throws IOException;

    /**
     * Gets the bit offset of byte where current position is.
     *
     * @return Bit offset from interval <0, 7>.
     */
    int getBitOffset();

    /**
     * Aligns the bit position according to the aligning value.
     * <p>
     * Examples:</p>
     * <ul>
     * <li>If aligning value is 2 and bit position is 3, aligned bit position will be 4.</li>
     * <li>If aligning value is 7 and bit position is 15, aligned bit position will be 21.</li>
     * <li>If aligning valus is 8 and bit position is 4, aligned bit positionw will be 8.</li>
     * </ul>
     * @param alignVal An aligning value to use.
     *
     * @throws IOException If the alignment failed.
     */
    void alignTo(final int alignVal) throws IOException;

    /**
     * Skips the next numBits bits and moves the current bit position.
     *
     * @param numBits Number of bits to skip.
     *
     * @throws IOException If the new position cannot be resolved.
     */
    void skipBits(final int numBits) throws IOException;

    /**
     * Rewinds this stream and sets the bit position to zero.
     *
     * @throws IOException If the rewinding failed.
     */
    void rewind() throws IOException;
}
