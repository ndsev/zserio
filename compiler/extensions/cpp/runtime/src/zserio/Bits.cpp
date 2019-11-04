#include <utility>
#include <cstring>

#include "zserio/VarUInt64Util.h"
#include "zserio/Bits.h"

namespace zserio
{

Bits::Bits()
{
}

Bits::Bits(const BitBuffer& bitBuffer) : m_bitBuffer(bitBuffer)
{
}

Bits::Bits(BitBuffer&& bitBuffer) : m_bitBuffer(std::move(bitBuffer))
{
}

bool Bits::operator==(const Bits& other) const
{
    if (this != &other)
    {
        if (getFuncAddress(m_bitSizeOfFunc) != getFuncAddress(other.m_bitSizeOfFunc) ||
                getFuncAddress(m_hashCodeFunc) != getFuncAddress(other.m_hashCodeFunc) ||
                getFuncAddress(m_readFunc) != getFuncAddress(other.m_readFunc) ||
                getFuncAddress(m_writeFunc) != getFuncAddress(other.m_writeFunc))
            return false;

        const size_t bitSize = m_bitBuffer.getBitSize();
        if (bitSize != other.m_bitBuffer.getBitSize())
            return false;

        if (memcmp(m_bitBuffer.getBuffer(), other.m_bitBuffer.getBuffer(), bitSize) != 0)
            return false;
    }

    return true;
}

void Bits::setBitBuffer(const BitBuffer& bitBuffer)
{
    resetBinding();
    m_bitBuffer = bitBuffer;
}

void Bits::setBitBuffer(BitBuffer&& bitBuffer)
{
    resetBinding();
    m_bitBuffer = std::move(bitBuffer);
}

const BitBuffer& Bits::getBitBuffer() const
{
    return m_bitBuffer;
}

const std::function<size_t(size_t)>& Bits::getBitSizeOfFunc() const
{
    return m_bitSizeOfFunc;
}

const std::function<int()>& Bits::getHashCodeFunc() const
{
    return m_hashCodeFunc;
}

const std::function<void(BitStreamReader&)>& Bits::getReadFunc() const
{
    return m_readFunc;
}

const std::function<void(BitStreamWriter&, PreWriteAction)>& Bits::getWriteFunc() const
{
    return m_writeFunc;
}

void Bits::resetBinding()
{
    m_bitSizeOfFunc = nullptr;
    m_hashCodeFunc = nullptr;
    m_readFunc = nullptr;
    m_writeFunc = nullptr;
}

size_t bitSizeOf(const Bits& bits, size_t) // bitPosition is always ignored
{
    const std::function<size_t(size_t)>& bitSizeOfFunc = bits.getBitSizeOfFunc();
    if (bitSizeOfFunc)
        return bitSizeOfFunc(0);

    return bits.getBitBuffer().getBitSize();
}

void read(Bits& bits, BitStreamReader& in)
{
    const size_t bitSize = zserio::convertVarUInt64ToArraySize(in.readVarUInt64());
    const std::function<void(BitStreamReader&)>& readFunc = bits.getReadFunc();
    if (readFunc)
    {
        const bool isReaderByteAligned = ((in.getBitPosition() & 0x07) == 0);
        const BitBuffer& bitBuffer = (isReaderByteAligned) ? in.readBitBufferInPlace(bitSize) :
                in.readBitBuffer(bitSize);
        BitStreamReader innerReader(bitBuffer);
        readFunc(innerReader);
    }
    else
    {
        bits.setBitBuffer(in.readBitBuffer(bitSize));
    }
}

void write(const Bits& bits, BitStreamWriter& out)
{
    const std::function<size_t(size_t)>& bitSizeOfFunc = bits.getBitSizeOfFunc();
    const std::function<void(BitStreamWriter&, PreWriteAction)>& writeFunc = bits.getWriteFunc();
    if (bitSizeOfFunc && writeFunc)
    {
        const size_t bitSize = bitSizeOfFunc(0);
        out.writeVarUInt64(static_cast<uint64_t>(bitSize));

        const bool isWriterByteAligned = ((out.getBitPosition() & 0x07) == 0);
        if (isWriterByteAligned)
        {
            BitBuffer bitBuffer = out.reserveBitBufferInPlace(bitSize);
            BitStreamWriter innerWriter(bitBuffer);
            writeFunc(innerWriter, NO_PRE_WRITE_ACTION);
        }
        else
        {
            BitBuffer bitBuffer(bitSize);
            BitStreamWriter innerWriter(bitBuffer);
            writeFunc(innerWriter, NO_PRE_WRITE_ACTION);
            out.writeBitBuffer(bitBuffer);
        }
    }
    else
    {
        const BitBuffer& bitBuffer = bits.getBitBuffer();
        out.writeVarUInt64(static_cast<uint64_t>(bitBuffer.getBitSize()));
        out.writeBitBuffer(bitBuffer);
    }
}

} // namespace zserio
