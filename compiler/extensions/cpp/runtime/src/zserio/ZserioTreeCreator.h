#ifndef ZSERIO_ZSERIO_TREE_CREATOR_H_INC
#define ZSERIO_ZSERIO_TREE_CREATOR_H_INC

#include <limits>
#include <type_traits>
#include <cerrno>
#include <cstdlib>

#include "zserio/BitBuffer.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/IReflectable.h"
#include "zserio/ITypeInfo.h"
#include "zserio/StringView.h"
#include "zserio/TypeInfoUtil.h"
#include "zserio/Traits.h"
#include "zserio/Vector.h"
#include "zserio/Types.h"

namespace zserio
{

namespace detail
{

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyValue(const IBasicTypeInfo<ALLOC>& typeInfo, T&& value, const ALLOC& allocator);

template <typename T, typename U,
        typename std::enable_if<std::is_unsigned<typename std::decay<U>::type>::value, int>::type = 0>
bool checkArithmeticValueRanges(U value)
{
    // value is unsigned
    return (value <= static_cast<U>(std::numeric_limits<T>::max()));
}

template <typename T, typename U,
        typename std::enable_if<std::is_signed<typename std::decay<U>::type>::value &&
                std::is_signed<typename std::decay<T>::type>::value, int>::type = 0>
bool checkArithmeticValueRanges(U value)
{
    // value is signed and it is converted to signed value
    return (static_cast<int64_t>(value) >= static_cast<int64_t>(std::numeric_limits<T>::min()) &&
            static_cast<int64_t>(value) <= static_cast<int64_t>(std::numeric_limits<T>::max()));
}

template <typename T, typename U,
        typename std::enable_if<std::is_signed<typename std::decay<U>::type>::value &&
                std::is_unsigned<typename std::decay<T>::type>::value, int>::type = 0>
bool checkArithmeticValueRanges(U value)
{
    // value is signed and it is converted to unsigned value
    return (value >= 0 && static_cast<uint64_t>(value) <= static_cast<uint64_t>(std::numeric_limits<T>::max()));
}

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyBoolValue(bool value, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(static_cast<T>(value), allocator);
}

template <typename T, typename U, typename ALLOC>
AnyHolder<ALLOC> makeAnyBoolValue(const U& value, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Value '") << value <<
            "' cannot be converted to bool value!";
}

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyIntegralValue(bool value, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Bool value '") << value <<
            "' cannot be converted to integral type!";
}

template <typename T, typename U, typename ALLOC,
        typename std::enable_if<std::is_integral<typename std::decay<U>::type>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyIntegralValue(U value, const ALLOC& allocator)
{
    // check ranges of integers
    if (!checkArithmeticValueRanges<T>(value))
    {
        throw CppRuntimeException("ZserioTreeCreator: Integral value '") << value << "' overflow (<" <<
                std::numeric_limits<T>::min() << ", " << std::numeric_limits<T>::max() << ">)!";
    }

    return AnyHolder<ALLOC>(static_cast<T>(value), allocator);
}

template <typename T, typename U, typename ALLOC,
        typename std::enable_if<!std::is_integral<typename std::decay<U>::type>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyIntegralValue(const U& value, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Value '") << value <<
            "' cannot be converted to integral value!";
}

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyFloatingValue(bool value, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Bool value '") << value <<
            "' cannot be converted to floating type!";
}

template <typename T, typename U, typename ALLOC,
        typename std::enable_if<std::is_arithmetic<typename std::decay<U>::type>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyFloatingValue(U value, const ALLOC& allocator)
{
    // allow conversion integers to floats
    return AnyHolder<ALLOC>(static_cast<T>(value), allocator);
}

template <typename T, typename U, typename ALLOC,
        typename std::enable_if<!std::is_arithmetic<typename std::decay<U>::type>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyFloatingValue(const U& value, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Value '") << value <<
            "' cannot be converted to floating value!";
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyStringValue(const string<ALLOC>& value, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(value, allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyStringValue(string<ALLOC>&& value, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(std::move(value), allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyStringValue(StringView value, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(toString(value, allocator), allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyStringValue(const char* value, const ALLOC& allocator)
{
    return makeAnyStringValue(StringView(value), allocator);
}

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyStringValue(const T&, const ALLOC&)
{
    throw CppRuntimeException("ZserioTreeCreator: Trying to make any string value from unsupported type!");
}

template <typename ALLOC>
AnyHolder<ALLOC> parseEnumStringValue(StringView stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    for (const auto& itemInfo : typeInfo.getEnumItems())
    {
        if (itemInfo.schemaName == stringValue)
        {
            if (TypeInfoUtil::isSigned(typeInfo.getUnderlyingType().getCppType()))
            {
                return makeAnyValue(typeInfo.getUnderlyingType(), static_cast<int64_t>(itemInfo.value),
                        allocator);
            }
            else
            {
                return makeAnyValue(typeInfo.getUnderlyingType(), itemInfo.value, allocator);
            }
        }
    }

    return AnyHolder<ALLOC>(allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyEnumValue(StringView stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    if (!stringValue.empty())
    {
        const char firstChar = stringValue[0];
        if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z') ||
                firstChar == '_')
        {
            AnyHolder<ALLOC> anyValue = parseEnumStringValue(stringValue, typeInfo, allocator);
            if (anyValue.hasValue())
                return anyValue;
        }
        // else it's a no match
    }

    throw CppRuntimeException("ZserioTreeCreator: Cannot create enum '") << typeInfo.getSchemaName() <<
            "' from string value '" << stringValue << "'!";
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyEnumValue(const string<ALLOC>& stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyEnumValue(StringView(stringValue), typeInfo, allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyEnumValue(const char* stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyEnumValue(StringView(stringValue), typeInfo, allocator);
}

template <typename T, typename ALLOC,
        typename std::enable_if<std::is_enum<T>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyEnumValue(T enumValue, const IBasicTypeInfo<ALLOC>&, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(enumValue, allocator);
}

template <typename T, typename ALLOC,
        typename std::enable_if<!std::is_enum<T>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyEnumValue(T enumRawValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyValue(typeInfo.getUnderlyingType(), enumRawValue, allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> parseBitmaskStringValue(StringView stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    uint64_t value = 0;
    size_t pos = 0;
    while (pos < stringValue.size())
    {
        bool match = false;
        const size_t available = stringValue.size() - pos;
        for (const auto& itemInfo : typeInfo.getBitmaskValues())
        {
            if (available >= itemInfo.schemaName.size() &&
                    stringValue.substr(pos, itemInfo.schemaName.size()) == itemInfo.schemaName)
            {
                const size_t newPos = pos + itemInfo.schemaName.size();
                // check that the identifier really ends here
                if (newPos == stringValue.size() || stringValue[newPos] == ' ' || stringValue[newPos] == '|' )
                {
                    value |= itemInfo.value;
                    if (newPos == stringValue.size())
                        return makeAnyValue(typeInfo.getUnderlyingType(), value, allocator); // end of string
                    match = true;
                    pos = itemInfo.schemaName.size();
                    break;
                }
            }
        }

        if (!match)
            break;

        while (pos < stringValue.size() && stringValue[pos] == ' ')
            ++pos;

        if (pos < stringValue.size() && stringValue[pos] == '|')
            ++pos;

        while (pos < stringValue.size() && stringValue[pos] == ' ')
            ++pos;
    }

    // invalid format or identifier
    return AnyHolder<ALLOC>(allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> parseBitmaskNumericStringValue(const char* stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    char *pEnd = nullptr;
    errno = 0;
    uint64_t value = std::strtoull(stringValue, &pEnd, 10);
    if (errno == ERANGE)
        return AnyHolder<ALLOC>(allocator);
    return makeAnyValue(typeInfo.getUnderlyingType(), value, allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyBitmaskValue(StringView stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    if (!stringValue.empty())
    {
        const char firstChar = stringValue[0];
        if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z') ||
                firstChar == '_')
        {
            AnyHolder<ALLOC> anyValue = parseBitmaskStringValue(stringValue, typeInfo, allocator);
            if (anyValue.hasValue())
                return anyValue;
        }
        else if (firstChar >= '0' && firstChar <= '9') // bitmask can be only unsigned
        {
            // ensure zero-terminated string
            const string<ALLOC> numericStringValue = toString(stringValue, allocator);
            AnyHolder<ALLOC> anyValue = parseBitmaskNumericStringValue(numericStringValue.c_str(), typeInfo,
                    allocator);
            if (anyValue.hasValue())
                return anyValue;
        }
    }

    throw CppRuntimeException("ZserioTreeCreator: Cannot create bitmask '") << typeInfo.getSchemaName() <<
            "' from string value '" << stringValue << "'!";
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyBitmaskValue(const string<ALLOC>& stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyBitmaskValue(StringView(stringValue), typeInfo, allocator);
}

template <typename ALLOC>
AnyHolder<ALLOC> makeAnyBitmaskValue(const char* stringValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyBitmaskValue(StringView(stringValue), typeInfo, allocator);
}

template <typename T, typename ALLOC,
        typename std::enable_if<is_bitmask<T>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyBitmaskValue(T bitmaskValue, const IBasicTypeInfo<ALLOC>&, const ALLOC& allocator)
{
    return AnyHolder<ALLOC>(bitmaskValue, allocator);
}

template <typename T, typename ALLOC,
        typename std::enable_if<!is_bitmask<T>::value, int>::type = 0>
AnyHolder<ALLOC> makeAnyBitmaskValue(T bitmaskRawValue, const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator)
{
    return makeAnyValue(typeInfo.getUnderlyingType(), bitmaskRawValue, allocator);
}

template <typename T, typename ALLOC>
AnyHolder<ALLOC> makeAnyValue(const IBasicTypeInfo<ALLOC>& typeInfo, T&& value, const ALLOC& allocator)
{
    switch (typeInfo.getCppType())
    {
    case CppType::BOOL:
        return makeAnyBoolValue<bool>(std::forward<T>(value), allocator);
    case CppType::UINT8:
        return makeAnyIntegralValue<uint8_t>(std::forward<T>(value), allocator);
    case CppType::UINT16:
        return makeAnyIntegralValue<uint16_t>(std::forward<T>(value), allocator);
    case CppType::UINT32:
        return makeAnyIntegralValue<uint32_t>(std::forward<T>(value), allocator);
    case CppType::UINT64:
        return makeAnyIntegralValue<uint64_t>(std::forward<T>(value), allocator);
    case CppType::INT8:
        return makeAnyIntegralValue<int8_t>(std::forward<T>(value), allocator);
    case CppType::INT16:
        return makeAnyIntegralValue<int16_t>(std::forward<T>(value), allocator);
    case CppType::INT32:
        return makeAnyIntegralValue<int32_t>(std::forward<T>(value), allocator);
    case CppType::INT64:
        return makeAnyIntegralValue<int64_t>(std::forward<T>(value), allocator);
    case CppType::FLOAT:
        return makeAnyFloatingValue<float>(std::forward<T>(value), allocator);
    case CppType::DOUBLE:
        return makeAnyFloatingValue<double>(std::forward<T>(value), allocator);
    case CppType::STRING:
        return makeAnyStringValue(std::forward<T>(value), allocator);
    case CppType::ENUM:
        return makeAnyEnumValue(std::forward<T>(value), typeInfo, allocator);
    case CppType::BITMASK:
        return makeAnyBitmaskValue(std::forward<T>(value), typeInfo, allocator);
    default:
        return AnyHolder<ALLOC>(std::forward<T>(value), allocator);
    }
}

// overload for values which are already in AnyHolder
template <typename ALLOC>
AnyHolder<ALLOC> makeAnyValue(const IBasicTypeInfo<ALLOC>&, AnyHolder<ALLOC>&& anyValue, const ALLOC&)
{
    return std::move(anyValue);
}

enum class CreatorState : uint8_t
{
    BEFORE_ROOT,
    IN_COMPOUND,
    IN_ARRAY
};

} // namespace detail

/**
 * Allows to append detail::CreatorState to CppRuntimeException.
 *
 * \param exception Exception to modify.
 * \param state Creator state to append.
 *
 * \return Reference to the exception to allow operator chaining.
 */
CppRuntimeException& operator<<(CppRuntimeException& exception, detail::CreatorState state);

/**
 * Allows to build zserio object tree defined by the given type info.
 */
template <typename ALLOC>
class BasicZserioTreeCreator : AllocatorHolder<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param typeInfo Type info defining the tree.
     */
    explicit BasicZserioTreeCreator(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator = ALLOC());

    /**
     * Creates the top level compound element and move to state of building its children.
     */
    void beginRoot();

    /**
     * Finishes building and returns the created tree.
     *
     * \return Zserio object tree.
     *
     * \throw CppRuntimeException When the creator is not in state of building the root object.
     */
    IBasicReflectablePtr<ALLOC> endRoot();

    /**
     * Creates an array field within the current compound.
     *
     * \param name Name of the array field.
     *
     * \throw CppRuntimeException When the field doesn't exist or when the creator is not in a compound.
     */
    void beginArray(const string<ALLOC>& name);

    /**
     * Finishes the array field.
     *
     * \throw CppRuntimeException When the creator is not in an array field.
     */
    void endArray();

    /**
     * Creates a compound field within the current compound.
     *
     * \param name Name of the compound field.
     *
     * \throw CppRuntimeException When the field doesn't exist or when the creator is not in a compound.
     */
    void beginCompound(const string<ALLOC>& name);

    /**
     * Finishes the compound.
     *
     * \throw CppRuntimeException When the creator is not in a compound.
     */
    void endCompound();

    /**
     * Sets field value within the current compound.
     *
     * \param name Name of the field.
     * \param value Value to set.
     *
     * \throw CppRuntimeException When the field doesn't exist or when the creator is not in a compound.
     */
    template <typename T>
    void setValue(const string<ALLOC>& name, T&& value);

    /**
     * Overload for setting of a null value.
     *
     * Note that this does nothing in C++ since non-optional fields are always present (default initialized).
     *
     * \param name Name of the field.
     * \param nullValue Null value.
     *
     * \throw CppRuntimeException When the field doesn't exist or when the creator is not in a compound.
     */
    void setValue(const string<ALLOC>& name, std::nullptr_t nullValue);

    /**
     * Gets type info of the expected field.
     *
     * \param name Field name.
     *
     * \return Type info of the expected field.
     *
     * \throw CppRuntimeException When the creator is not in a compound.
     */
    const IBasicTypeInfo<ALLOC>& getFieldType(const string<ALLOC>& name) const;

    /**
     * Creates compound array element within the current array.
     *
     * \throw CppRuntimeException When the creator is not in an array of compounds.
     */
    void beginCompoundElement();

    /**
     * Finishes the compound element.
     *
     * \throw CppRuntimeException When the creator is not in a compound element.
     */
    void endCompoundElement();

    /**
     * Adds the value to the array.
     *
     * \param value Value to add.
     *
     * \throw CppRuntimeException When the creator is not in an array of simple values.
     */
    template <typename T>
    void addValueElement(T&& value);

    /**
     * Gets type info of the expected array element.
     *
     * \return Type info of the expected array element.
     *
     * \throw CppRuntimeException When the creator is not in an array.
     */
    const IBasicTypeInfo<ALLOC>& getElementType() const;

private:
    using AllocatorHolder<ALLOC>::get_allocator;

    const IBasicTypeInfo<ALLOC>& getTypeInfo() const;
    const BasicFieldInfo<ALLOC>& findFieldInfo(const IBasicTypeInfo<ALLOC>& typeInfo, StringView name) const;

    template <typename T>
    AnyHolder<ALLOC> makeAnyValue(const IBasicTypeInfo<ALLOC>& typeInfo, T&& value) const;

    const IBasicTypeInfo<ALLOC>& m_typeInfo;
    vector<std::reference_wrapper<const BasicFieldInfo<ALLOC>>, ALLOC> m_fieldInfoStack;
    vector<IBasicReflectablePtr<ALLOC>, ALLOC> m_valueStack;
    detail::CreatorState m_state = detail::CreatorState::BEFORE_ROOT;
};

/** Typedef provided for convenience - using default std::allocator<uint8_t>. */
using ZserioTreeCreator = BasicZserioTreeCreator<std::allocator<uint8_t>>;

template <typename ALLOC>
BasicZserioTreeCreator<ALLOC>::BasicZserioTreeCreator(const IBasicTypeInfo<ALLOC>& typeInfo,
        const ALLOC& allocator) :
        AllocatorHolder<ALLOC>(allocator),
        m_typeInfo(typeInfo), m_fieldInfoStack(allocator), m_valueStack(allocator)
{}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::beginRoot()
{
    if (m_state != detail::CreatorState::BEFORE_ROOT)
        throw CppRuntimeException("ZserioTreeCreator: Cannot begin root in state '") << m_state << "'!";

    m_valueStack.push_back(m_typeInfo.createInstance(get_allocator()));
    m_state = detail::CreatorState::IN_COMPOUND;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> BasicZserioTreeCreator<ALLOC>::endRoot()
{
    if (m_state != detail::CreatorState::IN_COMPOUND || m_valueStack.size() != 1)
        throw CppRuntimeException("ZserioTreeCreator: Cannot end root in state '") << m_state << "'!";

    m_state = detail::CreatorState::BEFORE_ROOT;
    auto value = m_valueStack.back();
    m_valueStack.pop_back();
    return value;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::beginArray(const string<ALLOC>& name)
{
    if (m_state != detail::CreatorState::IN_COMPOUND)
        throw CppRuntimeException("ZserioTreeCreator: Cannot begin array in state '") << m_state << "'!";

    const auto& parentTypeInfo = getTypeInfo();
    const auto& fieldInfo = findFieldInfo(parentTypeInfo, name);
    if (!fieldInfo.isArray)
    {
        throw CppRuntimeException("ZserioTreeCreator: Member '") << fieldInfo.schemaName <<
                 "' is not an array!";
    }

    m_fieldInfoStack.push_back(fieldInfo);

    // note that we cannot just call getField() in case that the array is not optional like we do it in
    // setValue() and beginCompound() methods because in case of arrays we would join multiple arrays together
    // when this method is called multiple times with the same name - thus we will just create a new array
    //
    // moreover we need to properly initialize arrays of dynamic bit fields
    // see https://github.com/ndsev/zserio/issues/414
    m_valueStack.push_back(m_valueStack.back()->createField(name));

    m_state = detail::CreatorState::IN_ARRAY;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::endArray()
{
    if (m_state != detail::CreatorState::IN_ARRAY)
        throw CppRuntimeException("ZserioTreeCreator: Cannot end array in state '") << m_state << "'!";

    m_fieldInfoStack.pop_back();
    m_valueStack.pop_back();
    m_state = detail::CreatorState::IN_COMPOUND;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::beginCompound(const string<ALLOC>& name)
{
    if (m_state != detail::CreatorState::IN_COMPOUND)
        throw CppRuntimeException("ZserioTreeCreator: Cannot begin compound in state '") << m_state << "'!";

    const auto& parentTypeInfo = getTypeInfo();
    const auto& fieldInfo = findFieldInfo(parentTypeInfo, name);
    if (fieldInfo.isArray)
        throw CppRuntimeException("ZserioTreeCreator: Member '") << fieldInfo.schemaName << "' is an array!";

    if (!TypeInfoUtil::isCompound(fieldInfo.typeInfo.getCppType()))
    {
        throw CppRuntimeException("ZserioTreeCreator: Member '") << fieldInfo.schemaName <<
                "' is not a compound!";
    }

    m_fieldInfoStack.push_back(fieldInfo);
    if (TypeInfoUtil::hasChoice(parentTypeInfo.getCppType()) || fieldInfo.isOptional)
    {
        // optional field, or field within choice or union -> create the new compound
        m_valueStack.push_back(m_valueStack.back()->createField(name));
    }
    else
    {
        m_valueStack.push_back(m_valueStack.back()->getField(name));
    }

    m_state = detail::CreatorState::IN_COMPOUND;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::endCompound()
{
    if (m_state != detail::CreatorState::IN_COMPOUND || m_fieldInfoStack.empty())
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot end compound in state '") << m_state <<
                "'" << (m_fieldInfoStack.empty() ? ", expecting endRoot!" : "!'");
    }

    const BasicFieldInfo<ALLOC>& fieldInfo = m_fieldInfoStack.back();
    if (fieldInfo.isArray)
        throw CppRuntimeException("ZserioTreeCreator: Cannot end compound, it's an array element!");

    m_fieldInfoStack.pop_back();
    m_valueStack.pop_back();
}

template <typename ALLOC>
template <typename T>
void BasicZserioTreeCreator<ALLOC>::setValue(const string<ALLOC>& name, T&& value)
{
    if (m_state != detail::CreatorState::IN_COMPOUND)
        throw CppRuntimeException("ZserioTreeCreator: Cannot set value in state '") << m_state << "'!";

    const BasicFieldInfo<ALLOC>& fieldInfo = findFieldInfo(getTypeInfo(), name);
    if (fieldInfo.isArray)
    {
        throw CppRuntimeException("ZserioTreeCreator: Expecting array in member '") <<
                fieldInfo.schemaName << "'!";
    }

    m_valueStack.back()->setField(fieldInfo.schemaName,
            makeAnyValue(fieldInfo.typeInfo, std::forward<T>(value)));
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::setValue(const string<ALLOC>& name, std::nullptr_t nullValue)
{
    if (m_state != detail::CreatorState::IN_COMPOUND)
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot set value (null) in state '") << m_state <<
                "'!";
    }

    const BasicFieldInfo<ALLOC>& fieldInfo = findFieldInfo(getTypeInfo(), name);
    if (fieldInfo.isOptional)
    {
        // reset an optional field
        m_valueStack.back()->setField(fieldInfo.schemaName, AnyHolder<ALLOC>(nullValue, get_allocator()));
    }
    else
    {
        // reset non-optional field with default-constructed value
        // (classes generated in C++ do not support null values)
        m_valueStack.back()->createField(fieldInfo.schemaName);
    }
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BasicZserioTreeCreator<ALLOC>::getFieldType(const string<ALLOC>& name) const
{
    if (m_state != detail::CreatorState::IN_COMPOUND)
        throw CppRuntimeException("ZserioTreeCreator: Cannot get field type in state '") << m_state << "'!";

    return findFieldInfo(getTypeInfo(), name).typeInfo;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::beginCompoundElement()
{
    if (m_state != detail::CreatorState::IN_ARRAY)
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot begin compound element in state '") <<
                m_state << "'!";
    }

    const BasicFieldInfo<ALLOC>& fieldInfo = m_fieldInfoStack.back();
    if (!TypeInfoUtil::isCompound(fieldInfo.typeInfo.getCppType()))
    {
        throw CppRuntimeException("ZserioTreeCreator: Member '") << fieldInfo.schemaName <<
                "' is not a compound!";
    }

    auto compoundArray = m_valueStack.back();
    compoundArray->resize(compoundArray->size() + 1);
    m_valueStack.push_back(compoundArray->at(compoundArray->size() - 1));
    m_state = detail::CreatorState::IN_COMPOUND;
}

template <typename ALLOC>
void BasicZserioTreeCreator<ALLOC>::endCompoundElement()
{
    if (m_state != detail::CreatorState::IN_COMPOUND || m_fieldInfoStack.empty())
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot end compound element in state '") <<
                m_state << (m_fieldInfoStack.empty() ? ", expecting endRoot!" : "'!");
    }

    const BasicFieldInfo<ALLOC>& fieldInfo = m_fieldInfoStack.back();
    if (!fieldInfo.isArray)
        throw CppRuntimeException("ZserioTreeCreator: Cannot end compound element, not in array!");

    m_valueStack.pop_back();
    m_state = detail::CreatorState::IN_ARRAY;
}

template <typename ALLOC>
template <typename T>
void BasicZserioTreeCreator<ALLOC>::addValueElement(T&& value)
{
    if (m_state != detail::CreatorState::IN_ARRAY)
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot add value element in state '") <<
                m_state << "'!";
    }

    const BasicFieldInfo<ALLOC>& fieldInfo = m_fieldInfoStack.back();
    m_valueStack.back()->append(makeAnyValue(fieldInfo.typeInfo, std::forward<T>(value)));
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BasicZserioTreeCreator<ALLOC>::getElementType() const
{
    if (m_state != detail::CreatorState::IN_ARRAY)
    {
        throw CppRuntimeException("ZserioTreeCreator: Cannot get element type in state '") << m_state <<
                "'!";
    }

    return m_fieldInfoStack.back().get().typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BasicZserioTreeCreator<ALLOC>::getTypeInfo() const
{
    return m_fieldInfoStack.empty() ? m_typeInfo : m_fieldInfoStack.back().get().typeInfo;
}

template <typename ALLOC>
const BasicFieldInfo<ALLOC>& BasicZserioTreeCreator<ALLOC>::findFieldInfo(
        const IBasicTypeInfo<ALLOC>& typeInfo, StringView name) const
{
    Span<const BasicFieldInfo<ALLOC>> fields = typeInfo.getFields();
    auto found_it = std::find_if(fields.begin(), fields.end(),
            [name](const BasicFieldInfo<ALLOC>& field){ return field.schemaName == name; });
    if (found_it == fields.end())
    {
        throw CppRuntimeException("ZserioTreeCreator: Member '") << name <<  "' not found in '" <<
                typeInfo.getSchemaName() << "'!";
    }

    return *found_it;
}

template <typename ALLOC>
template <typename T>
AnyHolder<ALLOC> BasicZserioTreeCreator<ALLOC>::makeAnyValue(
        const IBasicTypeInfo<ALLOC>& typeInfo, T&& value) const
{
    return detail::makeAnyValue(typeInfo, std::forward<T>(value), get_allocator());
}

} // namespace zserio

#endif // ZSERIO_ZSERIO_TREE_CREATOR_H_INC
