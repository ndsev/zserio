#ifndef ZSERIO_HASH_CODE_UTIL_H_INC
#define ZSERIO_HASH_CODE_UTIL_H_INC

#include <type_traits>
#include <string>
#include <vector>
#include <memory>

#include "zserio/Types.h"
#include "zserio/FloatUtil.h"
#include "zserio/Enums.h"
#include "zserio/OptionalHolder.h"

namespace zserio
{
    /** Prime number for hash calculation. */
    static const uint32_t HASH_PRIME_NUMBER = 37;
    /** Initial seed for hash calculation. */
    static const uint32_t HASH_SEED = 23;

    /**
     * Gets initial hash code calculated from the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     *
     * \return Hash code.
     */
    inline uint32_t calcHashCodeFirstTerm(uint32_t seedValue)
    {
        return HASH_PRIME_NUMBER * seedValue;
    }

    /**
     * Calculates hash code of the given integral value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template<typename T>
    inline typename std::enable_if<std::is_integral<T>::value && (sizeof(T) <= 4), uint32_t>::type
            calcHashCode(uint32_t seedValue, T value)
    {
        return calcHashCodeFirstTerm(seedValue) + static_cast<uint32_t>(value);
    }

    /**
     * Calculates hash code of the given integral value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template<typename T>
    inline typename std::enable_if<std::is_integral<T>::value && (sizeof(T) > 4), uint32_t>::type
            calcHashCode(uint32_t seedValue, T value)
    {
        const auto unsignedValue = static_cast<typename std::make_unsigned<T>::type>(value);
        return calcHashCodeFirstTerm(seedValue) + static_cast<uint32_t>(unsignedValue ^ (unsignedValue >> 32U));
    }

    /**
     * Calculates hash code of the given float value using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param value Value for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    inline uint32_t calcHashCode(uint32_t seedValue, float value)
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
    inline uint32_t calcHashCode(uint32_t seedValue, double value)
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
    template <typename ALLOC>
    inline uint32_t calcHashCode(uint32_t seedValue,
            const std::basic_string<char, std::char_traits<char>, ALLOC>& stringValue)
    {
        uint32_t result = seedValue;
        for (auto element : stringValue)
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
    inline typename std::enable_if<std::is_enum<ENUM_TYPE>::value, uint32_t>::type calcHashCode(
            uint32_t seedValue, ENUM_TYPE enumValue)
    {
        return calcHashCode(seedValue, enumHashCode(enumValue));
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
    inline
    typename std::enable_if<!std::is_enum<OBJECT>::value && !std::is_integral<OBJECT>::value, uint32_t>::type
    calcHashCode(uint32_t seedValue, const OBJECT& object)
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
    template <typename ARRAY_ELEMENT, typename ALLOC>
    inline uint32_t calcHashCode(uint32_t seedValue, const std::vector<ARRAY_ELEMENT, ALLOC>& array)
    {
        uint32_t result = seedValue;
        for (const ARRAY_ELEMENT& element : array)
            result = calcHashCode(result, element);

        return result;
    }

    // must be last because of the two-phase lookup
    // - we can have optional array (OptionalHolder<std::vector<T>>), but we cannot have array of optionals
    /**
     * Calculates hash code of the given Zserio optional field using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param optionalHolder Optional field for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename FIELD>
    inline uint32_t calcHashCode(uint32_t seedValue, const InplaceOptionalHolder<FIELD>& optionalHolder)
    {
        if (!optionalHolder)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, *optionalHolder);
    }

    /**
     * Calculates hash code of the given Zserio optional field using the given seed value.
     *
     * \param seedValue Seed value (current hash code).
     * \param optionalHolder Optional field for which to calculate the hash code.
     *
     * \return Calculated hash code.
     */
    template <typename FIELD, typename ALLOC>
    inline uint32_t calcHashCode(uint32_t seedValue, const HeapOptionalHolder<FIELD, ALLOC>& optionalHolder)
    {
        if (!optionalHolder)
            return calcHashCode(seedValue, 0);

        return calcHashCode(seedValue, *optionalHolder);
    }

} // namespace zserio

#endif // ZSERIO_HASH_CODE_UTIL_H_INC
