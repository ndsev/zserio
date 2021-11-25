#ifndef ZSERIO_INTROSPECTABLE_H_INC
#define ZSERIO_INTROSPECTABLE_H_INC

#include <type_traits>

#include "zserio/AllocatorHolder.h"
#include "zserio/IIntrospectable.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/TypeInfo.h"
#include "zserio/TypeInfoUtil.h"

namespace zserio
{

template <typename ALLOC>
class IntrospectableBase : public IBasicIntrospectable<ALLOC>
{
public:
    explicit IntrospectableBase(const ITypeInfo& typeInfo);
    virtual ~IntrospectableBase() = 0;

    virtual const ITypeInfo& getTypeInfo() const override;
    virtual bool isArray() const override;

    virtual typename IBasicIntrospectable<ALLOC>::Ptr getField(StringView name) const override;
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr getParameter(StringView name) const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr callFunction(StringView name) const override;

    virtual StringView getChoice() const override;

    virtual typename IBasicIntrospectable<ALLOC>::Ptr find(StringView path) const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](StringView path) const override;

    virtual size_t size() const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override;

    // exact checked getters
    virtual bool getBool() const override;
    virtual int8_t getInt8() const override;
    virtual int16_t getInt16() const override;
    virtual int32_t getInt32() const override;
    virtual int64_t getInt64() const override;
    virtual uint8_t getUInt8() const override;
    virtual uint16_t getUInt16() const override;
    virtual uint32_t getUInt32() const override;
    virtual uint64_t getUInt64() const override;
    virtual float getFloat() const override;
    virtual double getDouble() const override;
    virtual const string<RebindAlloc<ALLOC, char>>& getString() const override;
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    // convenience conversions
    virtual int64_t toInt() const override;
    virtual uint64_t toUInt() const override;
    virtual double toDouble() const override;
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const override;

private:
    const ITypeInfo& m_typeInfo;
};

template <typename ALLOC, typename T, typename = void>
class BuiltinIntrospectableBase : public IntrospectableBase<ALLOC>
{
private:
    using Base = IntrospectableBase<ALLOC>;

protected:
    BuiltinIntrospectableBase(const ITypeInfo& typeInfo, const T& value) :
            Base(typeInfo), m_value(value)
    {}

    const T& getValue() const
    {
        return m_value;
    }

private:
    const T& m_value;
};

template <typename ALLOC, typename T>
class BuiltinIntrospectableBase<ALLOC, T,
        typename std::enable_if<std::is_integral<T>::value || std::is_floating_point<T>::value>::type> :
                public IntrospectableBase<ALLOC>
{
private:
    using Base = IntrospectableBase<ALLOC>;

protected:
    BuiltinIntrospectableBase(const ITypeInfo& typeInfo, T value) :
            Base(typeInfo), m_value(value)
    {}

    T getValue() const
    {
        return m_value;
    }

private:
    T m_value;
};

template <typename ALLOC, typename T>
class IntegralIntrospectableBase : public BuiltinIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_integral<T>::value, "T must be a signed integral type!");

    using BuiltinIntrospectableBase<ALLOC, T>::BuiltinIntrospectableBase;
    using BuiltinIntrospectableBase<ALLOC, T>::getValue;

public:
    virtual double toDouble() const override
    {
        return getValue();
    }

    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return toStringImpl(getValue(), allocator);
    }

private:
    template <typename U = T>
    static string<RebindAlloc<ALLOC, char>> toStringImpl(U value, const ALLOC& allocator)
    {
        return ::zserio::toString<RebindAlloc<ALLOC, char>>(value, allocator);
    }

    static string<RebindAlloc<ALLOC, char>> toStringImpl(bool value, const ALLOC& allocator)
    {
        return string<RebindAlloc<ALLOC, char>>(value ? "true" : "false", allocator);
    }
};

template <typename ALLOC, typename T>
class SignedIntrospectableBase : public IntegralIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_signed<T>::value, "T must be a signed integral type!");

    using IntegralIntrospectableBase<ALLOC, T>::IntegralIntrospectableBase;
    using IntegralIntrospectableBase<ALLOC, T>::getValue;

public:
    virtual int64_t toInt() const override
    {
        return getValue();
    }
};

template <typename ALLOC, typename T>
class UnsignedIntrospectableBase : public IntegralIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_unsigned<T>::value, "T must be an unsigned integral type!");

    using IntegralIntrospectableBase<ALLOC, T>::IntegralIntrospectableBase;
    using IntegralIntrospectableBase<ALLOC, T>::getValue;

public:
    virtual uint64_t toUInt() const override
    {
        return getValue();
    }
};

template <typename ALLOC>
class BasicBoolIntrospectable : public UnsignedIntrospectableBase<ALLOC, bool>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, bool>;

public:
    // TODO[Mi-L@]: typeInfo here is needed due to builtin arrays, should we check that it's bool type info?
    BasicBoolIntrospectable(const ITypeInfo& typeInfo, bool value) :
            Base(typeInfo, value)
    {}

    virtual bool getBool() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicInt8Introspectable : public SignedIntrospectableBase<ALLOC, int8_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int8_t>;

public:
    BasicInt8Introspectable(const ITypeInfo& typeInfo, int8_t value) :
            Base(typeInfo, value)
    {}

    virtual int8_t getInt8() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicInt16Introspectable : public SignedIntrospectableBase<ALLOC, int16_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int16_t>;

public:
    BasicInt16Introspectable(const ITypeInfo& typeInfo, int16_t value) :
            Base(typeInfo, value)
    {}

    virtual int16_t getInt16() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicInt32Introspectable : public SignedIntrospectableBase<ALLOC, int32_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int32_t>;

public:
    BasicInt32Introspectable(const ITypeInfo& typeInfo, int32_t value) :
            Base(typeInfo, value)
    {}

    virtual int32_t getInt32() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicInt64Introspectable : public SignedIntrospectableBase<ALLOC, int64_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int64_t>;

public:
    BasicInt64Introspectable(const ITypeInfo& typeInfo, int64_t value) :
            Base(typeInfo, value)
    {}

    virtual int64_t getInt64() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicUInt8Introspectable : public UnsignedIntrospectableBase<ALLOC, uint8_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint8_t>;

public:
    BasicUInt8Introspectable(const ITypeInfo& typeInfo, uint8_t value) :
            Base(typeInfo, value)
    {}

    virtual uint8_t getUInt8() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicUInt16Introspectable : public UnsignedIntrospectableBase<ALLOC, uint16_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint16_t>;

public:
    BasicUInt16Introspectable(const ITypeInfo& typeInfo, uint16_t value) :
            Base(typeInfo, value)
    {}

    virtual uint16_t getUInt16() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicUInt32Introspectable : public UnsignedIntrospectableBase<ALLOC, uint32_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint32_t>;

public:
    BasicUInt32Introspectable(const ITypeInfo& typeInfo, uint32_t value) :
            Base(typeInfo, value)
    {}

    virtual uint32_t getUInt32() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicUInt64Introspectable : public UnsignedIntrospectableBase<ALLOC, uint64_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint64_t>;

public:
    BasicUInt64Introspectable(const ITypeInfo& typeInfo, uint64_t value) :
            Base(typeInfo, value)
    {}

    virtual uint64_t getUInt64() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC, typename T>
class FloatingPointIntrospectableBase : public BuiltinIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_floating_point<T>::value, "T must be a floating point type!");

    using BuiltinIntrospectableBase<ALLOC, T>::getValue;
    using BuiltinIntrospectableBase<ALLOC, T>::BuiltinIntrospectableBase;

public:
    virtual double toDouble() const override
    {
        return getValue();
    }
};

template <typename ALLOC>
class BasicFloatIntrospectable : public FloatingPointIntrospectableBase<ALLOC, float>
{
private:
    using Base = FloatingPointIntrospectableBase<ALLOC, float>;

public:
    BasicFloatIntrospectable(const ITypeInfo& typeInfo, float value) :
            Base(typeInfo, value)
    {}

    virtual float getFloat() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicDoubleIntrospectable : public FloatingPointIntrospectableBase<ALLOC, double>
{
private:
    using Base = FloatingPointIntrospectableBase<ALLOC, double>;

public:
    BasicDoubleIntrospectable(const ITypeInfo& typeInfo, double value) :
            Base(typeInfo, value)
    {}

    virtual double getDouble() const override
    {
        return Base::getValue();
    }
};

template <typename ALLOC>
class BasicStringIntrospectable : public BuiltinIntrospectableBase<ALLOC, string<RebindAlloc<ALLOC, char>>>
{
private:
    using Base = BuiltinIntrospectableBase<ALLOC, string<RebindAlloc<ALLOC, char>>>;

public:
    explicit BasicStringIntrospectable(const ITypeInfo& typeInfo,
            const string<RebindAlloc<ALLOC, char>>& value) :
            Base(typeInfo, value)
    {}

    virtual const string<RebindAlloc<ALLOC, char>>& getString() const override
    {
        return Base::getValue();
    }

    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return string<RebindAlloc<ALLOC, char>>(getString(), allocator);
    }
};

template <typename ALLOC>
class BasicBitBufferIntrospectable : public BuiltinIntrospectableBase<ALLOC, BasicBitBuffer<ALLOC>>
{
private:
    using Base = BuiltinIntrospectableBase<ALLOC, BasicBitBuffer<ALLOC>>;

public:
    explicit BasicBitBufferIntrospectable(const ITypeInfo& typeInfo, const BasicBitBuffer<ALLOC>& value) :
            Base(typeInfo, value)
    {}

    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override
    {
        return Base::getValue();
    }
};

namespace detail
{

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr getFieldFromObject(
        const IBasicIntrospectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& fields = typeInfo.getFields();
        auto fieldsIt = std::find_if(fields.begin(), fields.end(),
                [name](const FieldInfo& fieldInfo) { return fieldInfo.schemaName == name; });
        if (fieldsIt != fields.end())
            return object.getField(name);
    }

    return nullptr;
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr getParameterFromObject(
        const IBasicIntrospectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& parameters = typeInfo.getParameters();
        auto parametersIt = std::find_if(parameters.begin(), parameters.end(),
                [name](const ParameterInfo& parameterInfo) { return parameterInfo.schemaName == name; });
        if (parametersIt != parameters.end())
            return object.getParameter(name);
    }

    return nullptr;
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr callFunctionInObject(
        const IBasicIntrospectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& functions = typeInfo.getFunctions();
        auto functionsIt = std::find_if(functions.begin(), functions.end(),
                [name](const FunctionInfo& functionInfo) { return functionInfo.schemaName == name; });
        if (functionsIt != functions.end())
            return object.callFunction(name);
    }

    return nullptr;
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr getFromObject(
        const IBasicIntrospectable<ALLOC>& object, StringView path, size_t pos)
{
    try
    {
        const size_t dotPos = path.find('.', pos);
        const bool isLast = dotPos == StringView::npos;
        const StringView name = path.substr(pos, dotPos == StringView::npos ? StringView::npos : dotPos - pos);

        const auto field = getFieldFromObject(object, name);
        if (field)
            return isLast ? field : getFromObject(*field, path, dotPos + 1);

        const auto parameter = getParameterFromObject(object, name);
        if (parameter)
            return isLast ? parameter : getFromObject(*parameter, path, dotPos + 1);

        const auto functionResult = callFunctionInObject(object, name);
        if (functionResult)
            return isLast ? functionResult : getFromObject(*functionResult, path, dotPos + 1);
    }
    catch (const CppRuntimeException&)
    {}

    return nullptr;
}

template <typename ALLOC, typename T>
struct IntrospectableTraits
{
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, bool>
{
    using Type = BasicBoolIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int8_t>
{
    using Type = BasicInt8Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int16_t>
{
    using Type = BasicInt16Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int32_t>
{
    using Type = BasicInt32Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int64_t>
{
    using Type = BasicInt64Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint8_t>
{
    using Type = BasicUInt8Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint16_t>
{
    using Type = BasicUInt16Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint32_t>
{
    using Type = BasicUInt32Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint64_t>
{
    using Type = BasicUInt64Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, float>
{
    using Type = BasicFloatIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, double>
{
    using Type = BasicDoubleIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, string<RebindAlloc<ALLOC, char>>>
{
    using Type = BasicStringIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, BasicBitBuffer<ALLOC>>
{
    using Type = BasicBitBufferIntrospectable<ALLOC>;
};

} // namespace detail

// TODO[Mi-L@]: Check if multiple inheritance is ok!
template <typename ALLOC>
class IntrospectableAllocatorHolderBase : public IntrospectableBase<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    IntrospectableAllocatorHolderBase(const ITypeInfo& typeInfo, const ALLOC& allocator) :
            IntrospectableBase<ALLOC>(typeInfo), AllocatorHolder<ALLOC>(allocator)
    {}
};

template <typename ALLOC>
class IntrospectableArrayBase : public IntrospectableAllocatorHolderBase<ALLOC>
{
public:
    using IntrospectableAllocatorHolderBase<ALLOC>::IntrospectableAllocatorHolderBase;
    using IntrospectableAllocatorHolderBase<ALLOC>::getTypeInfo;

    virtual bool isArray() const override
    {
        return true;
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr getField(StringView name) const override;
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr getParameter(StringView name) const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr callFunction(StringView name) const override;

    virtual StringView getChoice() const override;

    virtual typename IBasicIntrospectable<ALLOC>::Ptr find(StringView path) const override;
    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](StringView path) const override;

    virtual bool getBool() const override;
    virtual int8_t getInt8() const override;
    virtual int16_t getInt16() const override;
    virtual int32_t getInt32() const override;
    virtual int64_t getInt64() const override;
    virtual uint8_t getUInt8() const override;
    virtual uint16_t getUInt16() const override;
    virtual uint32_t getUInt32() const override;
    virtual uint64_t getUInt64() const override;
    virtual float getFloat() const override;
    virtual double getDouble() const override;
    virtual const string<RebindAlloc<ALLOC, char>>& getString() const override;
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    virtual int64_t toInt() const override;
    virtual uint64_t toUInt() const override;
    virtual double toDouble() const override;
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const override;
};

template <typename ALLOC, typename RAW_ARRAY>
class BasicBuiltinIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;
    using Base::get_allocator;

    using Introspectable = typename detail::IntrospectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    BasicBuiltinIntrospectableArray(const ITypeInfo& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return std::allocate_shared<Introspectable>(get_allocator(), Base::getTypeInfo(), m_rawArray.at(index));
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class BasicCompoundIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;
    using Base::get_allocator;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    BasicCompoundIntrospectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return m_rawArray.at(index).introspectable(get_allocator());
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
using BasicBitmaskIntrospectableArray = BasicCompoundIntrospectableArray<ALLOC, RAW_ARRAY>;

template <typename ALLOC, typename RAW_ARRAY>
class BasicEnumIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;
    using Base::get_allocator;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    BasicEnumIntrospectableArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType>(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return enumIntrospectable(m_rawArray.at(index), get_allocator());
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC>
class BasicIntrospectableFactory
{
public:
    static typename IBasicIntrospectable<ALLOC>::Ptr getBool(bool value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicBoolIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getBool(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt8(int8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt8(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt16(int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt32(int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt64(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt8(uint8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt8(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt16(uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt32(uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt64(uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt16(
            int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt32(
            int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt64(
            int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt16(
            uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt32(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt64(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarSize(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicUInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarSize(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat16(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicFloatIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat32(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicFloatIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat64(double value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicDoubleIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getString(
            const string<RebindAlloc<ALLOC, char>>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicStringIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getString(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getString(
            StringView value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicStringIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getString(), stringViewToString(value, allocator));
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getBitBuffer(
            const BasicBitBuffer<ALLOC>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicBitBufferIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getBitBuffer(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedSignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<BasicInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedSignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<BasicInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedSignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<BasicInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedSignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'int64_t' value!";
        }
        return std::allocate_shared<BasicInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedUnsignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<BasicUInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedUnsignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<BasicUInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedUnsignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<BasicUInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getFixedUnsignedBitField") +
                    "- invalid bit size '" + bitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<BasicUInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int8_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicSignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<BasicInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int16_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicSignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<BasicInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int32_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicSignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<BasicInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int64_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicSignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<BasicInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint8_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicUnsignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<BasicUInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint16_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicUnsignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<BasicUInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint32_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicUnsignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<BasicUInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint64_t value, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicBuiltinIntrospectableFactory::getDynamicUnsignedBitField") +
                    "- invalid max bit size '" + maxBitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<BasicUInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBuiltinArray(
            const ITypeInfo& typeInfo, const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicBuiltinIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getCompoundArray(
            RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicCompoundIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBitmaskArray(
            RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicBitmaskIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getEnumArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BasicEnumIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }
};

using IntrospectableFactory = BasicIntrospectableFactory<std::allocator<uint8_t>>;

template <typename ALLOC>
IntrospectableBase<ALLOC>::IntrospectableBase(const ITypeInfo& typeInfo) :
        m_typeInfo(typeInfo)
{}

template <typename ALLOC>
IntrospectableBase<ALLOC>::~IntrospectableBase()
{}

template <typename ALLOC>
const ITypeInfo& IntrospectableBase<ALLOC>::getTypeInfo() const
{
    return m_typeInfo;
}

template <typename ALLOC>
bool IntrospectableBase<ALLOC>::isArray() const
{
    return false;
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to get!";
}

template <typename ALLOC>
void IntrospectableBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to set!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no paramters to get!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no functions to call!";
}

template <typename ALLOC>
StringView IntrospectableBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is neither choice nor union!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::find(StringView path) const
{
    return detail::getFromObject(*this, path, 0);
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::operator[](StringView path) const
{
    return find(path);
}

template <typename ALLOC>
size_t IntrospectableBase<ALLOC>::size() const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::at(size_t) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::operator[](size_t) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
bool IntrospectableBase<ALLOC>::getBool() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not boolean type!";
}

template <typename ALLOC>
int8_t IntrospectableBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int8 type!";
}

template <typename ALLOC>
int16_t IntrospectableBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int16 type!";
}

template <typename ALLOC>
int32_t IntrospectableBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int32 type!";
}

template <typename ALLOC>
int64_t IntrospectableBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int64 type!";
}

template <typename ALLOC>
uint8_t IntrospectableBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint8 type!";
}

template <typename ALLOC>
uint16_t IntrospectableBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint16 type!";
}

template <typename ALLOC>
uint32_t IntrospectableBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint32 type!";
}

template <typename ALLOC>
uint64_t IntrospectableBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint64 type!";
}

template <typename ALLOC>
float IntrospectableBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not float type!";
}

template <typename ALLOC>
double IntrospectableBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not double type!";
}

template <typename ALLOC>
const string<RebindAlloc<ALLOC, char>>& IntrospectableBase<ALLOC>::getString() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not string type!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& IntrospectableBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not an extern type!";
}

template <typename ALLOC>
int64_t IntrospectableBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to signed integer is not available!";
}

template <typename ALLOC>
uint64_t IntrospectableBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to unsigned integer is not available!";
}

template <typename ALLOC>
double IntrospectableBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to double is not available!";
}

template <typename ALLOC>
string<RebindAlloc<ALLOC, char>> IntrospectableBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to string is not available!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableArrayBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
void IntrospectableArrayBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableArrayBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableArrayBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
StringView IntrospectableArrayBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableArrayBase<ALLOC>::find(StringView) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableArrayBase<ALLOC>::operator[](StringView) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
bool IntrospectableArrayBase<ALLOC>::getBool() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int8_t IntrospectableArrayBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int16_t IntrospectableArrayBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int32_t IntrospectableArrayBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int64_t IntrospectableArrayBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint8_t IntrospectableArrayBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint16_t IntrospectableArrayBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint32_t IntrospectableArrayBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint64_t IntrospectableArrayBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
float IntrospectableArrayBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
double IntrospectableArrayBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
const string<RebindAlloc<ALLOC, char>>& IntrospectableArrayBase<ALLOC>::getString() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& IntrospectableArrayBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int64_t IntrospectableArrayBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint64_t IntrospectableArrayBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
double IntrospectableArrayBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
string<RebindAlloc<ALLOC, char>> IntrospectableArrayBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

} // namespace zserio

#endif // ZSERIO_INTROSPECTABLE_H_INC
