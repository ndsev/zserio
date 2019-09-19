#include <zserio/BitSizeOfCalculator.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/HashCodeUtil.h>

#include "Emotions.h"

namespace bitmask_types
{
namespace uint8_bitmask
{

const Emotions Emotions::SAD(0x01);
const Emotions Emotions::CHEERY(0x02);
const Emotions Emotions::UNHAPPY(0x04);
const Emotions Emotions::HAPPY(0x08);
const Emotions Emotions::SANE(0x10);
const Emotions Emotions::MAD(0x20);
const Emotions Emotions::ALIVE(0x40);
const Emotions Emotions::DEAD(0x80);

Emotions::Emotions() : m_value(0)
{
}

Emotions::Emotions(underlying_type value) : m_value(value)
{
}

Emotions::Emotions(zserio::BitStreamReader& _in)
{
    read(_in);
}

Emotions::operator underlying_type() const
{
    return m_value;
}

uint8_t Emotions::getValue() const
{
    return m_value;
}

size_t Emotions::bitSizeOf(size_t) const
{
    return UINT8_C(8);
}

size_t Emotions::initializeOffsets(size_t _bitPosition) const
{
    return _bitPosition + bitSizeOf(_bitPosition);
}

bool Emotions::operator==(const Emotions& other) const
{
    return m_value == other.m_value;
}

bool Emotions::operator==(underlying_type other) const
{
    return m_value == other;
}

int Emotions::hashCode() const
{
    return zserio::calcHashCode(zserio::HASH_SEED, m_value);
}

void Emotions::read(zserio::BitStreamReader& _in)
{
    m_value = _in.readBits(UINT8_C(8));
}

void Emotions::write(zserio::BitStreamWriter& _out, zserio::PreWriteAction) const
{
    _out.writeBits(m_value, UINT8_C(8));
}

// TODO[MIK] Think about template traits of values and strings to allow application generic impl

std::string Emotions::toString() const
{
    std::string result("");
    if ( (*this & Emotions::SAD) == Emotions::SAD )
        result += "SAD";
    if ( (*this & Emotions::CHEERY) == Emotions::CHEERY )
        result += (result.empty()) ? "CHEERY" : " | CHEERY";
    if ( (*this & Emotions::UNHAPPY) == Emotions::UNHAPPY )
        result += (result.empty()) ? "UNHAPPY" : " | UNHAPPY";
    if ( (*this & Emotions::HAPPY) == Emotions::HAPPY )
        result += (result.empty()) ? "HAPPY" : " | HAPPY";
    if ( (*this & Emotions::SANE) == Emotions::SANE )
        result += (result.empty()) ? "SANE" : " | SANE";
    if ( (*this & Emotions::MAD) == Emotions::MAD )
        result += (result.empty()) ? "MAD" : " | MAD";
    if ( (*this & Emotions::ALIVE) == Emotions::ALIVE )
        result += (result.empty()) ? "ALIVE" : " | ALIVE";
    if ( (*this & Emotions::DEAD) == Emotions::DEAD )
        result += (result.empty()) ? "DEAD" : " | DEAD";

    return result.empty() ? "NONE" : result;
}

} // namespace uint8_bitmask

} // namespace bitmask_types
