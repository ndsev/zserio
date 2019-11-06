#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>

#include <SimpleStructure.h>

SimpleStructure::SimpleStructure() noexcept :
        m_numberA_(uint8_t()),
        m_numberC_(uint8_t())
{
}

uint8_t SimpleStructure::getNumberA() const
{
    return m_numberA_;
}

void SimpleStructure::setNumberA(uint8_t numberA_)
{
    m_numberA_ = numberA_;
}

::zserio::BitBuffer& SimpleStructure::getExternalStructure()
{
    return m_externalStructure_;
}

const ::zserio::BitBuffer& SimpleStructure::getExternalStructure() const
{
    return m_externalStructure_;
}

void SimpleStructure::setExternalStructure(const ::zserio::BitBuffer& externalStructure_)
{
    m_externalStructure_ = externalStructure_;
}

void SimpleStructure::setExternalStructure(::zserio::BitBuffer&& externalStructure_)
{
    m_externalStructure_ = ::std::move(externalStructure_);
}

uint8_t SimpleStructure::getNumberC() const
{
    return m_numberC_;
}

void SimpleStructure::setNumberC(uint8_t numberC_)
{
    m_numberC_ = numberC_;
}

size_t SimpleStructure::bitSizeOf(size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(3);
    endBitPosition += ::zserio::bitSizeOfBitBuffer(m_externalStructure_);
    endBitPosition += UINT8_C(7);

    return endBitPosition - bitPosition;
}

size_t SimpleStructure::initializeOffsets(size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(3);
    endBitPosition += ::zserio::bitSizeOfBitBuffer(m_externalStructure_);
    endBitPosition += UINT8_C(7);

    return endBitPosition;
}

bool SimpleStructure::operator==(const SimpleStructure& other) const
{
    if (this != &other)
    {
        return
                (m_numberA_ == other.m_numberA_) &&
                (m_externalStructure_ == other.m_externalStructure_) &&
                (m_numberC_ == other.m_numberC_);
    }

    return true;
}

int SimpleStructure::hashCode() const
{
    int result = ::zserio::HASH_SEED;

    result = ::zserio::calcHashCode(result, m_numberA_);
    result = ::zserio::calcHashCode(result, m_externalStructure_);
    result = ::zserio::calcHashCode(result, m_numberC_);

    return result;
}

void SimpleStructure::read(::zserio::BitStreamReader& in)
{
    m_numberA_ = readNumberA(in);
    m_externalStructure_ = readExternalStructure(in);
    m_numberC_ = readNumberC(in);
}

void SimpleStructure::write(::zserio::BitStreamWriter& out, ::zserio::PreWriteAction)
{
    out.writeBits(m_numberA_, 3);
    out.writeBitBuffer(m_externalStructure_);
    out.writeBits(m_numberC_, 7);
}

uint8_t SimpleStructure::readNumberA(::zserio::BitStreamReader& in)
{
    return static_cast<uint8_t>(in.readBits(3));
}

::zserio::BitBuffer SimpleStructure::readExternalStructure(::zserio::BitStreamReader& in)
{
    return in.readBitBuffer();
}

uint8_t SimpleStructure::readNumberC(::zserio::BitStreamReader& in)
{
    return static_cast<uint8_t>(in.readBits(7));
}
