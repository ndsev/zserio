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
    using IIntrospectablePtr = typename IBasicIntrospectable<ALLOC>::Ptr;

    explicit IntrospectableBase(const ITypeInfo& typeInfo);
    virtual ~IntrospectableBase() = 0;

    // TODO: isConst ???

    virtual const ITypeInfo& getTypeInfo() const override;

    virtual IIntrospectablePtr getField(StringView name) const override;
    virtual void setField(StringView name, const IIntrospectablePtr& value) override;
    virtual IIntrospectablePtr getParameter(StringView name) const override;
    virtual IIntrospectablePtr callFunction(StringView name) const override;

    virtual IIntrospectablePtr find(StringView path) const override;
    virtual IIntrospectablePtr operator[](StringView path) const override;

    virtual size_t size() const override;
    virtual IIntrospectablePtr at(size_t index) const override;
    virtual IIntrospectablePtr operator[](size_t index) const override;

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

// TODO[Mi-L@]: Check if multiple inheritance is ok!
template <typename ALLOC>
class IntrospectableAllocatorHolderBase : public IntrospectableBase<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    IntrospectableAllocatorHolderBase(const ITypeInfo& typeInfo, const ALLOC& allocator) :
            IntrospectableBase<ALLOC>(typeInfo), AllocatorHolder<ALLOC>(allocator)
    {}
};

template <typename ALLOC, typename RAW_ARRAY, class BUILTIN_INTROSPECTABLE>
class BasicBuiltinIntrospectableArray : public IntrospectableAllocatorHolderBase<ALLOC>
{
private:
    using Base = IntrospectableAllocatorHolderBase<ALLOC>;
    using Base::get_allocator;

public:
    BasicBuiltinIntrospectableArray(const ITypeInfo& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IIntrospectablePtr at(size_t index) const override
    {
        return std::allocate_shared<BUILTIN_INTROSPECTABLE>(get_allocator(), m_rawArray.at(index));
    }

    virtual IIntrospectablePtr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class BasicCompoundIntrospectableArray : public IntrospectableAllocatorHolderBase<ALLOC>
{
    using Base = IntrospectableAllocatorHolderBase<ALLOC>;
    using Base::get_allocator;

public:
    BasicCompoundIntrospectableArray(const ITypeInfo& typeInfo, const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IIntrospectablePtr at(size_t index) const override
    {
        return m_rawArray.at(index).introspectable(get_allocator());
    }

    virtual IIntrospectablePtr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const ITypeInfo& m_typeInfo;
    RAW_ARRAY& m_rawArray;
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
class BuiltinIntrospectableBase<ALLOC, T, typename std::enable_if<std::is_integral<T>::value>::type> :
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

    virtual double toDouble() const override
    {
        return getValue();
    }

    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return ::zserio::toString<RebindAlloc<ALLOC, char>>(getValue(), allocator);
    }
};

template <typename ALLOC, typename T>
class SignedIntrospectableBase : public IntegralIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_signed<T>::value, "T must be a signed integral type!");

    using IntegralIntrospectableBase<ALLOC, T>::IntegralIntrospectableBase;
    using IntegralIntrospectableBase<ALLOC, T>::getValue;

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

    virtual uint64_t toUInt() const override
    {
        return getValue();
    }
};

template <typename ALLOC>
class BasicInt8Introspectable : public SignedIntrospectableBase<ALLOC, int8_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int8_t>;

public:
    explicit BasicInt8Introspectable(int8_t value) :
            Base(BuiltinTypeInfo::getInt8(), value)
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
    explicit BasicInt16Introspectable(int16_t value):
            Base(BuiltinTypeInfo::getInt16(), value)
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
    explicit BasicInt32Introspectable(int32_t value) :
            Base(BuiltinTypeInfo::getInt32(), value)
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
    explicit BasicInt64Introspectable(int64_t value) :
            Base(BuiltinTypeInfo::getInt64(), value)
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
    explicit BasicUInt8Introspectable(uint8_t value) :
            Base(BuiltinTypeInfo::getUInt8(), value)
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
    explicit BasicUInt16Introspectable(uint16_t value) :
            Base(BuiltinTypeInfo::getUInt16(), value)
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
    explicit BasicUInt32Introspectable(uint32_t value) :
            Base(BuiltinTypeInfo::getUInt32(), value)
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
    explicit BasicUInt64Introspectable(uint64_t value) :
            Base(BuiltinTypeInfo::getUInt64(), value)
    {}

    virtual uint64_t getUInt64() const override
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
    explicit BasicStringIntrospectable(const string<RebindAlloc<ALLOC, char>>& value) :
            Base(BuiltinTypeInfo::getString(), value)
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
    explicit BasicBitBufferIntrospectable(const BasicBitBuffer<ALLOC>& value) :
            Base(BuiltinTypeInfo::getBitBuffer(), value)
    {}

    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override
    {
        return Base::getValue();
    }
};

using Int8Introspectable = BasicInt8Introspectable<std::allocator<uint8_t>>;
using Int16Introspectable = BasicInt16Introspectable<std::allocator<uint8_t>>;
using Int32Introspectable = BasicInt32Introspectable<std::allocator<uint8_t>>;
using Int64Introspectable = BasicInt64Introspectable<std::allocator<uint8_t>>;
using UInt8Introspectable = BasicUInt8Introspectable<std::allocator<uint8_t>>;
using UInt16Introspectable = BasicUInt16Introspectable<std::allocator<uint8_t>>;
using UInt32Introspectable = BasicUInt32Introspectable<std::allocator<uint8_t>>;
using UInt64Introspectable = BasicUInt64Introspectable<std::allocator<uint8_t>>;
using StringIntrospectable = BasicStringIntrospectable<std::allocator<uint8_t>>;
using BitBufferIntrospectable = BasicBitBufferIntrospectable<std::allocator<uint8_t>>;

namespace detail
{

template <typename ALLOC>
typename IBasicIntrospectable<ALLOC>::Ptr getFieldFromObject(
        const IBasicIntrospectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo))
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
    if (TypeInfoUtil::isCompound(typeInfo))
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
    if (TypeInfoUtil::isCompound(typeInfo))
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

    return nullptr;
}

} // namespace detail

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
typename IBasicIntrospectable<ALLOC>::Ptr IntrospectableBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to get!";
}

template <typename ALLOC>
void IntrospectableBase<ALLOC>::setField(StringView, const typename IBasicIntrospectable<ALLOC>::Ptr&)
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

} // namespace zserio

#endif // ZSERIO_INTROSPECTABLE_H_INC
