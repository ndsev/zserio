#ifndef ZSERIO_HASH_CODE_UTIL_H_INC
#define ZSERIO_HASH_CODE_UTIL_H_INC

#include <string>

#include "Types.h"
#include "FloatUtil.h"

namespace zserio
{
    static const int HASH_PRIME_NUMBER = 37;
    static const int HASH_SEED = 23;

    inline int calcHashCodeFirstTerm(int seedValue)
    {
        return HASH_PRIME_NUMBER * seedValue;
    }

    inline int calcHashCode(int seedValue, bool value)
    {
        return calcHashCodeFirstTerm(seedValue) + (value ? 1 : 0);
    }

    inline int calcHashCode(int seedValue, uint8_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    inline int calcHashCode(int seedValue, uint16_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    inline int calcHashCode(int seedValue, uint32_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    inline int calcHashCode(int seedValue, uint64_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value ^ (value >> 32));
    }

    inline int calcHashCode(int seedValue, int8_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    inline int calcHashCode(int seedValue, int16_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    inline int calcHashCode(int seedValue, int32_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + value;
    }

    inline int calcHashCode(int seedValue, int64_t value)
    {
        return calcHashCode(seedValue, static_cast<uint64_t>(value));
    }

    inline int calcHashCode(int seedValue, float value)
    {
        return calcHashCode(seedValue, convertFloatToUInt32(value));
    }

    inline int calcHashCode(int seedValue, double value)
    {
        return calcHashCode(seedValue, convertDoubleToUInt64(value));
    }

    inline int calcHashCode(int seedValue, const std::string& value)
    {
        int result = seedValue;
        for(std::string::const_iterator it = value.begin(); it != value.end(); ++it)
        {
            result = calcHashCode(result, *it);
        }

        return result;
    }

    template <class OBJECT>
    inline int calcHashCode(int seedValue, const OBJECT& value)
    {
        return calcHashCode(seedValue, value.hashCode());
    }
} // namespace zserio

#endif // ZSERIO_HASH_CODE_UTIL_H_INC
