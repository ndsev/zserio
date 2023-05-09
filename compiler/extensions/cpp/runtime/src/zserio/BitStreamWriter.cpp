#include <cstring>
#include <fstream>
#include <array>
#include <algorithm>

#include "zserio/CppRuntimeException.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/FloatUtil.h"
#include "zserio/BitStreamWriter.h"

namespace zserio
{

static const std::array<uint32_t, 33> MAX_U32_VALUES =
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

static const std::array<int32_t, 33> MIN_I32_VALUES =
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

static const std::array<int32_t, 33> MAX_I32_VALUES =
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

static const std::array<uint64_t, 65> MAX_U64_VALUES =
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

static const std::array<int64_t, 65> MIN_I64_VALUES =
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

static const std::array<int64_t, 65> MAX_I64_VALUES =
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

BitStreamWriter::BitStreamWriter(uint8_t* buffer, size_t bufferBitSize, BitsTag) :
        m_buffer(buffer, (bufferBitSize + 7) / 8),
        m_bitIndex(0),
        m_bufferBitSize(bufferBitSize)
{}

BitStreamWriter::BitStreamWriter(uint8_t* buffer, size_t bufferByteSize) :
        BitStreamWriter(Span<uint8_t>(buffer, bufferByteSize))
{}

BitStreamWriter::BitStreamWriter(Span<uint8_t> buffer) :
        m_buffer(buffer),
        m_bitIndex(0),
        m_bufferBitSize(buffer.size() * 8)
{}

BitStreamWriter::BitStreamWriter(Span<uint8_t> buffer, size_t bufferBitSize) :
        m_buffer(buffer),
        m_bitIndex(0),
        m_bufferBitSize(bufferBitSize)
{
    if (buffer.size() < (bufferBitSize + 7) / 8)
    {
        throw CppRuntimeException("BitStreamWriter: Wrong buffer bit size ('") << buffer.size() <<
                "' < '" << (bufferBitSize + 7) / 8 << "')!";
    }
}

void BitStreamWriter::writeBits(uint32_t data, uint8_t numBits)
{
    if (numBits > sizeof(uint32_t) * 8 || data > MAX_U32_VALUES[numBits])
    {
        throw CppRuntimeException("BitStreamWriter: Writing of ") << numBits << "-bits value '" << data <<
                "' failed!";
    }

    writeUnsignedBits(data, numBits);
}

void BitStreamWriter::writeBits64(uint64_t data, uint8_t numBits)
{
    if (numBits > sizeof(uint64_t) * 8 || data > MAX_U64_VALUES[numBits])
    {
        throw CppRuntimeException("BitStreamWriter: Writing of ") << numBits << "-bits value '" << data <<
                "' failed!";
    }

    writeUnsignedBits64(data, numBits);
}

void BitStreamWriter::writeSignedBits(int32_t data, uint8_t numBits)
{
    if (numBits > sizeof(int32_t) * 8 || data < MIN_I32_VALUES[numBits] || data > MAX_I32_VALUES[numBits])
    {
        throw CppRuntimeException("BitStreamWriter: Writing of ") << numBits << "-bits value '" << data <<
                "' failed!";
    }

    writeUnsignedBits(static_cast<uint32_t>(data) & MAX_U32_VALUES[numBits], numBits);
}

void BitStreamWriter::writeSignedBits64(int64_t data, uint8_t numBits)
{
    if (numBits > sizeof(int64_t) * 8 || data < MIN_I64_VALUES[numBits] || data > MAX_I64_VALUES[numBits])
    {
        throw CppRuntimeException("BitStreamWriter: Writing of ") << numBits << "-bits value '" << data <<
                "' failed!";
    }

    writeUnsignedBits64(static_cast<uint64_t>(data) & MAX_U64_VALUES[numBits], numBits);
}

void BitStreamWriter::writeVarInt64(int64_t data)
{
    writeSignedVarNum(data, 8, zserio::bitSizeOfVarInt64(data) / 8);
}

void BitStreamWriter::writeVarInt32(int32_t data)
{
    writeSignedVarNum(data, 4, zserio::bitSizeOfVarInt32(data) / 8);
}

void BitStreamWriter::writeVarInt16(int16_t data)
{
    writeSignedVarNum(data, 2, zserio::bitSizeOfVarInt16(data) / 8);
}

void BitStreamWriter::writeVarUInt64(uint64_t data)
{
    writeUnsignedVarNum(data, 8, zserio::bitSizeOfVarUInt64(data) / 8);
}

void BitStreamWriter::writeVarUInt32(uint32_t data)
{
    writeUnsignedVarNum(data, 4, zserio::bitSizeOfVarUInt32(data) / 8);
}

void BitStreamWriter::writeVarUInt16(uint16_t data)
{
    writeUnsignedVarNum(data, 2, zserio::bitSizeOfVarUInt16(data) / 8);
}

void BitStreamWriter::writeVarInt(int64_t data)
{
    if (data == INT64_MIN)
        writeBits(0x80, 8); // INT64_MIN is encoded as -0
    else
        writeSignedVarNum(data, 9, zserio::bitSizeOfVarInt(data) / 8);
}

void BitStreamWriter::writeVarUInt(uint64_t data)
{
    writeUnsignedVarNum(data, 9, zserio::bitSizeOfVarUInt(data) / 8);
}

void BitStreamWriter::writeVarSize(uint32_t data)
{
    writeUnsignedVarNum(data, 5, zserio::bitSizeOfVarSize(data) / 8);
}

void BitStreamWriter::writeFloat16(float data)
{
    const uint16_t halfPrecisionFloat = convertFloatToUInt16(data);
    writeUnsignedBits(halfPrecisionFloat, 16);
}

void BitStreamWriter::writeFloat32(float data)
{
    const uint32_t singlePrecisionFloat = convertFloatToUInt32(data);
    writeUnsignedBits(singlePrecisionFloat, 32);
}

void BitStreamWriter::writeFloat64(double data)
{
    const uint64_t doublePrecisionFloat = convertDoubleToUInt64(data);
    writeUnsignedBits64(doublePrecisionFloat, 64);
}

void BitStreamWriter::writeBytes(Span<const uint8_t> data)
{
    const size_t len = data.size();
    writeVarSize(convertSizeToUInt32(len));

    const BitPosType beginBitPosition = getBitPosition();
    if ((beginBitPosition & 0x07U) != 0)
    {
        // we are not aligned to byte
        for (size_t i = 0; i < len; ++i)
            writeBits(data[i], 8);
    }
    else
    {
        // we are aligned to bytes
        setBitPosition(beginBitPosition + len * 8);
        if (hasWriteBuffer())
            std::copy(data.begin(), data.end(), m_buffer.begin() + beginBitPosition / 8);
    }
}

void BitStreamWriter::writeString(StringView data)
{
    const size_t len = data.size();
    writeVarSize(convertSizeToUInt32(len));

    const BitPosType beginBitPosition = getBitPosition();
    if ((beginBitPosition & 0x07U) != 0)
    {
        // we are not aligned to byte
        for (size_t i = 0; i < len; ++i)
            writeBits(static_cast<uint8_t>(data[i]), 8);
    }
    else
    {
        // we are aligned to bytes
        setBitPosition(beginBitPosition + len * 8);
        if (hasWriteBuffer())
            std::copy(data.begin(), data.begin() + len, m_buffer.data() + beginBitPosition / 8);
    }
}

void BitStreamWriter::writeBool(bool data)
{
    writeBits((data ? 1 : 0), 1);
}

void BitStreamWriter::setBitPosition(BitPosType position)
{
    if (hasWriteBuffer())
        checkCapacity(position);

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

const uint8_t* BitStreamWriter::getWriteBuffer() const
{
    return m_buffer.data();
}

Span<const uint8_t> BitStreamWriter::getBuffer() const
{
    return m_buffer;
}

void BitStreamWriter::writeUnsignedBits(uint32_t data, uint8_t numBits)
{
    if (!hasWriteBuffer())
    {
        m_bitIndex += numBits;
        return;
    }

    checkCapacity(m_bitIndex + numBits);

    uint8_t restNumBits = numBits;
    const uint8_t bitsUsed = m_bitIndex & 0x07U;
    uint8_t bitsFree = 8 - bitsUsed;
    size_t byteIndex = m_bitIndex / 8;

    if (restNumBits > bitsFree)
    {
        // first part
        const uint8_t shiftNum = restNumBits - bitsFree;
        const uint8_t maskedByte = m_buffer[byteIndex] & ~(0xFFU >> bitsUsed);
        m_buffer[byteIndex++] = maskedByte | static_cast<uint8_t>(data >> shiftNum);
        restNumBits -= bitsFree;

        // middle parts
        while (restNumBits >= 8)
        {
            restNumBits -= 8;
            m_buffer[byteIndex++] = static_cast<uint8_t>((data >> restNumBits) & MAX_U32_VALUES[8]);
        }

        // reset bits free
        bitsFree = 8;
    }

    // last part
    if (restNumBits > 0)
    {
        const uint8_t shiftNum = bitsFree - restNumBits;
        const uint32_t mask = MAX_U32_VALUES[restNumBits];
        const uint8_t maskedByte = m_buffer[byteIndex] &
                static_cast<uint8_t>(~static_cast<uint8_t>(mask << shiftNum));
        m_buffer[byteIndex] = maskedByte | static_cast<uint8_t>((data & mask) << shiftNum);
    }

    m_bitIndex += numBits;
}

inline void BitStreamWriter::writeUnsignedBits64(uint64_t data, uint8_t numBits)
{
    if (numBits <= 32)
    {
        writeUnsignedBits(static_cast<uint32_t>(data), numBits);
    }
    else
    {
        writeUnsignedBits(static_cast<uint32_t>(data >> 32U), numBits - 32);
        writeUnsignedBits(static_cast<uint32_t>(data), 32);
    }
}

inline void BitStreamWriter::writeSignedVarNum(int64_t value, size_t maxVarBytes, size_t numVarBytes)
{
    const uint64_t absValue = static_cast<uint64_t>(value < 0 ? -value : value);
    writeVarNum(absValue, true, value < 0, maxVarBytes, numVarBytes);
}

inline void BitStreamWriter::writeUnsignedVarNum(uint64_t value, size_t maxVarBytes, size_t numVarBytes)
{
    writeVarNum(value, false, false, maxVarBytes, numVarBytes);
}

inline void BitStreamWriter::writeVarNum(uint64_t value, bool hasSign, bool isNegative, size_t maxVarBytes,
        size_t numVarBytes)
{
    static const std::array<uint64_t, 8> bitMasks = { 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F, 0xFF };
    const bool hasMaxByteRange = (numVarBytes == maxVarBytes);

    for (size_t i = 0; i < numVarBytes; i++)
    {
        uint8_t byte = 0x00;
        uint8_t numBits = 8;
        const bool hasNextByte = (i < numVarBytes - 1);
        const bool hasSignBit = (hasSign && i == 0);
        if (hasSignBit)
        {
            if (isNegative)
                byte |= 0x80U;
            numBits--;
        }
        if (hasNextByte)
        {
            numBits--;
            byte |= (0x01U << numBits); // use bit 6 if signed bit is present, use bit 7 otherwise
        }
        else // this is the last byte
        {
            if (!hasMaxByteRange) // next byte indicator is not used in last byte in case of max byte range
                numBits--;
        }

        const size_t shiftBits = (numVarBytes - (i + 1)) * 7 + ((hasMaxByteRange && hasNextByte) ? 1 : 0);
        byte |= static_cast<uint8_t>((value >> shiftBits) & bitMasks[numBits - 1]);
        writeUnsignedBits(byte, 8);
    }
}

inline void BitStreamWriter::throwInsufficientCapacityException() const
{
    throw InsufficientCapacityException("BitStreamWriter: Reached end of bit buffer!");
}

inline void BitStreamWriter::checkCapacity(size_t bitSize) const
{
    if (bitSize > m_bufferBitSize)
        throwInsufficientCapacityException();
}

} // namespace zserio
