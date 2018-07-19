package zserio.runtime.io;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

/**
 * An interface for a bit stream writer implementation.
 */
public interface BitStreamWriter extends BitStreamConsumer, DataOutput
{
    /**
     * Writes the given bit to the underlying storage.
     *
     * @param bit A bit value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeBit(final int bit) throws IOException;

    /**
     * Writes the given value with the given number of signed bits to the underlying storage.
     *
     * @param value   Value to write.
     * @param numBits Number of bits for the value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeSignedBits(final long value, final int numBits) throws IOException;

    /**
     * Writes the given value with the given number of unsigned bits to the underlying storage.
     *
     * @param value   Value to write.
     * @param numBits Number of bits for the value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeBits(final long value, final int numBits) throws IOException;

    /**
     * Writes an unsigned byte to the underlying storage.
     *
     * @param value Unsigned byte value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedByte(final short value) throws IOException;

    /**
     * Writes an unsigned short to the underlying storage.
     *
     * @param value Unsigned short value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedShort(final int value) throws IOException;

    /**
     * Writes an unsigned integer to the underlying storage.
     *
     * @param value Unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedInt(final long value) throws IOException;

    /**
     * Writes a given big integer value with the given number of bits to the underlying storage.
     *
     * @param value   Big integer value to write.
     * @param numBits Number of bits for the value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeBigInteger(final BigInteger value, final int numBits) throws IOException;

    /**
     * Writes a boolean value to the underlying storage.
     *
     * @param value Boolean value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeBool(final boolean value) throws IOException;

    /**
     * Writes a variable 16 bit integer value underlying storage.
     *
     * @param value Variable 17 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt16(final short value) throws IOException;

    /**
     * Writes a variable 16 bit unsigned integer value underlying storage.
     *
     * @param value Variable 16 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt16(final short value) throws IOException;

    /**
     * Writes a variable 32 bit integer value underlying storage.
     *
     * @param value Variable 32 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt32(final int value) throws IOException;

    /**
     * Writes a variable 32 bit unsigned integer value underlying storage.
     *
     * @param value Variable 32 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt32(final int value) throws IOException;

    /**
     * Writes a variable 64 bit integer value underlying storage.
     *
     * @param value Variable 64 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt64(final long value) throws IOException;

    /**
     * Writes a variable 64 bit unsigned integer value underlying storage.
     *
     * @param value Variable 64 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt64(final long value) throws IOException;

    /**
     * Writes a Zserio string to the underlying storage in UTF-8 encoding.
     *
     * @param value Zserio string to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeString(final String value) throws IOException;

    /**
     * Writes a 16 bit float value to the underlying storage.
     *
     * @param value 16 bit float value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeFloat16(final float value) throws IOException;
}
