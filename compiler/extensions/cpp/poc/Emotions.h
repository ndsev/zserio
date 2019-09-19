#ifndef BITMASK_TYPES_UINT8_BITMASK_EMOTIONS_H
#define BITMASK_TYPES_UINT8_BITMASK_EMOTIONS_H

#include <string>
#include <iostream>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
#include <zserio/Types.h>

namespace bitmask_types
{

namespace uint8_bitmask
{

class Emotions
{
public:
    typedef uint8_t underlying_type;

    static const Emotions SAD;
    static const Emotions CHEERY;
    static const Emotions UNHAPPY;
    static const Emotions HAPPY;
    static const Emotions SANE;
    static const Emotions MAD;
    static const Emotions ALIVE;
    static const Emotions DEAD;

    Emotions();
    explicit Emotions(underlying_type value);
    explicit Emotions(zserio::BitStreamReader& _in);

    explicit operator underlying_type() const;
    underlying_type getValue() const;

    size_t bitSizeOf(size_t _bitPosition = 0) const;
    size_t initializeOffsets(size_t _bitPosition = 0) const;

    bool operator==(const Emotions& other) const;
    bool operator==(underlying_type other) const;
    int hashCode() const;

    void read(zserio::BitStreamReader& _in);
    void write(zserio::BitStreamWriter& _out,
            zserio::PreWriteAction _preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS) const;

    std::string toString() const;

private:
    underlying_type m_value;
};

inline Emotions operator|(const Emotions& lhs, const Emotions& rhs)
{
    return Emotions(lhs.getValue() | rhs.getValue());
}

inline Emotions operator&(const Emotions& lhs, const Emotions& rhs)
{
    return Emotions(lhs.getValue() & rhs.getValue());
}

inline Emotions operator^(const Emotions& lhs, const Emotions& rhs)
{
    return Emotions(lhs.getValue() ^ rhs.getValue());
}

inline Emotions operator~(const Emotions& lhs)
{
    return Emotions(~lhs.getValue());
}

inline Emotions operator|=(Emotions& lhs, const Emotions& rhs)
{
    lhs = Emotions(lhs.getValue() | rhs.getValue());

    return lhs;
}

inline Emotions operator&=(Emotions& lhs, const Emotions& rhs)
{
    lhs = Emotions(lhs.getValue() & rhs.getValue());

    return lhs;
}

inline Emotions operator^=(Emotions& lhs, const Emotions& rhs)
{
    lhs = Emotions(lhs.getValue() ^ rhs.getValue());

    return lhs;
}

} // namespace uint8_bitmask

} // namespace bitmask_types

#endif // BITMASK_TYPES_UINT8_BITMASK_EMOTIONS_H
