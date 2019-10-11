#ifndef ZSERIO_BIT_STREAM_BUFFER_H_INC
#define ZSERIO_BIT_STREAM_BUFFER_H_INC

#include <vector>

#include "zserio/Types.h"

namespace zserio
{

class BitStreamBuffer
{
public:
    BitStreamBuffer();
    BitStreamBuffer(size_t bitOffset);
    BitStreamBuffer(size_t bitOffset, size_t bitSize);
    BitStreamBuffer(const uint8_t* buffer, size_t bufferBitOffset, size_t bufferBitSize);

    ~BitStreamBuffer() = default;

    BitStreamBuffer(const BitStreamBuffer&) = default;
    BitStreamBuffer& operator=(const BitStreamBuffer&) = default;

    BitStreamBuffer(BitStreamBuffer&&) = default;
    BitStreamBuffer& operator=(BitStreamBuffer&&) = default;

    void setBuffer(const uint8_t* buffer, size_t bufferBitOffset, size_t bufferBitSize);
    const uint8_t* getBuffer(size_t& bufferBitOffset, size_t& bufferBitSize) const;

private:
    std::vector<uint8_t> m_buffer;
    size_t m_bufferBitOffset = 0;
    size_t m_bufferBitSize = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_BUFFER_H_INC
