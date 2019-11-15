#include <utility>
#include <cstring>

#include "zserio/BitBuffer.h"

namespace zserio
{

BitBuffer::BitBuffer() : m_bitSize(0)
{
}

BitBuffer::BitBuffer(size_t bitSize) : m_buffer((bitSize + 7) / 8), m_bitSize(bitSize)
{
}

BitBuffer::BitBuffer(const std::vector<uint8_t>& buffer, size_t lastByteBits) :
        BitBuffer(&buffer[0], (buffer.size() - 1) * 8 + ((lastByteBits <= 8) ? lastByteBits : 8))
{
}

BitBuffer::BitBuffer(std::vector<uint8_t>&& buffer, size_t lastByteBits) :
        m_buffer(std::move(buffer)),
        m_bitSize((m_buffer.size() - 1) * 8 + ((lastByteBits <= 8) ? lastByteBits : 8))
{
    maskLastByte();
}

BitBuffer::BitBuffer(const uint8_t* buffer, size_t bitSize) :
        m_buffer(buffer, buffer + (bitSize + 7) / 8), m_bitSize(bitSize)
{
    maskLastByte();
}

bool BitBuffer::operator==(const BitBuffer& other) const
{
    if (this != &other)
    {
        if (getBitSize() != other.getBitSize())
            return false;

        if (memcmp(getBuffer(), other.getBuffer(), getByteSize()) != 0)
            return false;
    }

    return true;
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
    return &m_buffer[0];
}

uint8_t* BitBuffer::getBuffer()
{
    return &m_buffer[0];
}

size_t BitBuffer::getBitSize() const
{
    return m_bitSize;
}

size_t BitBuffer::getByteSize() const
{
    return (m_bitSize + 7) / 8;
}

// Please be aware the the following logic must be changed if the in-place constructor is introduced.
void BitBuffer::maskLastByte()
{
    const size_t roundedByteSize = m_bitSize / 8;
    const size_t lastByteBits = m_bitSize - 8 * roundedByteSize;
    if (lastByteBits > 0)
    {
        const uint8_t lastByteMask = 0xFF >> (8 - lastByteBits);
        m_buffer[roundedByteSize] &= lastByteMask;
    }
}

} // namespace zserio
