#include <utility>

#include "zserio/BitBuffer.h"

namespace zserio
{

BitBuffer::BitBuffer() : m_bitSize(0)
{
}

BitBuffer::BitBuffer(size_t bitSize) : m_internalBuffer((bitSize + 7) / 8 + 1), m_bitSize(bitSize)
{
}

BitBuffer::BitBuffer(std::vector<uint8_t>& buffer, size_t lastByteBits) :
        m_internalBuffer(buffer), m_bitSize((buffer.size() - 1) * 8 + lastByteBits)
{
}

BitBuffer::BitBuffer(std::vector<uint8_t>&& buffer, size_t lastByteBits) :
        m_internalBuffer(std::move(buffer)), m_bitSize((buffer.size() - 1) * 8 + lastByteBits)
{
}


BitBuffer::BitBuffer(uint8_t* buffer, size_t bitSize) :
        m_internalBuffer(buffer, buffer + (bitSize + 7) / 8 + 1), m_bitSize(bitSize)
{
}

BitBuffer::BitBuffer(uint8_t* buffer, size_t bitSize, InPlaceType) : m_externalBuffer(buffer),
        m_bitSize(bitSize)
{
}

const uint8_t* BitBuffer::get(size_t& bitSize) const
{
    bitSize = m_bitSize;

    return getBuffer();
}

uint8_t* BitBuffer::get(size_t& bitSize)
{
    bitSize = m_bitSize;

    return getBuffer();
}

const uint8_t* BitBuffer::getBuffer() const
{
    return (m_externalBuffer != nullptr) ? m_externalBuffer : &m_internalBuffer[0];
}

uint8_t* BitBuffer::getBuffer()
{
    return (m_externalBuffer != nullptr) ? m_externalBuffer : &m_internalBuffer[0];
}

size_t BitBuffer::getBitSize() const
{
    return m_bitSize;
}

} // namespace zserio
