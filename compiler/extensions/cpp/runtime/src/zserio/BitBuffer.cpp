#include <utility>
#include <cstring>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/HashCodeUtil.h"
#include "zserio/BitBuffer.h"

namespace zserio
{

BitBuffer::BitBuffer() : m_bitSize(0)
{
}

BitBuffer::BitBuffer(size_t bitSize) : m_buffer((bitSize + 7) / 8), m_bitSize(bitSize)
{
}

BitBuffer::BitBuffer(const std::vector<uint8_t>& buffer) :
        m_buffer(buffer), m_bitSize(8 * buffer.size())
{
}

BitBuffer::BitBuffer(const std::vector<uint8_t>& buffer, size_t bitSize) :
        m_bitSize(bitSize)
{
    const size_t byteSize = (bitSize + 7) / 8;
    if (buffer.size() < byteSize)
        throw CppRuntimeException("BitBuffer: Bit size " + convertToString(bitSize) +
                " out of range for given vector byte size " + convertToString(buffer.size()) + "!");

    m_buffer.assign(buffer.data(), buffer.data() + byteSize);
}

BitBuffer::BitBuffer(std::vector<uint8_t>&& buffer) :
        m_buffer(std::move(buffer)), m_bitSize(8 * m_buffer.size())
{
}

BitBuffer::BitBuffer(std::vector<uint8_t>&& buffer, size_t bitSize) :
        m_buffer(std::move(buffer)), m_bitSize(bitSize)
{
    const size_t byteSize = (bitSize + 7) / 8;
    if (m_buffer.size() < byteSize)
        throw CppRuntimeException("BitBuffer: Bit size " + convertToString(bitSize) +
                " out of range for given vector byte size " + convertToString(buffer.size()) + "!");
}

BitBuffer::BitBuffer(const uint8_t* buffer, size_t bitSize) :
        m_buffer(buffer, buffer + (bitSize + 7) / 8), m_bitSize(bitSize)
{
}

bool BitBuffer::operator==(const BitBuffer& other) const
{
    if (this != &other)
    {
        if (m_bitSize != other.m_bitSize)
            return false;

        const size_t byteSize = getByteSize();
        if (byteSize > 0)
        {
            if (byteSize > 1)
            {
                if (memcmp(getBuffer(), other.getBuffer(), byteSize - 1) != 0)
                    return false;
            }

            if (getMaskedLastByte() != other.getMaskedLastByte())
                return false;
        }
    }

    return true;
}

int BitBuffer::hashCode() const
{
    int result = ::zserio::HASH_SEED;
    const size_t byteSize = getByteSize();
    if (byteSize > 0)
    {
        if (byteSize > 1)
        {
            const uint8_t* lastElement = getBuffer() + byteSize - 1;
            for (const uint8_t* p = getBuffer(); p < lastElement; p++)
                result = calcHashCode(result, *p);
        }
        result = ::zserio::calcHashCode(result, getMaskedLastByte());
    }

    return result;
}

const uint8_t* BitBuffer::getBuffer() const
{
    return m_buffer.data();
}

uint8_t* BitBuffer::getBuffer()
{
    return m_buffer.data();
}

size_t BitBuffer::getBitSize() const
{
    return m_bitSize;
}

size_t BitBuffer::getByteSize() const
{
    return (m_bitSize + 7) / 8;
}

uint8_t BitBuffer::getMaskedLastByte() const
{
    const size_t roundedByteSize = m_bitSize / 8;
    const uint8_t lastByteBits = static_cast<uint8_t>(m_bitSize - 8 * roundedByteSize);

    return (lastByteBits == 0) ? m_buffer[roundedByteSize - 1] :
            (m_buffer[roundedByteSize] & (0xFF << (8 - lastByteBits)));
}

} // namespace zserio
