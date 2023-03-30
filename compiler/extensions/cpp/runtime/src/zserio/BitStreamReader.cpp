#include <limits>

#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/FloatUtil.h"

namespace zserio
{

namespace
{
    // max size calculated to prevent overflows in internal comparisons
    static const size_t MAX_BUFFER_SIZE = std::numeric_limits<size_t>::max() / 8 - 4;

    typedef BitStreamReader::BitPosType BitPosType;
    typedef BitStreamReader::ReaderContext ReaderContext;

#ifdef ZSERIO_RUNTIME_64BIT
    typedef uint64_t BaseType;
    typedef int64_t BaseSignedType;
#else
    typedef uint32_t BaseType;
    typedef int32_t BaseSignedType;
#endif

    inline BaseType& getCacheBuffer(ReaderContext::BitCache& cache)
    {
#ifdef ZSERIO_RUNTIME_64BIT
        return cache.buffer64;
#else
        return cache.buffer32;
#endif
    }

    static const BaseType MASK_TABLE[] =
    {
#ifdef ZSERIO_RUNTIME_64BIT
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
#else
        UINT32_C(0x00),
        UINT32_C(0x0001),     UINT32_C(0x0003),     UINT32_C(0x0007),     UINT32_C(0x000f),
        UINT32_C(0x001f),     UINT32_C(0x003f),     UINT32_C(0x007f),     UINT32_C(0x00ff),
        UINT32_C(0x01ff),     UINT32_C(0x03ff),     UINT32_C(0x07ff),     UINT32_C(0x0fff),
        UINT32_C(0x1fff),     UINT32_C(0x3fff),     UINT32_C(0x7fff),     UINT32_C(0xffff),
        UINT32_C(0x0001ffff), UINT32_C(0x0003ffff), UINT32_C(0x0007ffff), UINT32_C(0x000fffff),
        UINT32_C(0x001fffff), UINT32_C(0x003fffff), UINT32_C(0x007fffff), UINT32_C(0x00ffffff),
        UINT32_C(0x01ffffff), UINT32_C(0x03ffffff), UINT32_C(0x07ffffff), UINT32_C(0x0fffffff),
        UINT32_C(0x1fffffff), UINT32_C(0x3fffffff), UINT32_C(0x7fffffff), UINT32_C(0xffffffff)
#endif
    };

    static const uint8_t VARINT_SIGN_1 = UINT8_C(0x80);
    static const uint8_t VARINT_BYTE_1 = UINT8_C(0x3f);
    static const uint8_t VARINT_BYTE_N = UINT8_C(0x7f);
    static const uint8_t VARINT_HAS_NEXT_1 = UINT8_C(0x40);
    static const uint8_t VARINT_HAS_NEXT_N = UINT8_C(0x80);

    static const uint8_t VARUINT_BYTE = UINT8_C(0x7f);
    static const uint8_t VARUINT_HAS_NEXT = UINT8_C(0x80);

    static const uint32_t VARSIZE_MAX_VALUE = (UINT32_C(1) << 31) - 1;

#ifdef ZSERIO_RUNTIME_64BIT
    inline BaseType parse64(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 56 |
               static_cast<BaseType>(buffer[1]) << 48 |
               static_cast<BaseType>(buffer[2]) << 40 |
               static_cast<BaseType>(buffer[3]) << 32 |
               static_cast<BaseType>(buffer[4]) << 24 |
               static_cast<BaseType>(buffer[5]) << 16 |
               static_cast<BaseType>(buffer[6]) << 8 |
               static_cast<BaseType>(buffer[7]);
    }

    inline BaseType parse56(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 48 |
               static_cast<BaseType>(buffer[1]) << 40 |
               static_cast<BaseType>(buffer[2]) << 32 |
               static_cast<BaseType>(buffer[3]) << 24 |
               static_cast<BaseType>(buffer[4]) << 16 |
               static_cast<BaseType>(buffer[5]) << 8 |
               static_cast<BaseType>(buffer[6]);
    }

    inline BaseType parse48(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 40 |
               static_cast<BaseType>(buffer[1]) << 32 |
               static_cast<BaseType>(buffer[2]) << 24 |
               static_cast<BaseType>(buffer[3]) << 16 |
               static_cast<BaseType>(buffer[4]) << 8 |
               static_cast<BaseType>(buffer[5]);
    }

    inline BaseType parse40(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 32 |
               static_cast<BaseType>(buffer[1]) << 24 |
               static_cast<BaseType>(buffer[2]) << 16 |
               static_cast<BaseType>(buffer[3]) << 8 |
               static_cast<BaseType>(buffer[4]);
    }
#endif
    inline BaseType parse32(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 24 |
               static_cast<BaseType>(buffer[1]) << 16 |
               static_cast<BaseType>(buffer[2]) << 8 |
               static_cast<BaseType>(buffer[3]);
    }

    inline BaseType parse24(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 16 |
               static_cast<BaseType>(buffer[1]) << 8 |
               static_cast<BaseType>(buffer[2]);
    }

    inline BaseType parse16(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]) << 8 |
               static_cast<BaseType>(buffer[1]);
    }

    inline BaseType parse8(const uint8_t* buffer)
    {
        return static_cast<BaseType>(buffer[0]);
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
        BaseType& cacheBuffer = getCacheBuffer(ctx.cache);

        // ctx.bitIndex is always byte aligned and ctx.cacheNumBits is always zero in this call
        const size_t byteIndex = ctx.bitIndex >> 3;
        if (ctx.bufferBitSize >= ctx.bitIndex + cacheBitSize)
        {
            cacheBuffer =
#ifdef ZSERIO_RUNTIME_64BIT
                    parse64(ctx.buffer + byteIndex);
#else
                    parse32(ctx.buffer + byteIndex);
#endif
            ctx.cacheNumBits = cacheBitSize;
        }
        else
        {
            if (ctx.bitIndex + numBits > ctx.bufferBitSize)
                throwEof();

            ctx.cacheNumBits = static_cast<uint8_t>(ctx.bufferBitSize - ctx.bitIndex);

            // buffer must be always available in full bytes, even if some last bits are not used
            const uint8_t alignedNumBits = (ctx.cacheNumBits + 7) & ~0x7;

            switch (alignedNumBits)
            {
#ifdef ZSERIO_RUNTIME_64BIT
            case 64:
                cacheBuffer = parse64(ctx.buffer + byteIndex);
                break;
            case 56:
                cacheBuffer = parse56(ctx.buffer + byteIndex);
                break;
            case 48:
                cacheBuffer = parse48(ctx.buffer + byteIndex);
                break;
            case 40:
                cacheBuffer = parse40(ctx.buffer + byteIndex);
                break;
#endif
            case 32:
                cacheBuffer = parse32(ctx.buffer + byteIndex);
                break;
            case 24:
                cacheBuffer = parse24(ctx.buffer + byteIndex);
                break;
            case 16:
                cacheBuffer = parse16(ctx.buffer + byteIndex);
                break;
            default: // 8
                cacheBuffer = parse8(ctx.buffer + byteIndex);
                break;
            }

            cacheBuffer >>= alignedNumBits - ctx.cacheNumBits;
        }
    }

    /** Unchecked implementation of readBits. */
    inline BaseType readBitsImpl(ReaderContext& ctx, uint8_t numBits)
    {
        BaseType value = 0;
        const BaseType& cacheBuffer = getCacheBuffer(ctx.cache);

        if (ctx.cacheNumBits < numBits)
        {
            // read all remaining cache bits
            value = cacheBuffer & MASK_TABLE[ctx.cacheNumBits];
            ctx.bitIndex += ctx.cacheNumBits;
            numBits -= ctx.cacheNumBits;

            // load next piece of buffer into cache
            loadCacheNext(ctx, numBits);

            // add the remaining bits to the result
            // if numBits is sizeof(BaseType) * 8 here, value is already 0 (see MASK_TABLE[0])
            if (numBits < sizeof(BaseType) * 8)
                value <<= numBits;
        }
        value |= ((cacheBuffer >> (ctx.cacheNumBits - numBits)) & MASK_TABLE[numBits]);
        ctx.cacheNumBits -= numBits;
        ctx.bitIndex += numBits;

        // align each field to byte immediately
        const size_t alignment = 8;
        const BitPosType offset = ctx.bitIndex % alignment;
        if (offset != 0)
        {
            const uint8_t skip = static_cast<uint8_t>(alignment - offset);
            readBitsImpl(ctx, skip);
        }

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
        if (numBits != 0 && numBits < typeSize && (value >= (static_cast<BaseType>(1) << (numBits - 1))))
            value -= static_cast<BaseType>(1) << numBits;

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

BitStreamReader::ReaderContext::ReaderContext(const uint8_t* constBuffer, size_t constBufferBitSize)
:   buffer(const_cast<uint8_t*>(constBuffer)),
    bufferBitSize(constBufferBitSize),
    cacheNumBits(0),
    bitIndex(0)
{
    Init();

    const size_t bufferByteSize = (bufferBitSize + 7) / 8;
    if (bufferByteSize > MAX_BUFFER_SIZE)
    {
        throw CppRuntimeException("BitStreamReader: Buffer size exceeded limit '") << MAX_BUFFER_SIZE <<
                "' bytes!";
    }
}

void BitStreamReader::ReaderContext::Init()
{
#ifdef ZSERIO_RUNTIME_64BIT
    cache.buffer64 = 0;
#else
    cache.buffer32 = 0;
#endif
}

BitStreamReader::BitStreamReader(const uint8_t* buffer, size_t bufferByteSize) :
        BitStreamReader(buffer, bufferByteSize * 8, BitsTag())
{}

BitStreamReader::BitStreamReader(Span<const uint8_t> buffer) :
        BitStreamReader(buffer.data(), buffer.size() * 8, BitsTag())
{}

BitStreamReader::BitStreamReader(const uint8_t* buffer, size_t bufferBitSize, BitsTag) :
        m_context(buffer, bufferBitSize)
{}

BitStreamReader::~BitStreamReader()
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
    const uint8_t sign = byte & VARINT_SIGN_1;
    int64_t result = byte & VARINT_BYTE_1;
    if (!(byte & VARINT_HAS_NEXT_1))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    return sign ? -result : result;
}

int32_t BitStreamReader::readVarInt32()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const uint8_t sign = byte & VARINT_SIGN_1;
    int32_t result = byte & VARINT_BYTE_1;
    if ((byte & VARINT_HAS_NEXT_1) == 0)
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARINT_BYTE_N);
    if ((byte & VARINT_HAS_NEXT_N) == 0)
        return sign ? -result : result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    return sign ? -result : result;
}

int16_t BitStreamReader::readVarInt16()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const uint8_t sign = byte & VARINT_SIGN_1;
    int16_t result = byte & VARINT_BYTE_1;
    if (!(byte & VARINT_HAS_NEXT_1))
        return sign ? -result : result;

    result = static_cast<int16_t>(result << 8) | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    return sign ? -result : result;
}

uint64_t BitStreamReader::readVarUInt64()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint64_t result = byte & VARUINT_BYTE;
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    result = result << 8 | readBitsImpl(m_context, 8); // byte 8
    return result;
}

uint32_t BitStreamReader::readVarUInt32()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint32_t result = byte & VARUINT_BYTE;
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    return result;
}

uint16_t BitStreamReader::readVarUInt16()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint16_t result = byte & VARUINT_BYTE;
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    result = static_cast<uint16_t>(result << 8) | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    return result;
}

int64_t BitStreamReader::readVarInt()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    const uint8_t sign = byte & VARINT_SIGN_1;
    int64_t result = byte & VARINT_BYTE_1;
    if (!(byte & VARINT_HAS_NEXT_1))
        return sign ? (result == 0 ? INT64_MIN : -result) : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    result = result << 7 | (byte & VARINT_BYTE_N);
    if (!(byte & VARINT_HAS_NEXT_N))
        return sign ? -result : result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 9
    return sign ? -result : result;
}

uint64_t BitStreamReader::readVarUInt()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint64_t result = byte & VARUINT_BYTE;
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 6
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 7
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 8
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 9
    return result;
}

uint32_t BitStreamReader::readVarSize()
{
    uint8_t byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 1
    uint32_t result = byte & VARUINT_BYTE;
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 2
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 3
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    byte = static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 4
    result = result << 7 | (byte & VARUINT_BYTE);
    if (!(byte & VARUINT_HAS_NEXT))
        return result;

    result = result << 8 | static_cast<uint8_t>(readBitsImpl(m_context, 8)); // byte 5
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
