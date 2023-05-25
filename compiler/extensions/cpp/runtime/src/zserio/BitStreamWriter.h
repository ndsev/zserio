#ifndef ZSERIO_BIT_STREAM_WRITER_H_INC
#define ZSERIO_BIT_STREAM_WRITER_H_INC

#include <cstddef>
#include <cstring>
#include <algorithm>

#include "zserio/BitBuffer.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/Span.h"
#include "zserio/StringView.h"
#include "zserio/Types.h"
#include "zserio/SizeConvertUtil.h"

namespace zserio
{

/**
 * Writer class which allows to write various data to the bit stream.
 */
class BitStreamWriter
{
public:
    /** Exception throw in case of insufficient capacity of the given buffer. */
    class InsufficientCapacityException : public CppRuntimeException
    {
    public:
        using CppRuntimeException::CppRuntimeException;
    };

    /** Type for bit position. */
    using BitPosType = size_t;

    /**
     * Constructor from externally allocated byte buffer.
     *
     * \param buffer External byte buffer to create from.
     * \param bufferBitSize Size of the buffer in bits.
     */
    explicit BitStreamWriter(uint8_t* buffer, size_t bufferBitSize, BitsTag);

    /**
     * Constructor from externally allocated byte buffer.
     *
     * \param buffer External byte buffer to create from.
     * \param bufferByteSize Size of the buffer in bytes.
     */
    explicit BitStreamWriter(uint8_t* buffer, size_t bufferByteSize);

    /**
     * Constructor from externally allocated byte buffer.
     *
     * \param buffer External buffer to create from as a Span.
     */
    explicit BitStreamWriter(Span<uint8_t> buffer);

    /**
     * Constructor from externally allocated byte buffer with exact bit size.
     *
     * \param buffer External buffer to create from as a Span.
     * \param bufferBitSize Size of the buffer in bits.
     */
    explicit BitStreamWriter(Span<uint8_t> buffer, size_t bufferBitSize);

    /**
     * Constructor from externally allocated bit buffer.
     *
     * \param bitBuffer External bit buffer to create from.
     */
    template <typename ALLOC>
    explicit BitStreamWriter(BasicBitBuffer<ALLOC>& bitBuffer) :
            BitStreamWriter(bitBuffer.getData(), bitBuffer.getBitSize())
    {
    }

    /**
     * Destructor.
     */
    ~BitStreamWriter() = default;

    /**
     * Copying and moving is disallowed!
     * \{
     */
    BitStreamWriter(const BitStreamWriter&) = delete;
    BitStreamWriter& operator=(const BitStreamWriter&) = delete;

    BitStreamWriter(const BitStreamWriter&&) = delete;
    BitStreamWriter& operator=(BitStreamWriter&&) = delete;
    /**
     * \}
     */

    /**
     * Writes unsigned bits up to 32 bits.
     *
     * \param data Data to write.
     * \param numBits Number of bits to write.
     */
    void writeBits(uint32_t data, uint8_t numBits = 32);

    /**
     * Writes unsigned bits up to 64 bits.
     *
     * \param data Data to write.
     * \param numBits Number of bits to write.
     */
    void writeBits64(uint64_t data, uint8_t numBits = 64);

    /**
     * Writes signed bits up to 32 bits.
     *
     * \param data Data to write.
     * \param numBits Number of bits to write.
     */
    void writeSignedBits(int32_t data, uint8_t numBits = 32);

    /**
     * Writes signed bits up to 64 bits.
     *
     * \param data Data to write.
     * \param numBits Number of bits to write.
     */
    void writeSignedBits64(int64_t data, uint8_t numBits = 64);

    /**
     * Writes signed variable integer up to 64 bits.
     *
     * \param data Varint64 to write.
     */
    void writeVarInt64(int64_t data);

    /**
     * Writes signed variable integer up to 32 bits.
     *
     * \param data Varint32 to write.
     */
    void writeVarInt32(int32_t data);

    /**
     * Writes signed variable integer up to 16 bits.
     *
     * \param data Varint16 to write.
     */
    void writeVarInt16(int16_t data);

    /**
     * Writes unsigned variable integer up to 64 bits.
     *
     * \param data Varuint64 to write.
     */
    void writeVarUInt64(uint64_t data);

    /**
     * Writes unsigned variable integer up to 32 bits.
     *
     * \param data Varuint32 to write.
     */
    void writeVarUInt32(uint32_t data);

    /**
     * Writes unsigned variable integer up to 16 bits.
     *
     * \param data Varuint16 to write.
     */
    void writeVarUInt16(uint16_t data);

    /**
     * Writes signed variable integer up to 72 bits.
     *
     * \param data Varuint64 to write.
     */
    void writeVarInt(int64_t data);

    /**
     * Writes signed variable integer up to 72 bits.
     *
     * \param data Varuint64 to write.
     */
    void writeVarUInt(uint64_t data);

    /**
     * Writes variable size integer up to 40 bits.
     *
     * \param data Varsize to write.
     */
    void writeVarSize(uint32_t data);

    /**
     * Writes 16-bit float.
     *
     * \param data Float16 to write.
     */
    void writeFloat16(float data);

    /**
     * Writes 32-bit float.
     *
     * \param data Float32 to write.
     */
    void writeFloat32(float data);

    /**
     * Writes 64-bit float.
     *
     * \param data Float64 to write.
     */
    void writeFloat64(double data);

    /**
     * Writes bytes.
     *
     * \param data Bytes to write.
     */
    void writeBytes(Span<const uint8_t> data);

    /**
     * Writes UTF-8 string.
     *
     * \param data String view to write.
     */
    void writeString(StringView data);

    /**
     * Writes bool as a single bit.
     *
     * \param data Bool to write.
     */
    void writeBool(bool data);

    /**
     * Writes bit buffer.
     *
     * \param bitBuffer Bit buffer to write.
     */
    template <typename ALLOC>
    void writeBitBuffer(const BasicBitBuffer<ALLOC>& bitBuffer)
    {
        const size_t bitSize = bitBuffer.getBitSize();
        writeVarSize(convertSizeToUInt32(bitSize));

        Span<const uint8_t> buffer = bitBuffer.getData();
        size_t numBytesToWrite = bitSize / 8;
        const uint8_t numRestBits = static_cast<uint8_t>(bitSize - numBytesToWrite * 8);
        const BitPosType beginBitPosition = getBitPosition();
        const Span<const uint8_t>::iterator itEnd = buffer.begin() + numBytesToWrite;
        if ((beginBitPosition & 0x07U) != 0)
        {
            // we are not aligned to byte
            for (Span<const uint8_t>::iterator it = buffer.begin(); it != itEnd; ++it)
                writeUnsignedBits(*it, 8);
        }
        else
        {
            // we are aligned to byte
            setBitPosition(beginBitPosition + numBytesToWrite * 8);
            if (hasWriteBuffer())
            {
                std::copy(buffer.begin(), buffer.begin() + numBytesToWrite,
                        m_buffer.data() + beginBitPosition / 8);
            }
        }

        if (numRestBits > 0)
            writeUnsignedBits(*itEnd >> (8U - numRestBits), numRestBits);
    }

    /**
     * Gets current bit position.
     *
     * \return Current bit position.
     */
    BitPosType getBitPosition() const { return m_bitIndex; }

    /**
     * Sets current bit position. Use with caution!
     *
     * \param position New bit position.
     */
    void setBitPosition(BitPosType position);

    /**
     * Moves current bit position to perform the requested bit alignment.
     *
     * \param alignment Size of the alignment in bits.
     */
    void alignTo(size_t alignment);

    /**
     * Gets whether the writer has assigned a write buffer.
     *
     * \return True when a buffer is assigned. False otherwise.
     */
    bool hasWriteBuffer() const { return m_buffer.data() != nullptr; }

    /**
     * Gets the write buffer.
     *
     * \return Pointer to the beginning of write buffer.
     */
    const uint8_t* getWriteBuffer() const;

    /**
     * Gets the write buffer as span.
     *
     * \return Span which represents the write buffer.
     */
    Span<const uint8_t> getBuffer() const;

    /**
     * Gets size of the underlying buffer in bits.
     *
     * \return Buffer bit size.
     */
    size_t getBufferBitSize() const { return m_bufferBitSize; }

private:
    void writeUnsignedBits(uint32_t data, uint8_t numBits);
    void writeUnsignedBits64(uint64_t data, uint8_t numBits);
    void writeSignedVarNum(int64_t value, size_t maxVarBytes, size_t numVarBytes);
    void writeUnsignedVarNum(uint64_t value, size_t maxVarBytes, size_t numVarBytes);
    void writeVarNum(uint64_t value, bool hasSign, bool isNegative, size_t maxVarBytes, size_t numVarBytes);

    void checkCapacity(size_t bitSize) const;
    void throwInsufficientCapacityException() const;

    Span<uint8_t> m_buffer;
    size_t m_bitIndex;
    size_t m_bufferBitSize;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_WRITER_H_INC
