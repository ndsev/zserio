#include <cstring>
#include <fstream>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/FloatUtil.h"
#include "zserio/BitStreamWriter.h"

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

BitStreamWriter::BitStreamWriter() :
        m_buffer(NULL),
        m_bitIndex(0),
        m_bufferBitSize(0),
        m_hasInternalBuffer(true),
        m_internalBuffer()
{
}

BitStreamWriter::BitStreamWriter(uint8_t* buffer, size_t bufferByteSize) :
        m_buffer(buffer),
        m_bitIndex(0),
        m_bufferBitSize(bufferByteSize * 8),
        m_hasInternalBuffer(false),
        m_internalBuffer()
{
}

BitStreamWriter::BitStreamWriter(BitBuffer& bitBuffer) :
        m_buffer(bitBuffer.getBuffer()),
        m_bitIndex(0),
        m_bufferBitSize(bitBuffer.getBitSize()),
        m_hasInternalBuffer(false),
        m_internalBuffer()
{
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
            zserio::bitSizeOfVarInt64(data));
}

void BitStreamWriter::writeVarInt32(int32_t data)
{
    static const uint8_t valBitsVarInt32[] = { 6, 7, 7, 8 };
    writeVarNum(data, valBitsVarInt32, sizeof(valBitsVarInt32) / sizeof(valBitsVarInt32[0]),
            zserio::bitSizeOfVarInt32(data));
}

void BitStreamWriter::writeVarInt16(int16_t data)
{
    static const uint8_t valBitsVarInt16[] = { 6, 8 };
    writeVarNum(data, valBitsVarInt16, sizeof(valBitsVarInt16) / sizeof(valBitsVarInt16[0]),
            zserio::bitSizeOfVarInt16(data));
}

void BitStreamWriter::writeVarUInt64(uint64_t data)
{
    static const uint8_t valBitsVarUInt64[] = { 7, 7, 7, 7, 7, 7, 7, 8 };
    writeVarAbsNum(data, false, valBitsVarUInt64, sizeof(valBitsVarUInt64) / sizeof(valBitsVarUInt64[0]),
            zserio::bitSizeOfVarUInt64(data));
}

void BitStreamWriter::writeVarUInt32(uint32_t data)
{
    static const uint8_t valBitsVarUInt32[] = { 7, 7, 7, 8 };
    writeVarAbsNum(data, false, valBitsVarUInt32, sizeof(valBitsVarUInt32) / sizeof(valBitsVarUInt32[0]),
            zserio::bitSizeOfVarUInt32(data));
}

void BitStreamWriter::writeVarUInt16(uint16_t data)
{
    static const uint8_t valBitsVarUInt16[] = { 7, 8 };
    writeVarAbsNum(data, false, valBitsVarUInt16, sizeof(valBitsVarUInt16) / sizeof(valBitsVarUInt16[0]),
            zserio::bitSizeOfVarUInt16(data));
}

void BitStreamWriter::writeVarInt(int64_t data)
{
    static const uint8_t valBitsVarInt[] = { 6, 7, 7, 7, 7, 7, 7, 7, 8 };
    if (data == INT64_MIN)
        writeBits(0x80, 8); // INT64_MIN is encoded as -0
    else
        writeVarNum(data, valBitsVarInt, sizeof(valBitsVarInt) / sizeof(valBitsVarInt[0]),
                zserio::bitSizeOfVarInt(data));
}

void BitStreamWriter::writeVarUInt(uint64_t data)
{
    static const uint8_t valBitsVarUInt[] = { 7, 7, 7, 7, 7, 7, 7, 7, 8 };
    writeVarAbsNum(data, false, valBitsVarUInt, sizeof(valBitsVarUInt) / sizeof(valBitsVarUInt[0]),
            zserio::bitSizeOfVarUInt(data));
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

void BitStreamWriter::writeString(const std::string& data)
{
    const size_t len = data.size();
    BitStreamWriter::writeVarUInt64(len);
    for (size_t i = 0; i < len; ++i)
    {
        BitStreamWriter::writeBits(static_cast<uint8_t>(data[i]), 8);
    }
}

void BitStreamWriter::writeBool(bool data)
{
    BitStreamWriter::writeBits((data ? 1 : 0), 1);
}

void BitStreamWriter::writeBitBuffer(const BitBuffer& bitBuffer)
{
    const size_t bitSize = bitBuffer.getBitSize();
    writeVarUInt64(bitSize);

    const uint8_t* buffer = bitBuffer.getBuffer();
    size_t numBytesToWrite = bitSize / 8;
    const uint8_t numRestBits = static_cast<uint8_t>(bitSize - numBytesToWrite * 8);
    const BitPosType beginBitPosition = getBitPosition();
    if ((beginBitPosition & 0x07) != 0)
    {
        // we are not aligned to byte
        while (numBytesToWrite > 0)
        {
            writeUnsignedBits(*buffer, 8);
            buffer++;
            numBytesToWrite--;
        }
    }
    else
    {
        // we are aligned to byte
        setBitPosition(beginBitPosition + numBytesToWrite * 8);
        if (hasWriteBuffer())
            memcpy(m_buffer + beginBitPosition / 8, buffer, numBytesToWrite);
        buffer += numBytesToWrite;
    }

    if (numRestBits > 0)
        writeUnsignedBits(*buffer >> (8 - numRestBits), numRestBits);
}

void BitStreamWriter::setBitPosition(BitPosType position)
{
    if (hasWriteBuffer())
    {
        if (!ensureCapacity(position))
            throw CppRuntimeException("BitStreamWriter: Reached eof(), setting of bit position failed.");
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
    writeBufferByteSize = (m_bufferBitSize + 7) / 8;

    return m_buffer;
}

void BitStreamWriter::writeBufferToFile(const std::string& filename) const
{
    std::ofstream os(filename.c_str(), std::ofstream::binary);
    if (!os)
        throw CppRuntimeException("WriteBitStreamToFile: Failed to open '" + filename +"' for writing!");

    os.write(reinterpret_cast<const char*>(m_buffer), (m_bufferBitSize + 7) / 8);
    if (!os)
        throw CppRuntimeException("WriteBitStreamToFile: Failed to write '" + filename +"'!");
}

inline void BitStreamWriter::writeUnsignedBits(uint32_t data, uint8_t numBits)
{
    if (!hasWriteBuffer())
    {
        m_bitIndex += numBits;
        return;
    }

    if (!ensureCapacity(m_bitIndex + numBits))
        throw CppRuntimeException("BitStreamWriter: Reached eof(), writing to stream failed.");

    uint8_t restNumBits = numBits;
    uint8_t bitsFree = 8 - (m_bitIndex & 0x07);
    size_t byteIndex = m_bitIndex / 8;

    if (restNumBits > bitsFree)
    {
        // first part
        const uint8_t shiftNum = restNumBits - bitsFree;
        const uint32_t mask = 0xFFFFFFFFU >> (32 - restNumBits);
        const uint8_t maskedByte = m_buffer[byteIndex] & ~static_cast<uint8_t>(mask >> shiftNum);
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
        const uint8_t maskedByte = m_buffer[byteIndex] & ~static_cast<uint8_t>(mask << shiftNum);
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
        writeUnsignedBits(static_cast<uint32_t>(data >> 32), numBits - 32);
        writeUnsignedBits(static_cast<uint32_t>(data), 32);
    }
}

inline void BitStreamWriter::writeVarNum(int64_t value, const uint8_t* valBits, size_t valBitsSize,
        size_t numVarBits)
{
    const uint64_t absValue = static_cast<uint64_t>(value < 0 ? -value : value);
    writeVarAbsNum(absValue, value < 0, valBits, valBitsSize, numVarBits);
}

inline void BitStreamWriter::writeVarAbsNum(uint64_t value, bool sign, const uint8_t* valBits,
        size_t valBitsSize, size_t numVarBits)
{
    static const uint64_t bitMasks[8] = { 1, 3, 7, 15, 31, 63, 127, 255 };
    const size_t numVarBytes = bitsToBytes(numVarBits);
    for (size_t i = numVarBytes; i > 0; i--)
    {
        const uint8_t numBits = valBits[numVarBytes - i];
        if (numBits < 7)
        {
            writeBool(sign); // sign
        }
        if (numBits < 8)
        {
            writeBool(i > 1); // hasNextByte
        }
        const size_t shiftBits = (i - 1) * 7 + ((numVarBytes == valBitsSize && i > 1) ? 1 : 0);
        writeBits(static_cast<uint8_t>((value >> shiftBits) & bitMasks[numBits - 1]), numBits);
    }
}

inline bool BitStreamWriter::ensureCapacity(size_t bitSize)
{
    if (bitSize > m_bufferBitSize)
    {
        if (!m_hasInternalBuffer)
            return false;

        // we have internal buffer which can be resized
        const size_t missingBytes = (bitSize - m_bufferBitSize + 7) / 8;
        m_internalBuffer.resize(m_internalBuffer.size() + missingBytes);
        m_buffer = &m_internalBuffer[0];
        m_bufferBitSize = m_internalBuffer.size() * 8;
    }

    return true;
}

} // namespace zserio
