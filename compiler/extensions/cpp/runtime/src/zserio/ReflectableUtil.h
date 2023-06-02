#ifndef ZSERIO_REFLECTABLE_UTIL_H_INC
#define ZSERIO_REFLECTABLE_UTIL_H_INC

#include <algorithm>
#include <functional>
#include <cmath>
#include <limits>

#include "zserio/CppRuntimeException.h"
#include "zserio/IReflectable.h"
#include "zserio/ITypeInfo.h"
#include "zserio/StringView.h"
#include "zserio/Traits.h"
#include "zserio/TypeInfoUtil.h"

namespace zserio
{

namespace detail
{

template <typename T>
struct gets_value_by_value : std::integral_constant<bool,
        std::is_arithmetic<T>::value ||
        std::is_same<StringView, T>::value ||
        std::is_enum<T>::value ||
        is_bitmask<T>::value>
{};

} // namespace detail

/**
 * Utilities on zserio reflectable interface.
 */
class ReflectableUtil
{
public:
    /**
     * Makes "deep" comparison of given reflectables.
     *
     * \note Floating point values are compared using "almost equal" strategy.
     *
     * \param lhs Left-hand side reflectable.
     * \param rhs Right-hand side reflectable.
     *
     * \return True when the reflectables are equal, false otherwise.
     */
    template <typename ALLOC = std::allocator<uint8_t>>
    static bool equal(const IBasicReflectableConstPtr<ALLOC>& lhs,
            const IBasicReflectableConstPtr<ALLOC>& rhs);

    /**
     * Gets native value from the given reflectable.
     *
     * Overload for types where the value is returned by value:
     *
     * - arithmetic types, enums, bitmasks and strings (via string view).
     *
     * \param reflectable Reflectable to use for value extraction.
     *
     * \return Value of the type T.
     */
    template <typename T, typename ALLOC = std::allocator<uint8_t>,
            typename std::enable_if<detail::gets_value_by_value<T>::value, int>::type = 0>
    static T getValue(const IBasicReflectableConstPtr<ALLOC>& reflectable, const ALLOC& allocator = ALLOC())
    {
        return reflectable->getAnyValue(allocator).template get<T>();
    }

    /**
     * Gets constant reference to the native value from the given constant reflectable.
     *
     * Overload for types where the value is returned by const reference:
     *
     * - compound, bit buffers and arrays.
     *
     * \param reflectable Constant reflectable to use for value extraction.
     *
     * \return Constant reference to the value of the type T.
     *
     * \throw CppRuntimeException When wrong type is requested ("Bad type in AnyHolder").
     */
    template <typename T, typename ALLOC = std::allocator<uint8_t>,
            typename std::enable_if<!detail::gets_value_by_value<T>::value, int>::type = 0>
    static const T& getValue(const IBasicReflectableConstPtr<ALLOC>& reflectable,
            const ALLOC& allocator = ALLOC())
    {
        return reflectable->getAnyValue(allocator).template get<std::reference_wrapper<const T>>().get();
    }

    /**
     * Gets reference to the native value from the given reflectable.
     *
     * Overload for types where the value is returned by reference:
     *
     * - compound, arrays.
     *
     * \param reflectable Reflectable to use for value extraction.
     *
     * \return Reference to the value of the type T.
     *
     * \throw CppRuntimeException When wrong type is requested ("Bad type in AnyHolder").
     */
    template <typename T, typename ALLOC = std::allocator<uint8_t>,
            typename std::enable_if<
                    !detail::gets_value_by_value<T>::value &&
                    !std::is_same<BasicBitBuffer<ALLOC>, T>::value, int>::type = 0>
    static T& getValue(const IBasicReflectablePtr<ALLOC>& reflectable, const ALLOC& allocator = ALLOC())
    {
        return reflectable->getAnyValue(allocator).template get<std::reference_wrapper<T>>().get();
    }

    /**
     * Gets constant reference to the native value from the given reflectable.
     *
     * Overload for bit buffers which are currently returned only by constant reference.
     *
     * \param reflectable Reflectable to use for value extraction.
     *
     * \return Constant reference to the bit buffer value.
     *
     * \throw CppRuntimeException When wrong type is requested ("Bad type in AnyHolder").
     */
    template <typename T, typename ALLOC = std::allocator<uint8_t>,
            typename std::enable_if<std::is_same<BasicBitBuffer<ALLOC>, T>::value, int>::type = 0>
    static const T& getValue(const IBasicReflectablePtr<ALLOC>& reflectable, const ALLOC& allocator = ALLOC())
    {
        return reflectable->getAnyValue(allocator).template get<std::reference_wrapper<const T>>().get();
    }

private:
    template <typename ALLOC>
    static bool arraysEqual(const IBasicReflectableConstPtr<ALLOC>& lhsArray,
            const IBasicReflectableConstPtr<ALLOC>& rhsArray);

    template <typename ALLOC>
    static bool compoundsEqual(const IBasicReflectableConstPtr<ALLOC>& lhsCompound,
            const IBasicReflectableConstPtr<ALLOC>& rhsCompound);

    template <typename ALLOC>
    static bool valuesEqual(const IBasicReflectableConstPtr<ALLOC>& lhsValue,
            const IBasicReflectableConstPtr<ALLOC>& rhsValue);

    static bool doubleValuesAlmostEqual(double lhs, double rhs);
};

template <typename ALLOC>
bool ReflectableUtil::equal(const IBasicReflectableConstPtr<ALLOC>& lhs,
        const IBasicReflectableConstPtr<ALLOC>& rhs)
{
    if (lhs == nullptr || rhs == nullptr)
        return lhs == rhs;

    const auto& lhsTypeInfo = lhs->getTypeInfo();
    const auto& rhsTypeInfo = rhs->getTypeInfo();

    if (lhsTypeInfo.getSchemaType() != rhsTypeInfo.getSchemaType() ||
            lhsTypeInfo.getSchemaName() != rhsTypeInfo.getSchemaName())
        return false;

    if (lhs->isArray() || rhs->isArray())
    {
        if (!lhs->isArray() || !rhs->isArray())
            return false;
        return arraysEqual<ALLOC>(lhs, rhs);
    }
    else if (TypeInfoUtil::isCompound(lhsTypeInfo.getSchemaType()))
    {
        return compoundsEqual<ALLOC>(lhs, rhs);
    }
    else
    {
        return valuesEqual<ALLOC>(lhs, rhs);
    }
}

template <typename ALLOC>
bool ReflectableUtil::arraysEqual(const IBasicReflectableConstPtr<ALLOC>& lhsArray,
        const IBasicReflectableConstPtr<ALLOC>& rhsArray)
{
    if (lhsArray->size() != rhsArray->size())
        return false;

    for (size_t i = 0; i < lhsArray->size(); ++i)
    {
        if (!equal<ALLOC>(lhsArray->at(i), rhsArray->at(i)))
            return false;
    }

    return true;
}

template <typename ALLOC>
bool ReflectableUtil::compoundsEqual(const IBasicReflectableConstPtr<ALLOC>& lhsCompound,
        const IBasicReflectableConstPtr<ALLOC>& rhsCompound)
{
    for (const auto& parameterInfo : lhsCompound->getTypeInfo().getParameters())
    {
        auto lhsParameter = lhsCompound->getParameter(parameterInfo.schemaName);
        auto rhsParameter = rhsCompound->getParameter(parameterInfo.schemaName);
        if (!equal<ALLOC>(lhsParameter, rhsParameter))
            return false;
    }

    if (TypeInfoUtil::hasChoice(lhsCompound->getTypeInfo().getSchemaType()))
    {
        if (lhsCompound->getChoice() != rhsCompound->getChoice())
            return false;

        if (!lhsCompound->getChoice().empty())
        {
            auto lhsField = lhsCompound->getField(lhsCompound->getChoice());
            auto rhsField = rhsCompound->getField(rhsCompound->getChoice());
            if (!equal<ALLOC>(lhsField, rhsField))
                return false;
        }
    }
    else
    {
        for (const auto& fieldInfo : lhsCompound->getTypeInfo().getFields())
        {
            auto lhsField = lhsCompound->getField(fieldInfo.schemaName);
            auto rhsField = rhsCompound->getField(fieldInfo.schemaName);
            if (!equal<ALLOC>(lhsField, rhsField))
                return false;
        }
    }

    return true;
}

template <typename ALLOC>
bool ReflectableUtil::valuesEqual(const IBasicReflectableConstPtr<ALLOC>& lhsValue,
        const IBasicReflectableConstPtr<ALLOC>& rhsValue)
{
    CppType cppType = lhsValue->getTypeInfo().getCppType();
    if (cppType == CppType::ENUM || cppType == CppType::BITMASK)
        cppType = lhsValue->getTypeInfo().getUnderlyingType().getCppType();

    switch (cppType)
    {
    case CppType::BOOL:
        return lhsValue->getBool() == rhsValue->getBool();
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
        return lhsValue->toInt() == rhsValue->toInt();
    case CppType::UINT8:
    case CppType::UINT16:
    case CppType::UINT32:
    case CppType::UINT64:
        return lhsValue->toUInt() == rhsValue->toUInt();
    case CppType::FLOAT:
    case CppType::DOUBLE:
        return doubleValuesAlmostEqual(lhsValue->toDouble(), rhsValue->toDouble());
    case CppType::BYTES:
        {
            Span<const uint8_t> lhs = lhsValue->getBytes();
            Span<const uint8_t> rhs = rhsValue->getBytes();

            return lhs.size() == rhs.size() && std::equal(lhs.begin(), lhs.end(), rhs.begin());
        }
    case CppType::STRING:
        return lhsValue->getStringView() == rhsValue->getStringView();
    case CppType::BIT_BUFFER:
        return lhsValue->getBitBuffer() == rhsValue->getBitBuffer();
    default:
        throw CppRuntimeException("ReflectableUtil::valuesEqual - Unexpected C++ type!");
    }
}

inline bool ReflectableUtil::doubleValuesAlmostEqual(double lhs, double rhs)
{
    if (std::isinf(lhs) || std::isinf(rhs))
        return std::isinf(lhs) && std::isinf(rhs) && ((lhs > 0.0 && rhs > 0.0) || (lhs < 0.0 && rhs < 0.0));

    if (std::isnan(lhs) || std::isnan(rhs))
        return std::isnan(lhs) && std::isnan(rhs);

    // see: https://en.cppreference.com/w/cpp/types/numeric_limits/epsilon
    return std::fabs(lhs - rhs) <= std::numeric_limits<double>::epsilon() * std::fabs(lhs + rhs)
            || std::fabs(lhs - rhs) < std::numeric_limits<double>::min();
}

} // namespace zserio

#endif // ZSERIO_REFLECTABLE_UTIL_H_INC
