#ifndef ZSERIO_BIT_STREAM_WRITER_H_INC
#define ZSERIO_BIT_STREAM_WRITER_H_INC

#include <cstddef>
#include <string>
#include <vector>

#include "Types.h"

namespace zserio
{

class BitStreamWriter
{
public:
    typedef size_t BitPosType;

    BitStreamWriter();
    explicit BitStreamWriter(uint8_t* buffer, size_t bufferByteSize);
    ~BitStreamWriter();

    BitStreamWriter(const BitStreamWriter& other) = delete;
    BitStreamWriter& operator=(const BitStreamWriter& other) = delete;

    BitStreamWriter(const BitStreamWriter&& other) = delete;
    BitStreamWriter& operator=(BitStreamWriter&& other) = delete;

    void writeBits(uint32_t data, uint8_t numBits = 32);
    void writeBits64(uint64_t data, uint8_t numBits = 64);

    void writeSignedBits(int32_t data, uint8_t numBits = 32);
    void writeSignedBits64(int64_t data, uint8_t numBits = 64);

    void writeVarInt64(int64_t data);
    void writeVarInt32(int32_t data);
    void writeVarInt16(int16_t data);

    void writeVarUInt64(uint64_t data);
    void writeVarUInt32(uint32_t data);
    void writeVarUInt16(uint16_t data);

    void writeVarInt(int64_t data);
    void writeVarUInt(uint64_t data);

    void writeFloat16(float data);
    void writeFloat32(float data);
    void writeFloat64(double data);

    void writeString(const std::string& data);
    void writeBool(bool data);

    BitPosType getBitPosition() const { return m_bitIndex; }
    void setBitPosition(BitPosType pos);

    void alignTo(size_t alignment);

    bool hasWriteBuffer() const { return m_hasInternalBuffer || m_buffer != NULL; }
    const uint8_t* getWriteBuffer(size_t& writeBufferByteSize) const;

    void writeBufferToFile(const std::string& filename) const;

private:
    void writeUnsignedBits(uint32_t data, uint8_t numBits);
    void writeUnsignedBits64(uint64_t data, uint8_t numBits);
    void writeVarNum(int64_t value, const uint8_t* valBits, size_t valBitsSize, size_t numVarBits);
    void writeVarAbsNum(uint64_t value, bool sign, const uint8_t* valBits, size_t valBitsSize,
            size_t numVarBits);

    uint8_t* m_buffer;
    size_t m_bitIndex;
    size_t m_bufferBitSize;
    bool m_hasInternalBuffer;
    std::vector<uint8_t> m_internalBuffer;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_WRITER_H_INC
