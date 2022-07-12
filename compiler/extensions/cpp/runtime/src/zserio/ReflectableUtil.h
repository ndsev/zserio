#ifndef ZSERIO_REFLECTABLE_UTIL_H_INC
#define ZSERIO_REFLECTABLE_UTIL_H_INC

#include <cmath>
#include <limits>

#include "IReflectable.h"
#include "ITypeInfo.h"
#include "TypeInfoUtil.h"
#include "CppRuntimeException.h"

namespace zserio
{

template <typename ALLOC = std::allocator<uint8_t>>
struct ReflectableUtil
{
public:
    static bool equal(const IBasicReflectableConstPtr<ALLOC>& lhs,
            const IBasicReflectableConstPtr<ALLOC>& rhs);

private:
    static bool arraysEqual(const IBasicReflectableConstPtr<ALLOC>& lhsArray,
            const IBasicReflectableConstPtr<ALLOC>& rhsArray);

    static bool compoundsEqual(const IBasicReflectableConstPtr<ALLOC>& lhsCompound,
            const IBasicReflectableConstPtr<ALLOC>& rhsCompound);

    static bool valuesEqual(const IBasicReflectableConstPtr<ALLOC>& lhsValue,
            const IBasicReflectableConstPtr<ALLOC>& rhsValue);

    static bool doubleValuesAlmostEqual(double lhs, double rhs);
};

template <typename ALLOC>
bool ReflectableUtil<ALLOC>::equal(const IBasicReflectableConstPtr<ALLOC>& lhs,
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
        return arraysEqual(lhs, rhs);
    }
    else if (TypeInfoUtil::isCompound(lhsTypeInfo.getSchemaType()))
    {
        return compoundsEqual(lhs, rhs);
    }
    else
    {
        return valuesEqual(lhs, rhs);
    }
}

template <typename ALLOC>
bool ReflectableUtil<ALLOC>::arraysEqual(const IBasicReflectableConstPtr<ALLOC>& lhsArray,
        const IBasicReflectableConstPtr<ALLOC>& rhsArray)
{
    if (lhsArray->size() != rhsArray->size())
        return false;

    for (size_t i = 0; i < lhsArray->size(); ++i)
    {
        if (!equal(lhsArray->at(i), rhsArray->at(i)))
            return false;
    }

    return true;
}

template <typename ALLOC>
bool ReflectableUtil<ALLOC>::compoundsEqual(const IBasicReflectableConstPtr<ALLOC>& lhsCompound,
        const IBasicReflectableConstPtr<ALLOC>& rhsCompound)
{
    for (const auto& parameterInfo : lhsCompound->getTypeInfo().getParameters())
    {
        auto lhsParameter = lhsCompound->getParameter(parameterInfo.schemaName);
        auto rhsParameter = rhsCompound->getParameter(parameterInfo.schemaName);
        if (!equal(lhsParameter, rhsParameter))
            return false;
    }

    if (TypeInfoUtil::hasChoice(lhsCompound->getTypeInfo().getSchemaType()))
    {
        if (lhsCompound->getChoice() != rhsCompound->getChoice())
            return false;

        if (!lhsCompound->getChoice().empty())
        {
            auto lhsField = lhsCompound->getField(lhsCompound->getChoice());
            auto rhsField = rhsCompound->getField(lhsCompound->getChoice());
            if (!equal(lhsField, rhsField))
                return false;
        }
    }
    else
    {
        for (const auto& fieldInfo : lhsCompound->getTypeInfo().getFields())
        {
            auto lhsField = lhsCompound->getField(fieldInfo.schemaName);
            auto rhsField = rhsCompound->getField(fieldInfo.schemaName);
            if (!equal(lhsField, rhsField))
                return false;
        }
    }

    return true;
}

template <typename ALLOC>
bool ReflectableUtil<ALLOC>::valuesEqual(const IBasicReflectableConstPtr<ALLOC>& lhsValue,
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
    case CppType::STRING:
        return lhsValue->getStringView() == rhsValue->getStringView();
    case CppType::BIT_BUFFER:
        return lhsValue->getBitBuffer() == rhsValue->getBitBuffer();
    default:
        throw CppRuntimeException("ReflectableUtil::valuesEqual - Unexpected C++ type!");
    }
}

template <typename ALLOC>
bool ReflectableUtil<ALLOC>::doubleValuesAlmostEqual(double lhs, double rhs)
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
