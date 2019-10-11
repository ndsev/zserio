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
    const size_t bitStreamSize = zserio::convertVarUInt64ToArraySize(in.readVarUInt64());
    const std::function<void(BitStreamReader&)>& readFunc = bitStream.getReadFunc();
    if (readFunc)
    {
        const bool isReaderByteAligned = ((in.getBitPosition() & 0x07) == 0);
        BitStreamBuffer bitStreamBuffer = (isReaderByteAligned) ? in.readEmplaceBuffer(bitStreamSize) :
                in.readBuffer(bitStreamSize);
        BitStreamReader innerReader(bitStreamBuffer);
        bitStream.readFunc(innerReader);
    }
    else
    {
        BitStreamBuffer bitStreamBuffer = in.readBuffer(bitSize);
        bitStream.setBuffer(bitStreamBuffer); // TODO
    }
}

void write(const BitStream& bitStream, BitStreamWriter& out)
{
    const std::function<size_t(size_t)>& bitSizeOfFunc = bitStream.getBitSizeOfFunc();
    const std::function<void(BitStreamWriter&, PreWriteAction)>& writeFunc = bitStream.getWriteFunc();
    if (bitSizeOfFunc && writeFunc)
    {
        const size_t bitStreamSize = bitStream.bitSizeOfFunc(0);
        out.writeVarUInt64(static_cast<uint64_t>(bitStreamSize));

        const bool isWriterByteAligned = ((out.getBitPosition() & 0x07) == 0);
        BitStreamBuffer bitStreamBuffer = (isWriterByteAligned) ? out.getInnerBuffer(bitStreamSize) :
        if (isWriterByteAligned)
        {
            BitStreamBuffer bitStreamBuffer = out.reserveBuffer(bitStreamSize);
            BitStreamWriter innerWriter(bitStreamBuffer);
            bitStream.writeFunc(innerWriter, NO_PRE_WRITE_ACTION);
        }
        else
        {
            BitStreamBuffer bitStreamBuffer(bitStreamSize);
            BitStreamWriter innerWriter(bitStreamBuffer);
            bitStream.writeFunc(innerWriter, NO_PRE_WRITE_ACTION);
            out.writeBuffer(innerWriter.getBuffer());
        }
    }
    else
    {
        BitStreamBuffer bitStreamBuffer = bitStream.getBuffer();
        out.writeVarUInt64(static_cast<uint64_t>(bitStreamBuffer.getBitSize());
        out.writeBuffer(bitStreamBuffer);
    }
}

} // namespace zserio
