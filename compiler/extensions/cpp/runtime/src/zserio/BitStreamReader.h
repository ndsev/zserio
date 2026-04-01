#ifndef ZSERIO_BIT_STREAM_READER_H_INC
#define ZSERIO_BIT_STREAM_READER_H_INC

#include <algorithm>
#include <cstring>
#include <string>

#include "zserio/BitBuffer.h"
#include "zserio/RebindAlloc.h"
#include "zserio/Span.h"
#include "zserio/String.h"
#include "zserio/Types.h"
#include "zserio/Vector.h"

namespace zserio
{

/**
 * Wrapper class to specify maximum initial array allocation.
 */
class ArrayPreallocation
{
public:
    static constexpr size_t MAX_INITIAL_ARRAY_ALLOCATION = 128 * 1024;

    /**
     * Default constructor sets default array preallocation.
     */
    ArrayPreallocation() :
            allocation(MAX_INITIAL_ARRAY_ALLOCATION)
    {}
    /**
     * Constructor.
     *
     * \param alloc Sets custom maximum preallocation for arrays.
     */
    explicit ArrayPreallocation(size_t alloc) :
            allocation(alloc)
    {}
    /**
     * Gets the value for maximum array preallocation.
     */
    operator size_t() const
    {
        return allocation;
    }

private:
    size_t allocation;
};

/**
 * Reader class which allows to read various data from the bit stream.
 */
class BitStreamReader
{
public:
    /** Type for bit position. */
    using BitPosType = size_t;

    /**
     * Context of the reader defining its state.
     */
    struct ReaderContext
    {
        /**
         * Constructor.
         *
         * \param readBuffer Span to the buffer to read.
         * \param readBufferBitSize Size of the buffer in bits.
         */
        explicit ReaderContext(
                Span<const uint8_t> readBuffer, size_t readBufferBitSize, size_t maxArrayPrealloc);

        /**
         * Destructor.
         */
        ~ReaderContext() = default;

        /**
         * Copying and moving is disallowed!
         * \{
         */
        ReaderContext(const ReaderContext&) = delete;
        ReaderContext& operator=(const ReaderContext&) = delete;

        ReaderContext(const ReaderContext&&) = delete;
        ReaderContext& operator=(const ReaderContext&&) = delete;
        /**
         * \}
         */

        Span<const uint8_t> buffer; /**< Buffer to read from. */
        const BitPosType bufferBitSize; /**< Size of the buffer in bits. */

        uintptr_t cache; /**< Bit cache to optimize bit reading. */
        uint8_t cacheNumBits; /**< Num bits available in the bit cache. */

        BitPosType bitIndex; /**< Current bit index. */

        const size_t maxArrayPreallocation; /**< Maximum initial array allocation. */
    };

    /**
     * Constructor from raw buffer.
     *
     * \param buffer Pointer to the buffer to read.
     * \param bufferByteSize Size of the buffer in bytes.
     * \param maxArrayPrealloc Maximum preallocation for arrays.
     */
    explicit BitStreamReader(
            const uint8_t* buffer, size_t bufferByteSize, ArrayPreallocation maxArrayPrealloc = {});

    /**
     * Constructor from buffer passed as a Span.
     *
     * \param buffer Buffer to read.
     * \param maxArrayPrealloc Maximum preallocation for arrays.
     */
    explicit BitStreamReader(Span<const uint8_t> buffer, ArrayPreallocation maxArrayPrealloc = {});

    /**
     * Constructor from buffer passed as a Span with exact bit size.
     *
     * \param buffer Buffer to read.
     * \param bufferBitSize Size of the buffer in bits.
     * \param maxArrayPrealloc Maximum preallocation for arrays.
     */
    explicit BitStreamReader(
            Span<const uint8_t> buffer, size_t bufferBitSize, ArrayPreallocation maxArrayPrealloc = {});

    /**
     * Constructor from raw buffer with exact bit size.
     *
     * \param buffer Pointer to buffer to read.
     * \param bufferBitSize Size of the buffer in bits.
     * \param maxArrayPrealloc Maximum preallocation for arrays.
     */
    explicit BitStreamReader(
            const uint8_t* buffer, size_t bufferBitSize, BitsTag, ArrayPreallocation maxArrayPrealloc = {});

    /**
     * Constructor from bit buffer.
     *
     * \param bitBuffer Bit buffer to read from.
     * \param maxArrayPrealloc Maximum preallocation for arrays.
     */
    template <typename ALLOC>
    explicit BitStreamReader(const BasicBitBuffer<ALLOC>& bitBuffer, ArrayPreallocation maxArrayPrealloc = {}) :
            BitStreamReader(bitBuffer.getData(), bitBuffer.getBitSize(), maxArrayPrealloc)
    {}

    /**
     * Destructor.
     */
    ~BitStreamReader() = default;

    /**
     * Reads unsigned bits up to 32-bits.
     *
     * \param numBits Number of bits to read.
     *
     * \return Read bits.
     */
    uint32_t readBits(uint8_t numBits = 32);

    /**
     * Reads unsigned bits up to 64-bits.
     *
     * \param numBits Number of bits to read.
     *
     * \return Read bits.
     */
    uint64_t readBits64(uint8_t numBits = 64);

    /**
     * Reads signed bits up to 32-bits.
     *
     * \param numBits Number of bits to read.
     *
     * \return Read bits.
     */
    int32_t readSignedBits(uint8_t numBits = 32);

    /**
     * Reads signed bits up to 64-bits.
     *
     * \param numBits Number of bits to read.
     *
     * \return Read bits.
     */
    int64_t readSignedBits64(uint8_t numBits = 64);

    /**
     * Reads signed variable integer up to 64 bits.
     *
     * \return Read varint64.
     */
    int64_t readVarInt64();

    /**
     * Reads signed variable integer up to 32 bits.
     *
     * \return Read varint32.
     */
    int32_t readVarInt32();

    /**
     * Reads signed variable integer up to 16 bits.
     *
     * \return Read varint16.
     */
    int16_t readVarInt16();

    /**
     * Read unsigned variable integer up to 64 bits.
     *
     * \return Read varuint64.
     */
    uint64_t readVarUInt64();

    /**
     * Read unsigned variable integer up to 32 bits.
     *
     * \return Read varuint32.
     */
    uint32_t readVarUInt32();

    /**
     * Read unsigned variable integer up to 16 bits.
     *
     * \return Read varuint16.
     */
    uint16_t readVarUInt16();

    /**
     * Reads signed variable integer up to 72 bits.
     *
     * \return Read varint.
     */
    int64_t readVarInt();

    /**
     * Read unsigned variable integer up to 72 bits.
     *
     * \return Read varuint.
     */
    uint64_t readVarUInt();

    /**
     * Read variable size integer up to 40 bits.
     *
     * \return Read varsize.
     */
    uint32_t readVarSize();

    /**
     * Reads 16-bit float.
     *
     * \return Read float16.
     */
    float readFloat16();

    /**
     * Reads 32-bit float.
     *
     * \return Read float32.
     */
    float readFloat32();

    /**
     * Reads 64-bit float double.
     *
     * \return Read float64.
     */
    double readFloat64();

    /**
     * Reads bytes.
     *
     * \param alloc Allocator to use.
     *
     * \return Read bytes as a vector.
     */
    template <typename ALLOC = std::allocator<uint8_t>>
    vector<uint8_t, ALLOC> readBytes(const ALLOC& alloc = ALLOC())
    {
        const size_t len = static_cast<size_t>(readVarSize());
        const BitPosType beginBitPosition = getBitPosition();
        if (beginBitPosition + 8 * len > getBufferBitSize())
        {
            throw CppRuntimeException("BitStreamReader: Byte array size exceeds available buffer!");
        }
        if ((beginBitPosition & 0x07U) != 0)
        {
            // we are not aligned to byte
            vector<uint8_t, ALLOC> value{alloc};
            value.reserve(len);
            for (size_t i = 0; i < len; ++i)
            {
                value.push_back(readByte());
            }
            return value;
        }
        else
        {
            // we are aligned to byte
            setBitPosition(beginBitPosition + len * 8);
            Span<const uint8_t>::iterator beginIt = m_context.buffer.begin() + beginBitPosition / 8;
            return vector<uint8_t, ALLOC>(beginIt, beginIt + len, alloc);
        }
    }

    /**
     * Reads an UTF-8 string.
     *
     * \param alloc Allocator to use.
     *
     * \return Read string.
     */
    template <typename ALLOC = std::allocator<char>>
    string<ALLOC> readString(const ALLOC& alloc = ALLOC())
    {
        const size_t len = static_cast<size_t>(readVarSize());
        const BitPosType beginBitPosition = getBitPosition();
        if (beginBitPosition + 8 * len > getBufferBitSize())
        {
            throw CppRuntimeException("BitStreamReader: String size exceeds available buffer!");
        }
        if ((beginBitPosition & 0x07U) != 0)
        {
            // we are not aligned to byte
            string<ALLOC> value{alloc};
            value.reserve(len);
            for (size_t i = 0; i < len; ++i)
            {
                using char_traits = std::char_traits<char>;
                const char readCharacter =
                        char_traits::to_char_type(static_cast<char_traits::int_type>(readByte()));
                value.push_back(readCharacter);
            }
            return value;
        }
        else
        {
            // we are aligned to byte
            setBitPosition(beginBitPosition + len * 8);
            Span<const uint8_t>::iterator beginIt = m_context.buffer.begin() + beginBitPosition / 8;
            return string<ALLOC>(beginIt, beginIt + len, alloc);
        }
    }

    /**
     * Reads bool as a single bit.
     *
     * \return Read bool value.
     */
    bool readBool();

    /**
     * Reads a bit buffer.
     *
     * \param alloc Allocator to use.
     *
     * \return Read bit buffer.
     */
    template <typename ALLOC = std::allocator<uint8_t>>
    BasicBitBuffer<RebindAlloc<ALLOC, uint8_t>> readBitBuffer(const ALLOC& allocator = ALLOC())
    {
        const size_t bitSize = static_cast<size_t>(readVarSize());
        const BitPosType beginBitPosition = getBitPosition();
        if (beginBitPosition + bitSize > getBufferBitSize())
        {
            throw CppRuntimeException("BitStreamReader: Bit buffer size exceeds available buffer!");
        }

        const size_t numBytesToRead = bitSize / 8;
        const uint8_t numRestBits = static_cast<uint8_t>(bitSize - numBytesToRead * 8);
        BasicBitBuffer<RebindAlloc<ALLOC, uint8_t>> bitBuffer(bitSize, allocator);
        Span<uint8_t> buffer = bitBuffer.getData();
        const Span<uint8_t>::iterator itEnd = buffer.begin() + numBytesToRead;
        if ((beginBitPosition & 0x07U) != 0)
        {
            // we are not aligned to byte
            for (Span<uint8_t>::iterator it = buffer.begin(); it != itEnd; ++it)
            {
                *it = static_cast<uint8_t>(readBits(8));
            }
        }
        else
        {
            // we are aligned to byte
            setBitPosition(beginBitPosition + numBytesToRead * 8);
            Span<const uint8_t>::const_iterator sourceIt = m_context.buffer.begin() + beginBitPosition / 8;
            (void)std::copy(sourceIt, sourceIt + numBytesToRead, buffer.begin());
        }

        if (numRestBits > 0)
        {
            *itEnd = static_cast<uint8_t>(readBits(numRestBits) << (8U - numRestBits));
        }

        return bitBuffer;
    }

    /**
     * Gets current bit position.
     *
     * \return Current bit position.
     */
    BitPosType getBitPosition() const
    {
        return m_context.bitIndex;
    }

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
     * Gets size of the underlying buffer in bits.
     *
     * \return Buffer bit size.
     */
    size_t getBufferBitSize() const
    {
        return m_context.bufferBitSize;
    }

    /**
     * Gets the maximum initial array capacity in bytes.
     *
     * \return Maximum array preallocation.
     */
    size_t getMaxArrayPreallocation() const
    {
        return m_context.maxArrayPreallocation;
    }

private:
    uint8_t readByte();

    ReaderContext m_context;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_READER_H_INC
