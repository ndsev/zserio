#include <cstring>

#include "BitStreamException.h"
#include "CppRuntimeException.h"
#include "StringConvertUtil.h"
#include "BitPositionUtil.h"
#include "BitSizeOfCalculator.h"
#include "BitStreamWriter.h"

namespace zserio
{

static const uint32_t MAX_U32_VALUES[] =
{
    0x00U,
    0x0001U,     0x0003U,     0x0007U,     0x000fU,
    0x001fU,     0x003fU,     0x007fU,     0x00ffU,
    0x01ffU,     0x03ffU,     0x07ffU,     0x0fffU,
    0x1fffU,     0x3fffU,     0x7fffU,     0xffffU,
    0x0001ffffU, 0x0003ffffU, 0x0007ffffU, 0x000fffffU,
    0x001fffffU, 0x003fffffU, 0x007fffffU, 0x00ffffffU,
    0x01ffffffU, 0x03ffffffU, 0x07ffffffU, 0x0fffffffU,
    0x1fffffffU, 0x3fffffffU, 0x7fffffffU, 0xffffffffU,
};

static const int32_t MIN_I32_VALUES[] =
{
    0,
   -0x0001,     -0x0002,     -0x0004,     -0x0008,
   -0x0010,     -0x0020,     -0x0040,     -0x0080,
   -0x0100,     -0x0200,     -0x0400,     -0x0800,
   -0x1000,     -0x2000,     -0x4000,     -0x8000,
   -0x00010000, -0x00020000, -0x00040000, -0x00080000,
   -0x00100000, -0x00200000, -0x00400000, -0x00800000,
   -0x01000000, -0x02000000, -0x04000000, -0x08000000,
   -0x10000000, -0x20000000, -0x40000000, INT32_MIN
};

static const int32_t MAX_I32_VALUES[] =
{
    0x00,
    0x0000,      0x0001,      0x0003,      0x0007,
    0x000f,      0x001f,      0x003f,      0x007f,
    0x00ff,      0x01ff,      0x03ff,      0x07ff,
    0x0fff,      0x1fff,      0x3fff,      0x7fff,
    0x0000ffff,  0x0001ffff,  0x0003ffff,  0x0007ffff,
    0x000fffff,  0x001fffff,  0x003fffff,  0x007fffff,
    0x00ffffff,  0x01ffffff,  0x03ffffff,  0x07ffffff,
    0x0fffffff,  0x1fffffff,  0x3fffffff,  0x7fffffff
};

static const uint64_t MAX_U64_VALUES[] =
{
    0x00ULL,
    0x0001ULL,             0x0003ULL,             0x0007ULL,             0x000fULL,
    0x001fULL,             0x003fULL,             0x007fULL,             0x00ffULL,
    0x01ffULL,             0x03ffULL,             0x07ffULL,             0x0fffULL,
    0x1fffULL,             0x3fffULL,             0x7fffULL,             0xffffULL,
    0x0001ffffULL,         0x0003ffffULL,         0x0007ffffULL,         0x000fffffULL,
    0x001fffffULL,         0x003fffffULL,         0x007fffffULL,         0x00ffffffULL,
    0x01ffffffULL,         0x03ffffffULL,         0x07ffffffULL,         0x0fffffffULL,
    0x1fffffffULL,         0x3fffffffULL,         0x7fffffffULL,         0xffffffffULL,
    0x0001ffffffffULL,     0x0003ffffffffULL,     0x0007ffffffffULL,     0x000fffffffffULL,
    0x001fffffffffULL,     0x003fffffffffULL,     0x007fffffffffULL,     0x00ffffffffffULL,
    0x01ffffffffffULL,     0x03ffffffffffULL,     0x07ffffffffffULL,     0x0fffffffffffULL,
    0x1fffffffffffULL,     0x3fffffffffffULL,     0x7fffffffffffULL,     0xffffffffffffULL,
    0x0001ffffffffffffULL, 0x0003ffffffffffffULL, 0x0007ffffffffffffULL, 0x000fffffffffffffULL,
    0x001fffffffffffffULL, 0x003fffffffffffffULL, 0x007fffffffffffffULL, 0x00ffffffffffffffULL,
    0x01ffffffffffffffULL, 0x03ffffffffffffffULL, 0x07ffffffffffffffULL, 0x0fffffffffffffffULL,
    0x1fffffffffffffffULL, 0x3fffffffffffffffULL, 0x7fffffffffffffffULL, 0xffffffffffffffffULL
};

static const int64_t MIN_I64_VALUES[] =
{
    0LL,
   -0x0001LL,             -0x0002LL,             -0x0004LL,             -0x0008LL,
   -0x0010LL,             -0x0020LL,             -0x0040LL,             -0x0080LL,
   -0x0100LL,             -0x0200LL,             -0x0400LL,             -0x0800LL,
   -0x1000LL,             -0x2000LL,             -0x4000LL,             -0x8000LL,
   -0x00010000LL,         -0x00020000LL,         -0x00040000LL,         -0x00080000LL,
   -0x00100000LL,         -0x00200000LL,         -0x00400000LL,         -0x00800000LL,
   -0x01000000LL,         -0x02000000LL,         -0x04000000LL,         -0x08000000LL,
   -0x10000000LL,         -0x20000000LL,         -0x40000000LL,         -0x80000000LL,
   -0x000100000000LL,     -0x000200000000LL,     -0x000400000000LL,     -0x000800000000LL,
   -0x001000000000LL,     -0x002000000000LL,     -0x004000000000LL,     -0x008000000000LL,
   -0x010000000000LL,     -0x020000000000LL,     -0x040000000000LL,     -0x080000000000LL,
   -0x100000000000LL,     -0x200000000000LL,     -0x400000000000LL,     -0x800000000000LL,
   -0x0001000000000000LL, -0x0002000000000000LL, -0x0004000000000000LL, -0x0008000000000000LL,
   -0x0010000000000000LL, -0x0020000000000000LL, -0x0040000000000000LL, -0x0080000000000000LL,
   -0x0100000000000000LL, -0x0200000000000000LL, -0x0400000000000000LL, -0x0800000000000000LL,
   -0x1000000000000000LL, -0x2000000000000000LL, -0x4000000000000000LL, INT64_MIN
};

static const int64_t MAX_I64_VALUES[] =
{
    0x00LL,
    0x0000LL,              0x0001LL,              0x0003LL,             0x0007LL,
    0x000fLL,              0x001fLL,              0x003fLL,             0x007fLL,
    0x00ffLL,              0x01ffLL,              0x03ffLL,             0x07ffLL,
    0x0fffLL,              0x1fffLL,              0x3fffLL,             0x7fffLL,
    0x0000ffffLL,          0x0001ffffLL,          0x0003ffffLL,         0x0007ffffLL,
    0x000fffffLL,          0x001fffffLL,          0x003fffffLL,         0x007fffffLL,
    0x00ffffffLL,          0x01ffffffLL,          0x03ffffffLL,         0x07ffffffLL,
    0x0fffffffLL,          0x1fffffffLL,          0x3fffffffLL,         0x7fffffffLL,
    0x0000ffffffffLL,      0x0001ffffffffLL,      0x0003ffffffffLL,     0x0007ffffffffLL,
    0x000fffffffffLL,      0x001fffffffffLL,      0x003fffffffffLL,     0x007fffffffffLL,
    0x00ffffffffffLL,      0x01ffffffffffLL,      0x03ffffffffffLL,     0x07ffffffffffLL,
    0x0fffffffffffLL,      0x1fffffffffffLL,      0x3fffffffffffLL,     0x7fffffffffffLL,
    0x0000ffffffffffffLL,  0x0001ffffffffffffLL,  0x0003ffffffffffffLL, 0x0007ffffffffffffLL,
    0x000fffffffffffffLL,  0x001fffffffffffffLL,  0x003fffffffffffffLL, 0x007fffffffffffffLL,
    0x00ffffffffffffffLL,  0x01ffffffffffffffLL,  0x03ffffffffffffffLL, 0x07ffffffffffffffLL,
    0x0fffffffffffffffLL,  0x1fffffffffffffffLL,  0x3fffffffffffffffLL, 0x7fffffffffffffffLL
};

BitStreamWriter::BitStreamWriter()
  : m_buffer(NULL),
    m_bitIndex(0),
    m_bufferBitSize(0),
    m_hasInternalBuffer(true),
    m_internalBuffer()
{
}

BitStreamWriter::BitStreamWriter(uint8_t* buffer, size_t bufferByteSize)
  : m_buffer(buffer),
    m_bitIndex(0),
    m_bufferBitSize(bufferByteSize * 8),
    m_hasInternalBuffer(false),
    m_internalBuffer()
{
    std::memset(m_buffer, 0, bufferByteSize);
}

BitStreamWriter::~BitStreamWriter()
{
}

void BitStreamWriter::writeBits(uint32_t data, uint8_t numBits)
{
    if (numBits > sizeof(uint32_t) * 8 || data > MAX_U32_VALUES[numBits])
        throw CppRuntimeException("BitStreamWriter: Writing of " + convertToString(numBits) + "-bits value '" +
                convertToString(data) + "' failed.");

    writeUnsignedBits(data, numBits);
}

void BitStreamWriter::writeBits64(uint64_t data, uint8_t numBits)
{
    if (numBits > sizeof(uint64_t) * 8 || data > MAX_U64_VALUES[numBits])
        throw CppRuntimeException("BitStreamWriter: Writing of " + convertToString(numBits) + "-bits value '" +
                convertToString(data) + "' failed.");

    writeUnsignedBits64(data, numBits);
}

void BitStreamWriter::writeSignedBits(int32_t data, uint8_t numBits)
{
    if (numBits > sizeof(int32_t) * 8 || data < MIN_I32_VALUES[numBits] || data > MAX_I32_VALUES[numBits])
        throw CppRuntimeException("BitStreamWriter: Writing of " + convertToString(numBits) + "-bits value '" +
                convertToString(data) + "' failed.");

    writeUnsignedBits(static_cast<uint32_t>(data) & MAX_U32_VALUES[numBits], numBits);
}

void BitStreamWriter::writeSignedBits64(int64_t data, uint8_t numBits)
{
    if (numBits > sizeof(int64_t) * 8 || data < MIN_I64_VALUES[numBits] || data > MAX_I64_VALUES[numBits])
        throw CppRuntimeException("BitStreamWriter: Writing of " + convertToString(numBits) + "-bits value '" +
                convertToString(data) + "' failed.");

    writeUnsignedBits64(static_cast<uint64_t>(data) & MAX_U64_VALUES[numBits], numBits);
}

void BitStreamWriter::writeVarInt64(int64_t data)
{
    static const uint8_t valBitsVarInt64[] = { 6, 7, 7, 7, 7, 7, 7, 8 };
    writeVarNum(data, valBitsVarInt64, sizeof(valBitsVarInt64) / sizeof(valBitsVarInt64[0]),
            getBitSizeOfVarInt64(data));
}

void BitStreamWriter::writeVarInt32(int32_t data)
{
    static const uint8_t valBitsVarInt32[] = { 6, 7, 7, 8 };
    writeVarNum(data, valBitsVarInt32, sizeof(valBitsVarInt32) / sizeof(valBitsVarInt32[0]),
            getBitSizeOfVarInt32(data));
}

void BitStreamWriter::writeVarInt16(int16_t data)
{
    static const uint8_t valBitsVarInt16[] = { 6, 8 };
    writeVarNum(data, valBitsVarInt16, sizeof(valBitsVarInt16) / sizeof(valBitsVarInt16[0]),
            getBitSizeOfVarInt16(data));
}

void BitStreamWriter::writeVarUInt64(uint64_t data)
{
    static const uint8_t valBitsVarUInt64[] = { 7, 7, 7, 7, 7, 7, 7, 8 };
    writeVarNum(data, valBitsVarUInt64, sizeof(valBitsVarUInt64) / sizeof(valBitsVarUInt64[0]),
            getBitSizeOfVarUInt64(data));
}

void BitStreamWriter::writeVarUInt32(uint32_t data)
{
    static const uint8_t valBitsVarUInt32[] = { 7, 7, 7, 8 };
    writeVarNum(data, valBitsVarUInt32, sizeof(valBitsVarUInt32) / sizeof(valBitsVarUInt32[0]),
            getBitSizeOfVarUInt32(data));
}

void BitStreamWriter::writeVarUInt16(uint16_t data)
{
    static const uint8_t valBitsVarUInt16[] = { 7, 8 };
    writeVarNum(data, valBitsVarUInt16, sizeof(valBitsVarUInt16) / sizeof(valBitsVarUInt16[0]),
            getBitSizeOfVarUInt16(data));
}

void BitStreamWriter::writeFloat16(float data)
{
    // Converts a 32 bit floating point number to a 16bit unsigned integer.
    // The original code comes from:
    // http://www.mathworks.com/matlabcentral/fileexchange/23173-ieee-754r-half-precision-floating-point-converter

    uint16_t result = 0;

    uint32_t const* intData = reinterpret_cast<uint32_t*>(&data);
    const uint32_t bits = *intData;

    if ((bits & UINT32_C(0x7FFFFFFF)) == 0) // Signed zero
    {
        result = uint16_t(bits >> 16);  // Return the signed zero
    }
    else
    {
        uint32_t xs = bits & UINT32_C(0x80000000);  // Pick off sign bit
        uint32_t xe = bits & UINT32_C(0x7F800000);  // Pick off exponent bits
        uint32_t xm = bits & UINT32_C(0x007FFFFF);  // Pick off mantissa bits

        if (xe == 0)
        {
            // Denormal will underflow, return a signed zero
            result = uint16_t(xs >> 16);
        }
        else if (xe == UINT32_C(0x7F800000))
        {
            // Inf or NaN (all the exponent bits are set)
            if (xm == 0)
            {
                // If mantissa is zero ...
                result = static_cast<uint16_t>((xs >> 16) | UINT16_C(0x7C00)); // Signed Inf
            }
            else
            {
                result = UINT16_C(0xFE00); // NaN, only 1st mantissa bit set
            }
        }
        else
        {
            // Normalized number
            uint16_t hs = static_cast<uint16_t>(xs >> 16); // Sign bit
            int hes = static_cast<int>(xe >> 23) - 127 + 15; // Exponent unbias the single, then bias the halfp
            uint16_t hm = 0;
            if (hes >= 0x1F)
            {
                // Overflow
                result = static_cast<uint16_t>((xs >> 16) | UINT16_C(0x7C00)); // Signed Inf
            }
            else if (hes <= 0)
            {
                // Underflow
                if ((14 - hes) > 24)
                {
                    // Mantissa shifted all the way off & no rounding possibility
                    hm = UINT16_C(0);  // Set mantissa to zero
                }
                else
                {
                    xm |= 0x00800000u;  // Add the hidden leading bit
                    hm = static_cast<uint16_t>(xm >> (14 - hes)); // Mantissa
                    if ((xm >> (13 - hes)) & UINT32_C(0x00000001)) // Check for rounding
                        hm += UINT16_C(1); // Round, might overflow into exp bit, but this is OK
                }
                result = hs | hm; // Combine sign bit and mantissa bits, biased exponent is zero
            }
            else
            {
                uint16_t he = uint16_t(hes << 10); // Exponent
                hm = static_cast<uint16_t>(xm >> 13); // Mantissa
                if (xm & UINT16_C(0x00001000)) // Check for rounding
                    result = (hs | he | hm) + UINT16_C(1); // Round, might overflow to inf, this is OK
                else
                    result = hs | he | hm;  // No rounding
            }
        }
    }

    writeBits( result, 16 );
}

void BitStreamWriter::writeString(const std::string& data)
{
    const uint64_t len = static_cast<uint64_t>(data.size());
    BitStreamWriter::writeVarUInt64(len);
    for (uint64_t i = 0; i < len; ++i)
    {
        BitStreamWriter::writeBits(static_cast<uint8_t>(data[i]), 8);
    }
}

void BitStreamWriter::writeBool(bool data)
{
    BitStreamWriter::writeBits((data ? 1 : 0), 1);
}

void BitStreamWriter::setBitPosition(BitPosType position)
{
    if (position > m_bufferBitSize)
    {
        throw BitStreamException("BitStreamWriter: Reached eof(), setting of bit position failed.");
    }

    m_bitIndex = position;
}

void BitStreamWriter::alignTo(size_t alignment)
{
    const BitPosType offset = getBitPosition() % alignment;
    if (offset != 0)
    {
        const uint8_t skip = static_cast<uint8_t>(alignment - offset);
        writeBits64(0, skip);
    }
}

const uint8_t* BitStreamWriter::getWriteBuffer(size_t& writeBufferByteSize) const
{
    writeBufferByteSize = m_bufferBitSize / 8;

    return m_buffer;
}

inline void BitStreamWriter::writeUnsignedBits(uint32_t data, uint8_t numBits)
{
    if (!hasWriteBuffer())
    {
        m_bitIndex += numBits;
        return;
    }

    const size_t freeBitSize = m_bufferBitSize - m_bitIndex;
    if (numBits > freeBitSize)
    {
        if (!m_hasInternalBuffer)
            throw BitStreamException("BitStreamWriter: Reached eof(), writing to stream failed.");

        // we have internal buffer which can be resized
        const size_t missingBytes = (numBits - freeBitSize + 7) / 8;
        m_internalBuffer.resize(m_internalBuffer.size() + missingBytes);
        m_buffer = &m_internalBuffer[0];
        m_bufferBitSize = m_internalBuffer.size() * 8;
    }

    const uint8_t org_numBits = numBits;
    uint8_t bits_free = 8 - (m_bitIndex & 0x07);
    uint32_t byte_index = m_bitIndex / 8;

    if (numBits > bits_free)
    {
        // first part
        m_buffer[byte_index++] |= static_cast<uint8_t>(data >> (numBits - bits_free));
        numBits -= bits_free;

        // middle parts
        while (numBits >= 8)
        {
            numBits -= 8;
            m_buffer[byte_index++] = static_cast<uint8_t>((data >> numBits) & MAX_U32_VALUES[8]);
        }

        // reset bits free
        bits_free = 8;
    }

    // last part
    if (numBits > 0)
    {
        m_buffer[byte_index] |= static_cast<uint8_t>((data & MAX_U32_VALUES[numBits]) << (bits_free - numBits));
    }

    m_bitIndex += org_numBits;
}

inline void BitStreamWriter::writeUnsignedBits64(uint64_t data, uint8_t numBits)
{
    if (numBits <= 32)
    {
        writeUnsignedBits(static_cast<uint32_t>(data), numBits);
    }
    else
    {
        writeUnsignedBits(static_cast<uint32_t>(data >> 32), numBits - 32);
        writeUnsignedBits(static_cast<uint32_t>(data), 32);
    }
}

inline void BitStreamWriter::writeVarNum(int64_t value, const uint8_t* valBits, size_t valBitsSize,
        size_t numVarBits)
{
    static const uint64_t bitMasks[8] = { 1, 3, 7, 15, 31, 63, 127, 255 };

    const uint64_t absValue = static_cast<uint64_t>(value < 0 ? -value : value);
    const size_t numVarBytes = bitsToBytes(numVarBits);
    for (size_t i = numVarBytes; i > 0; i--)
    {
        const uint8_t numBits = valBits[numVarBytes - i];
        if (numBits < 7)
        {
            writeBool(value < 0); // sign
        }
        if (numBits < 8)
        {
            writeBool(i > 1); // hasNextByte
        }
        const int shiftBits = (i - 1) * 7 + ((numVarBytes==valBitsSize && i > 1 ) ? 1 : 0);
        writeBits(((absValue >> shiftBits) & bitMasks[numBits - 1]), numBits);
    }
}

} // namespace zserio
