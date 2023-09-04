#include <limits>
#include <array>

#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/FloatUtil.h"
#include "zserio/RuntimeArch.h"

namespace zserio
{

namespace
{
    // max size calculated to prevent overflows in internal comparisons
    const size_t MAX_BUFFER_SIZE = std::numeric_limits<size_t>::max() / 8 - 4;

    using BitPosType = BitStreamReader::BitPosType;
    using ReaderContext = BitStreamReader::ReaderContext;

#ifdef ZSERIO_RUNTIME_64BIT
    using BaseType = uint64_t;
    using BaseSignedType = int64_t;
#else
    using BaseType = uint32_t;
    using BaseSignedType = int32_t;
#endif

#ifdef ZSERIO_RUNTIME_64BIT
    const std::array<BaseType, 65> MASK_TABLE =
    {
        UINT64_C(0x00),
        UINT64_C(0x0001),     UINT64_C(0x0003),     UINT64_C(0x0007),     UINT64_C(0x000f),
        UINT64_C(0x001f),     UINT64_C(0x003f),     UINT64_C(0x007f),     UINT64_C(0x00ff),
        UINT64_C(0x01ff),     UINT64_C(0x03ff),     UINT64_C(0x07ff),     UINT64_C(0x0fff),
        UINT64_C(0x1fff),     UINT64_C(0x3fff),     UINT64_C(0x7fff),     UINT64_C(0xffff),
        UINT64_C(0x0001ffff), UINT64_C(0x0003ffff), UINT64_C(0x0007ffff), UINT64_C(0x000fffff),
        UINT64_C(0x001fffff), UINT64_C(0x003fffff), UINT64_C(0x007fffff), UINT64_C(0x00ffffff),
        UINT64_C(0x01ffffff), UINT64_C(0x03ffffff), UINT64_C(0x07ffffff), UINT64_C(0x0fffffff),
        UINT64_C(0x1fffffff), UINT64_C(0x3fffffff), UINT64_C(0x7fffffff), UINT64_C(0xffffffff),

        UINT64_C(0x00000001ffffffff), UINT64_C(0x00000003ffffffff),
        UINT64_C(0x00000007ffffffff), UINT64_C(0x0000000fffffffff),
        UINT64_C(0x0000001fffffffff), UINT64_C(0x0000003fffffffff),
        UINT64_C(0x0000007fffffffff), UINT64_C(0x000000ffffffffff),
        UINT64_C(0x000001ffffffffff), UINT64_C(0x000003ffffffffff),
        UINT64_C(0x000007ffffffffff), UINT64_C(0x00000fffffffffff),
        UINT64_C(0x00001fffffffffff), UINT64_C(0x00003fffffffffff),
        UINT64_C(0x00007fffffffffff), UINT64_C(0x0000ffffffffffff),
        UINT64_C(0x0001ffffffffffff), UINT64_C(0x0003ffffffffffff),
        UINT64_C(0x0007ffffffffffff), UINT64_C(0x000fffffffffffff),
        UINT64_C(0x001fffffffffffff), UINT64_C(0x003fffffffffffff),
        UINT64_C(0x007fffffffffffff), UINT64_C(0x00ffffffffffffff),
        UINT64_C(0x01ffffffffffffff), UINT64_C(0x03ffffffffffffff),
        UINT64_C(0x07ffffffffffffff), UINT64_C(0x0fffffffffffffff),
        UINT64_C(0x1fffffffffffffff), UINT64_C(0x3fffffffffffffff),
        UINT64_C(0x7fffffffffffffff), UINT64_C(0xffffffffffffffff)
    };
#else
    const std::array<BaseType, 33> MASK_TABLE =
    {
        UINT32_C(0x00),
        UINT32_C(0x0001),     UINT32_C(0x0003),     UINT32_C(0x0007),     UINT32_C(0x000f),
        UINT32_C(0x001f),     UINT32_C(0x003f),     UINT32_C(0x007f),     UINT32_C(0x00ff),
        UINT32_C(0x01ff),     UINT32_C(0x03ff),     UINT32_C(0x07ff),     UINT32_C(0x0fff),
        UINT32_C(0x1fff),     UINT32_C(0x3fff),     UINT32_C(0x7fff),     UINT32_C(0xffff),
        UINT32_C(0x0001ffff), UINT32_C(0x0003ffff), UINT32_C(0x0007ffff), UINT32_C(0x000fffff),
        UINT32_C(0x001fffff), UINT32_C(0x003fffff), UINT32_C(0x007fffff), UINT32_C(0x00ffffff),
        UINT32_C(0x01ffffff), UINT32_C(0x03ffffff), UINT32_C(0x07ffffff), UINT32_C(0x0fffffff),
        UINT32_C(0x1fffffff), UINT32_C(0x3fffffff), UINT32_C(0x7fffffff), UINT32_C(0xffffffff)
    };
#endif

    const uint8_t VARINT_SIGN_1 = UINT8_C(0x80);
    const uint8_t VARINT_BYTE_1 = UINT8_C(0x3f);
    const uint8_t VARINT_BYTE_N = UINT8_C(0x7f);
    const uint8_t VARINT_HAS_NEXT_1 = UINT8_C(0x40);
    const uint8_t VARINT_HAS_NEXT_N = UINT8_C(0x80);

    const uint8_t VARUINT_BYTE = UINT8_C(0x7f);
    const uint8_t VARUINT_HAS_NEXT = UINT8_C(0x80);

    const uint32_t VARSIZE_MAX_VALUE = (UINT32_C(1) << 31U) - 1;

#ifdef ZSERIO_RUNTIME_64BIT
    inline BaseType parse64(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 56U |
               static_cast<BaseType>(*(bufferIt + 1)) << 48U |
               static_cast<BaseType>(*(bufferIt + 2)) << 40U |
               static_cast<BaseType>(*(bufferIt + 3)) << 32U |
               static_cast<BaseType>(*(bufferIt + 4)) << 24U |
               static_cast<BaseType>(*(bufferIt + 5)) << 16U |
               static_cast<BaseType>(*(bufferIt + 6)) << 8U |
               static_cast<BaseType>(*(bufferIt + 7));
    }

    inline BaseType parse56(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 48U |
               static_cast<BaseType>(*(bufferIt + 1)) << 40U |
               static_cast<BaseType>(*(bufferIt + 2)) << 32U |
               static_cast<BaseType>(*(bufferIt + 3)) << 24U |
               static_cast<BaseType>(*(bufferIt + 4)) << 16U |
               static_cast<BaseType>(*(bufferIt + 5)) << 8U |
               static_cast<BaseType>(*(bufferIt + 6));
    }

    inline BaseType parse48(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 40U |
               static_cast<BaseType>(*(bufferIt + 1)) << 32U |
               static_cast<BaseType>(*(bufferIt + 2)) << 24U |
               static_cast<BaseType>(*(bufferIt + 3)) << 16U |
               static_cast<BaseType>(*(bufferIt + 4)) << 8U |
               static_cast<BaseType>(*(bufferIt + 5));
    }

    inline BaseType parse40(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 32U |
               static_cast<BaseType>(*(bufferIt + 1)) << 24U |
               static_cast<BaseType>(*(bufferIt + 2)) << 16U |
               static_cast<BaseType>(*(bufferIt + 3)) << 8U |
               static_cast<BaseType>(*(bufferIt + 4));
    }
#endif
    inline BaseType parse32(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 24U |
               static_cast<BaseType>(*(bufferIt + 1)) << 16U |
               static_cast<BaseType>(*(bufferIt + 2)) << 8U |
               static_cast<BaseType>(*(bufferIt + 3));
    }

    inline BaseType parse24(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 16U |
               static_cast<BaseType>(*(bufferIt + 1)) << 8U |
               static_cast<BaseType>(*(bufferIt + 2));
    }

    inline BaseType parse16(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt) << 8U |
               static_cast<BaseType>(*(bufferIt + 1));
    }

    inline BaseType parse8(Span<const uint8_t>::const_iterator bufferIt)
    {
        return static_cast<BaseType>(*bufferIt);
    }

    /** Optimization which increases chances to inline checkNumBits and checkNumBits64. */
    inline void throwNumBitsIsNotValid(uint8_t numBits)
    {
        throw CppRuntimeException("BitStreamReader: ReadBits #") << numBits <<
                " is not valid, reading from stream failed!";
    }

    /** Checks numBits validity for 32-bit reads. */
    inline void checkNumBits(uint8_t numBits)
    {
        if (numBits > 32)
            throwNumBitsIsNotValid(numBits);
    }

    /** Checks numBits validity for 64-bit reads. */
    inline void checkNumBits64(uint8_t numBits)
    {
        if (numBits > 64)
            throwNumBitsIsNotValid(numBits);
    }

    /** Optimization which increases chances to inline loadCacheNext. */
    inline void throwEof()
    {
        throw CppRuntimeException("BitStreamReader: Reached eof(), reading from stream failed!");
    }

    /** Loads next 32/64 bits to 32/64 bit-cache. */
    inline void loadCacheNext(ReaderContext& ctx, uint8_t numBits)
    {
        static const uint8_t cacheBitSize = sizeof(BaseType) * 8;

        // ctx.bitIndex is always byte aligned and ctx.cacheNumBits is always zero in this call
        const size_t byteIndex = ctx.bitIndex >> 3U;
        if (ctx.bufferBitSize >= ctx.bitIndex + cacheBitSize)
        {
            ctx.cache =
#ifdef ZSERIO_RUNTIME_64BIT
                    parse64(ctx.buffer.begin() + byteIndex);
#else
                    parse32(ctx.buffer.begin() + byteIndex);
#endif
            ctx.cacheNumBits = cacheBitSize;
        }
        else
        {
            if (ctx.bitIndex + numBits > ctx.bufferBitSize)
                throwEof();

            ctx.cacheNumBits = static_cast<uint8_t>(ctx.bufferBitSize - ctx.bitIndex);

            // buffer must be always available in full bytes, even if some last bits are not used
            const uint8_t alignedNumBits = (ctx.cacheNumBits + 7U) & ~0x7U;

            switch (alignedNumBits)
            {
#ifdef ZSERIO_RUNTIME_64BIT
            case 64:
                ctx.cache = parse64(ctx.buffer.begin() + byteIndex);
                break;
            case 56:
                ctx.cache = parse56(ctx.buffer.begin() + byteIndex);
                break;
            case 48:
                ctx.cache = parse48(ctx.buffer.begin() + byteIndex);
                break;
            case 40:
                ctx.cache = parse40(ctx.buffer.begin() + byteIndex);
                break;
#endif
            case 32:
                ctx.cache = parse32(ctx.buffer.begin() + byteIndex);
                break;
            case 24:
                ctx.cache = parse24(ctx.buffer.begin() + byteIndex);
                break;
            case 16:
                ctx.cache = parse16(ctx.buffer.begin() + byteIndex);
                break;
            default: // 8
                ctx.cache = parse8(ctx.buffer.begin() + byteIndex);
                break;
            }

            ctx.cache >>= static_cast<uint8_t>(alignedNumBits - ctx.cacheNumBits);
        }
    }

    /** Unchecked implementation of readBits. */
    inline BaseType readBitsImpl(ReaderContext& ctx, uint8_t numBits)
    {
        BaseType value = 0;
        if (ctx.cacheNumBits < numBits)
        {
            // read all remaining cache bits
            value = ctx.cache & MASK_TABLE[ctx.cacheNumBits];
            ctx.bitIndex += ctx.cacheNumBits;
            numBits -= ctx.cacheNumBits;

            // load next piece of buffer into cache
            loadCacheNext(ctx, numBits);

            // add the remaining bits to the result
            // if numBits is sizeof(BaseType) * 8 here, value is already 0 (see MASK_TABLE[0])
            if (numBits < sizeof(BaseType) * 8)
                value <<= numBits;
        }
        value |= ((ctx.cache >> static_cast<uint8_t>(ctx.cacheNumBits - numBits)) & MASK_TABLE[numBits]);
        ctx.cacheNumBits -= numBits;
        ctx.bitIndex += numBits;

        return value;
    }

    /** Unchecked version of readSignedBits. */
    inline BaseSignedType readSignedBitsImpl(ReaderContext& ctx, uint8_t numBits)
    {
        static const uint8_t typeSize = sizeof(BaseSignedType) * 8;
        BaseType value = readBitsImpl(ctx, numBits);

        // Skip the signed overflow correction if numBits == typeSize.
        // In that case, the value that comes out the readBits function
        // is already correct.
        if (numBits != 0 && numBits < typeSize &&
                (value >= (static_cast<BaseType>(1) << static_cast<uint8_t>(numBits - 1))))
        {
            value -= static_cast<BaseType>(1) << numBits;
        }

        return static_cast<BaseSignedType>(value);
    }

#ifndef ZSERIO_RUNTIME_64BIT
    /** Unchecked implementation of readBits64. Always reads > 32bit! */
    inline uint64_t readBits64Impl(ReaderContext& ctx, uint8_t numBits)
    {
        // read the first 32 bits
        numBits -= 32;
        uint64_t value = readBitsImpl(ctx, 32);

        // add the remaining bits
        value <<= numBits;
        value |= readBitsImpl(ctx, numBits);

        return value;
    }
#endif
} // namespace

BitStreamReader::ReaderContext::ReaderContext(Span<const uint8_t> readBuffer, size_t readBufferBitSize)
:   buffer(readBuffer),
    bufferBitSize(readBufferBitSize),
    cache(0),
    cacheNumBits(0),
    bitIndex(0)
{
    if (buffer.size() > MAX_BUFFER_SIZE)
    {
        throw CppRuntimeException("BitStreamReader: Buffer size exceeded limit '") << MAX_BUFFER_SIZE <<
                "' bytes!";
    }
}

BitStreamReader::BitStreamReader(const uint8_t* buffer, size_t bufferByteSize) :
        BitStreamReader(Span<const uint8_t>(buffer, bufferByteSize))
{}

BitStreamReader::BitStreamReader(Span<const uint8_t> buffer) :
        m_context(buffer, buffer.size() * 8)
{}

BitStreamReader::BitStreamReader(Span<const uint8_t> buffer, size_t bufferBitSize) :
        m_context(buffer, bufferBitSize)
{
    if (buffer.size() < (bufferBitSize + 7) / 8)
    {
        throw CppRuntimeException("BitStreamReader: Wrong buffer bit size ('") << buffer.size() <<
                "' < '" << (bufferBitSize + 7) / 8 << "')!";
    }
}

BitStreamReader::BitStreamReader(const uint8_t* buffer, size_t bufferBitSize, BitsTag) :
        m_context(Span<const uint8_t>(buffer, (bufferBitSize + 7) / 8), bufferBitSize)
{}

uint32_t BitStreamReader::readBits(uint8_t numBits)
{
    checkNumBits(numBits);

    return static_cast<uint32_t>(readBitsImpl(m_context, numBits));
}

uint64_t BitStreamReader::readBits64(uint8_t numBits)
{
    checkNumBits64(numBits);

#ifdef ZSERIO_RUNTIME_64BIT
    return readBitsImpl(m_context, numBits);
#else
    if (numBits <= 32)
        return readBitsImpl(m_context, numBits);

    return readBits64Impl(m_context, numBits);
#endif
}

int64_t BitStreamReader::readSignedBits64(uint8_t numBits)
{
    checkNumBits64(numBits);

#ifdef ZSERIO_RUNTIME_64BIT
    return readSignedBitsImpl(m_context, numBits);
#else
    if (numBits <= 32)
        return readSignedBitsImpl(m_context, numBits);

    int64_t value = static_cast<int64_t>(readBits64Impl(m_context, numBits));

    // Skip the signed overflow correction if numBits == 64.
    // In that case, the value that comes out the readBits function
    // is already correct.
    const bool needsSignExtension =
            numBits < 64 && (static_cast<uint64_t>(value) >= (UINT64_C(1) << (numBits - 1)));
    if (needsSignExtension)
        value -= UINT64_C(1) << numBits;

    return value;
#endif
}

int32_t BitStreamReader::readSignedBits(uint8_t numBits)
{
    checkNumBits(numBits);

    return static_cast<int32_t>(readSignedBitsImpl(m_context, numBits));
}

int64_t BitStreamReader::readVarInt64()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const bool sign = (byte & VARINT_SIGN_1) != 0;
    uint64_t result = byte & VARINT_BYTE_1;
    if ((byte & VARINT_HAS_NEXT_1) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = static_cast<uint64_t>(result) << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);
}

int32_t BitStreamReader::readVarInt32()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const bool sign = (byte & VARINT_SIGN_1) != 0;
    uint32_t result = byte & VARINT_BYTE_1;
    if ((byte & VARINT_HAS_NEXT_1) == 0)
        return sign ? -static_cast<int32_t>(result) : static_cast<int32_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int32_t>(result) : static_cast<int32_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int32_t>(result) : static_cast<int32_t>(result);

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    return sign ? -static_cast<int32_t>(result) : static_cast<int32_t>(result);
}

int16_t BitStreamReader::readVarInt16()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const bool sign = (byte & VARINT_SIGN_1) != 0;
    uint16_t result = byte & VARINT_BYTE_1;
    if ((byte & VARINT_HAS_NEXT_1) == 0)
        return sign ? static_cast<int16_t>(-result) : static_cast<int16_t>(result);

    result = static_cast<uint16_t>(result << 8U) | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    return sign ? static_cast<int16_t>(-result) : static_cast<int16_t>(result);
}

uint64_t BitStreamReader::readVarUInt64()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint64_t result = byte & VARUINT_BYTE;
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    return result;
}

uint32_t BitStreamReader::readVarUInt32()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint32_t result = byte & VARUINT_BYTE;
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    return result;
}

uint16_t BitStreamReader::readVarUInt16()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint16_t result = byte & VARUINT_BYTE;
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    result = static_cast<uint16_t>(result << 8U) | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    return result;
}

int64_t BitStreamReader::readVarInt()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const bool sign = (byte & VARINT_SIGN_1) != 0;
    uint64_t result = byte & VARINT_BYTE_1;
    if ((byte & VARINT_HAS_NEXT_1) == 0)
        return sign ? (result == 0 ? INT64_MIN : -static_cast<int64_t>(result)) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    result = result << 7U | static_cast<uint8_t>(byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 9
    return sign ? -static_cast<int64_t>(result) : static_cast<int64_t>(result);
}

uint64_t BitStreamReader::readVarUInt()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint64_t result = byte & VARUINT_BYTE;
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 9
    return result;
}

uint32_t BitStreamReader::readVarSize()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint32_t result = byte & VARUINT_BYTE;
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7U | static_cast<uint8_t>(byte & VARUINT_BYTE);
    if ((byte & VARUINT_HAS_NEXT) == 0)
        return result;

    result = result << 8U | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    if (result > VARSIZE_MAX_VALUE)
        throw CppRuntimeException("BitStreamReader: Read value '") << result <<
                "' is out of range for varsize type!";

    return result;
}

float BitStreamReader::readFloat16()
{
    const uint16_t halfPrecisionFloatValue = static_cast<uint16_t>(readBitsImpl(m_context, 16));

    return convertUInt16ToFloat(halfPrecisionFloatValue);
}

float BitStreamReader::readFloat32()
{
    const uint32_t singlePrecisionFloatValue = static_cast<uint32_t>(readBitsImpl(m_context, 32));

    return convertUInt32ToFloat(singlePrecisionFloatValue);
}

double BitStreamReader::readFloat64()
{
#ifdef ZSERIO_RUNTIME_64BIT
    const uint64_t doublePrecisionFloatValue = static_cast<uint64_t>(readBitsImpl(m_context, 64));
#else
    const uint64_t doublePrecisionFloatValue = readBits64Impl(m_context, 64);
#endif

    return convertUInt64ToDouble(doublePrecisionFloatValue);
}

bool BitStreamReader::readBool()
{
    return readBitsImpl(m_context, 1) != 0;
}

void BitStreamReader::setBitPosition(BitPosType position)
{
    if (position > m_context.bufferBitSize)
        throw CppRuntimeException("BitStreamReader: Reached eof(), setting of bit position failed!");

    m_context.bitIndex = (position / 8) * 8; // set to byte aligned position
    m_context.cacheNumBits = 0; // invalidate cache
    const uint8_t skip = static_cast<uint8_t>(position - m_context.bitIndex);
    if (skip != 0)
        readBits(skip);
}

void BitStreamReader::alignTo(size_t alignment)
{
    const BitPosType offset = getBitPosition() % alignment;
    if (offset != 0)
    {
        const uint8_t skip = static_cast<uint8_t>(alignment - offset);
        readBits64(skip);
    }
}

uint8_t BitStreamReader::readByte()
{
    return static_cast<uint8_t>(readBitsImpl(m_context, 8));
}

} // namespace zserio
