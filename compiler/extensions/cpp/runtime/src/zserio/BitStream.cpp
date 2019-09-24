#include "zserio/BitStream.h"
#include "zserio/VarUInt64Util.h" // TODO[mikir] size_t is not declared here if it is the first one

namespace zserio
{

BitStream::BitStream()
{
}

BitStream::BitStream(const uint8_t* buffer, size_t bufferBitSize)
{
    setBuffer(buffer, bufferBitSize);
}

void BitStream::setBuffer(const uint8_t* buffer, size_t bufferBitSize)
{
    m_buffer.assign(buffer, buffer + (bufferBitSize + 1) / 8);
    m_bufferBitSize = bufferBitSize;
}

const uint8_t* BitStream::getBuffer(size_t& bufferBitSize) const
{
    bufferBitSize = m_bufferBitSize;

    return &m_buffer[0];
}

const std::function<size_t(size_t)>& BitStream::getBitSizeOfFunc() const
{
    return m_bitSizeOfFunc;
}

const std::function<int()>& BitStream::getHashCodeFunc() const
{
    return m_hashCodeFunc;
}

const std::function<void(BitStreamReader&)>& BitStream::getReadFunc() const
{
    return m_readFunc;
}

const std::function<void(BitStreamWriter&, PreWriteAction)>& BitStream::getWriteFunc() const
{
    return m_writeFunc;
}

size_t bitSizeOf(const BitStream& bitStream, size_t) // bitPosition is always ignored
{
    const std::function<size_t(size_t)>& bitSizeOfFunc = bitStream.getBitSizeOfFunc();
    if (bitSizeOfFunc)
        return bitSizeOfFunc(0);

    size_t bitStreamSize;
    bitStream.getBuffer(bitStreamSize);

    return bitStreamSize;
}

void read(BitStream& bitStream, BitStreamReader& in)
{
    // TODO[mikir]
    const size_t bitStreamSize = zserio::convertVarUInt64ToArraySize(in.readVarUInt64());
}

void write(const BitStream& bitStream, BitStreamWriter& out)
{
    // TODO[mikir]
}

} // namespace zserio
