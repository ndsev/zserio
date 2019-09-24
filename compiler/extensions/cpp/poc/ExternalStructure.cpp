#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>

#include <ExternalStructure.h>

ExternalStructure::ExternalStructure() noexcept :
        m_numberB_(uint8_t())
{
}

ExternalStructure::ExternalStructure(::zserio::BitStreamReader& in) :
        m_numberB_(readNumberB(in))
{
}

uint8_t ExternalStructure::getNumberB() const
{
    return m_numberB_;
}

void ExternalStructure::setNumberB(uint8_t numberB_)
{
    m_numberB_ = numberB_;
}

size_t ExternalStructure::bitSizeOf(size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(8);

    return endBitPosition - bitPosition;
}

size_t ExternalStructure::initializeOffsets(size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += UINT8_C(8);

    return endBitPosition;
}

bool ExternalStructure::operator==(const ExternalStructure& other) const
{
    if (this != &other)
    {
        return
                (m_numberB_ == other.m_numberB_);
    }

    return true;
}

int ExternalStructure::hashCode() const
{
    int result = ::zserio::HASH_SEED;

    result = ::zserio::calcHashCode(result, m_numberB_);

    return result;
}

void ExternalStructure::read(::zserio::BitStreamReader& in)
{
    m_numberB_ = readNumberB(in);
}

void ExternalStructure::write(::zserio::BitStreamWriter& out, ::zserio::PreWriteAction)
{
    out.writeBits(m_numberB_, UINT8_C(8));
}

uint8_t ExternalStructure::readNumberB(::zserio::BitStreamReader& in)
{
    return static_cast<uint8_t>(in.readBits(UINT8_C(8)));
}
