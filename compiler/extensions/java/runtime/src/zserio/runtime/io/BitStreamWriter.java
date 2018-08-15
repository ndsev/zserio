package zserio.runtime.io;

import java.io.IOException;
import java.math.BigInteger;

/**
 * An interface for a bit stream writer implementation.
 */
public interface BitStreamWriter extends BitStreamCloseable
{
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
     * Writes a signed byte to the underlying storage.
     *
     * @param value Signed byte value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeByte(final byte value) throws IOException;

    /**
     * Writes an unsigned byte to the underlying storage.
     *
     * @param value Unsigned byte value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedByte(final short value) throws IOException;

    /**
     * Writes a signed short to the underlying storage.
     *
     * @param value Signed short value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeShort(final short value) throws IOException;

    /**
     * Writes an unsigned short to the underlying storage.
     *
     * @param value Unsigned short value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedShort(final int value) throws IOException;

    /**
     * Writes a signed integer to the underlying storage.
     *
     * @param value Signed integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeInt(final int value) throws IOException;

    /**
     * Writes an unsigned integer to the underlying storage.
     *
     * @param value Unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeUnsignedInt(final long value) throws IOException;

    /**
     * Writes a signed long to the underlying storage.
     *
     * @param value Signed integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeLong(final long value) throws IOException;

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
     * Writes a 16-bit float value to the underlying storage.
     *
     * @param value Half precision float value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeFloat16(final float value) throws IOException;

    /**
     * Writes a 32-bit float value to the underlying storage.
     *
     * @param value Single precision float value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeFloat32(final float value) throws IOException;

    /**
     * Writes a 64-bit float value to the underlying storage.
     *
     * @param value Double precision float value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeFloat64(final double value) throws IOException;

    /**
     * Writes a Zserio string to the underlying storage in UTF-8 encoding.
     *
     * @param value Zserio string to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeString(final String value) throws IOException;

    /**
     * Writes a boolean value to the underlying storage.
     *
     * @param value Boolean value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeBool(final boolean value) throws IOException;

    /**
     * Writes a variable 16 bit integer value to the underlying storage.
     *
     * @param value Variable 17 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt16(final short value) throws IOException;

    /**
     * Writes a variable 16 bit unsigned integer value to the underlying storage.
     *
     * @param value Variable 16 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt16(final short value) throws IOException;

    /**
     * Writes a variable 32 bit integer value to the underlying storage.
     *
     * @param value Variable 32 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt32(final int value) throws IOException;

    /**
     * Writes a variable 32 bit unsigned integer value to the underlying storage.
     *
     * @param value Variable 32 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt32(final int value) throws IOException;

    /**
     * Writes a variable 64 bit integer value to the underlying storage.
     *
     * @param value Variable 64 bit integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarInt64(final long value) throws IOException;

    /**
     * Writes a variable 64 bit unsigned integer value to the underlying storage.
     *
     * @param value Variable 64 bit unsigned integer value to write.
     *
     * @throws IOException If the writing failed.
     */
    void writeVarUInt64(final long value) throws IOException;

    /**
     * Writes a signed variable integer value to the underlying storage.
     *
     * @param value BigInteger value to write.
     *
     * @throws IOException
     */
    void writeVarInt(final long value) throws IOException;

    /** Writes an unsigned variable integer value to the underlying storage.
     *
     * @param value BigInteger value to write.
     *
     * @throws IOException
     */
    void writeVarUInt(final BigInteger value) throws IOException;

    /**
     * Gets the current bit position.
     *
     * @return Current bit position counted from zero.
     */
    long getBitPosition();

    /**
     * Gets the current byte position.
     *
     * @return Current byte position counted from zero.
     */
    int getBytePosition();

    /**
     * Sets the bit position to the given value.
     *
     * @param position Bit position counted from zero to set.
     *
     * @throws IOException If the position cannot be set.
     */
    void setBitPosition(long position) throws IOException;

    /**
     * Aligns the bit position according to the aligning value.
     *
     * @param alignVal An aligning value to use.
     *
     * @throws IOException If the alignment failed.
     */
    void alignTo(final int alignVal) throws IOException;
}
