#ifndef ZSERIO_HASH_CODE_UTIL_H_INC
#define ZSERIO_HASH_CODE_UTIL_H_INC

#include <type_traits>
#include <string>
#include <vector>

#include "zserio/Types.h"
#include "zserio/FloatUtil.h"
#include "zserio/Arrays.h"
#include "zserio/Enums.h"
#include "zserio/OptionalHolder.h"

namespace zserio
{
    static const int HASH_PRIME_NUMBER = 37; /**< Primer number for hash calculation. */
    static const int HASH_SEED = 23; /** Initial seed for hash calculation. */

    /**
     * Gets initial hash code calculated from the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     *
     * \return Hash code.
     */
    inline int calcHashCodeFirstTerm(int seedValue)
    {
        return HASH_PRIME_NUMBER * seedValue;
    }

    /**
     * Calculates hash code of the given bool value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, bool value)
    {
        return calcHashCodeFirstTerm(seedValue) + (value ? 1 : 0);
    }

    /**
     * Calculates hash code of the given uint8_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, uint8_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    /**
     * Calculates hash code of the given uint16_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, uint16_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    /**
     * Calculates hash code of the given uint32_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, uint32_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    /**
     * Calculates hash code of the given uint64_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, uint64_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value ^ (value >> 32));
    }

    /**
     * Calculates hash code of the given int8_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, int8_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    /**
     * Calculates hash code of the given int16_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, int16_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<int>(value);
    }

    /**
     * Calculates hash code of the given int32_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, int32_t value)
    {
        return calcHashCodeFirstTerm(seedValue) + value;
    }

    /**
     * Calculates hash code of the given int64_t value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, int64_t value)
    {
        return calcHashCode(seedValue, static_cast<uint64_t>(value));
    }

    /**
     * Calculates hash code of the given float value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, float value)
    {
        return calcHashCode(seedValue, convertFloatToUInt32(value));
    }

    /**
     * Calculates hash code of the given double value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, double value)
    {
        return calcHashCode(seedValue, convertDoubleToUInt64(value));
    }

    /**
     * Calculates hash code of the given string value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param stringValue Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline int calcHashCode(int seedValue, const std::string& stringValue)
    {
        int result = seedValue;
        for (std::string::value_type element : stringValue)
            result = calcHashCode(result, element);

        return result;
    }

    /**
     * Calculates hash code of the given enum item using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param enumValue Enum item for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename ENUM_TYPE>
    inline typename std::enable_if<std::is_enum<ENUM_TYPE>::value, int>::type calcHashCode(int seedValue,
            ENUM_TYPE enumValue)
    {
        return calcHashCode(seedValue, enumToValue(enumValue));
    }

    /**
     * Calculates hash code of the given Zserio object (structure, choice, ...) using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param object Object for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename OBJECT>
    inline typename std::enable_if<!std::is_enum<OBJECT>::value, int>::type calcHashCode(int seedValue,
            const OBJECT& object)
    {
        return calcHashCode(seedValue, object.hashCode());
    }

    /**
     * Calculates hash code of the given Zserio array using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param array Array for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename ARRAY_ELEMENT>
    inline int calcHashCode(int seedValue, const std::vector<ARRAY_ELEMENT>& array)
    {
        int result = seedValue;
        for (const ARRAY_ELEMENT& element : array)
            result = calcHashCode(result, element);

        return result;
    }

    // must be last because of the two-phase lookup
    // - we can have optional array (OptionaHolder<std::vector<T>>), but we cannot have array of optionals
    /**
     * Calculates hash code of the given Zserio optional field using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param optionalHolder Optional field for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename FIELD>
    inline int calcHashCode(int seedValue, const OptionalHolder<FIELD>& optionalHolder)
    {
        if (!optionalHolder)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, *optionalHolder);
    }
} // namespace zserio

#endif // ZSERIO_HASH_CODE_UTIL_H_INC
