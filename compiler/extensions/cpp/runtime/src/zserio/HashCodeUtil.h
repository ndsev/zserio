#ifndef ZSERIO_HASH_CODE_UTIL_H_INC
#define ZSERIO_HASH_CODE_UTIL_H_INC

#include <type_traits>
#include <string>
#include <vector>

#include "Types.h"
#include "FloatUtil.h"
#include "Arrays.h"
#include "Enums.h"

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

    inline int calcHashCode(int seedValue, const std::string& stringValue)
    {
        int result = seedValue;
        for (std::string::value_type element : stringValue)
            result = calcHashCode(result, element);

        return result;
    }

    template <typename ENUM_TYPE>
    inline typename std::enable_if<std::is_enum<ENUM_TYPE>::value, int>::type calcHashCode(int seedValue,
            ENUM_TYPE enumValue)
    {
        return calcHashCode(seedValue, enumToValue(enumValue));
    }

    template <typename OBJECT>
    inline typename std::enable_if<!std::is_enum<OBJECT>::value, int>::type calcHashCode(int seedValue,
            const OBJECT& object)
    {
        return calcHashCode(seedValue, object.hashCode());
    }

    template <typename ARRAY_ELEMENT>
    inline int calcHashCode(int seedValue, const std::vector<ARRAY_ELEMENT>& array)
    {
        int result = seedValue;
        for (const ARRAY_ELEMENT& element : array)
            result = calcHashCode(result, element);

        return result;
    }
} // namespace zserio

#endif // ZSERIO_HASH_CODE_UTIL_H_INC
