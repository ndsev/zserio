#include <zserio/BitSizeOfCalculator.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/HashCodeUtil.h>

#include "Emotions.h"

namespace bitmask_types
{
namespace uint8_bitmask
{

const Emotions Emotions::Values::SAD = Emotions(0x01);
const Emotions Emotions::Values::CHEERY = Emotions(0x02);
const Emotions Emotions::Values::UNHAPPY = Emotions(0x04);
const Emotions Emotions::Values::HAPPY = Emotions(0x08);
const Emotions Emotions::Values::SANE = Emotions(0x10);
const Emotions Emotions::Values::MAD = Emotions(0x20);
const Emotions Emotions::Values::ALIVE = Emotions(0x40);
const Emotions Emotions::Values::DEAD = Emotions(0x80);

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
    if ( (*this & Emotions::Values::SAD) == Emotions::Values::SAD )
        result += "SAD";
    if ( (*this & Emotions::Values::CHEERY) == Emotions::Values::CHEERY )
        result += (result.empty()) ? "CHEERY" : " | CHEERY";
    if ( (*this & Emotions::Values::UNHAPPY) == Emotions::Values::UNHAPPY )
        result += (result.empty()) ? "UNHAPPY" : " | UNHAPPY";
    if ( (*this & Emotions::Values::HAPPY) == Emotions::Values::HAPPY )
        result += (result.empty()) ? "HAPPY" : " | HAPPY";
    if ( (*this & Emotions::Values::SANE) == Emotions::Values::SANE )
        result += (result.empty()) ? "SANE" : " | SANE";
    if ( (*this & Emotions::Values::MAD) == Emotions::Values::MAD )
        result += (result.empty()) ? "MAD" : " | MAD";
    if ( (*this & Emotions::Values::ALIVE) == Emotions::Values::ALIVE )
        result += (result.empty()) ? "ALIVE" : " | ALIVE";
    if ( (*this & Emotions::Values::DEAD) == Emotions::Values::DEAD )
        result += (result.empty()) ? "DEAD" : " | DEAD";

    return result.empty() ? "NONE" : result;
}

} // namespace uint8_bitmask

} // namespace bitmask_types
