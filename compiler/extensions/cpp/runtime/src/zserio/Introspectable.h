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

/**
 * Base class for all introspectable implementations.
 *
 * Implements the find() feature and overrides all generic methods with default throw behavior.
 */
template <typename ALLOC>
class IntrospectableBase : public IBasicIntrospectable<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param typeInfo Type info of the introspected object.
     */
    explicit IntrospectableBase(const ITypeInfo& typeInfo);

    /** Destructor. */
    virtual ~IntrospectableBase() override = 0;

    /**
    * Copying and moving is disallowed!
    * \{
    */
    IntrospectableBase(const IntrospectableBase&) = delete;
    IntrospectableBase& operator=(const IntrospectableBase&) = delete;

    IntrospectableBase(const IntrospectableBase&&) = delete;
    IntrospectableBase& operator=(const IntrospectableBase&&) = delete;
    /**
    * \}
    */

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
    virtual StringView getString() const override;
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    // convenience conversions
    virtual int64_t toInt() const override;
    virtual uint64_t toUInt() const override;
    virtual double toDouble() const override;
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const override;

    // TODO[Mi-L@]: Do NOT override here if there will be no reflectable when -withoutWriterCode is used!
    virtual void write(zserio::BitStreamWriter& writer) override;

private:
    const ITypeInfo& m_typeInfo;
};

/**
 * Base class for all builtin introspectables.
 */
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

/**
 * Specialization of the BuiltinIntrospectableBase base class for numeric (arithmetic) types.
 *
 * Hold the value instead of reference.
 */
template <typename ALLOC, typename T>
class BuiltinIntrospectableBase<ALLOC, T,
        typename std::enable_if<std::is_arithmetic<T>::value || std::is_same<T, StringView>::value>::type> :
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

/**
 * Base class for integral introspectables.
 *
 * Implements toString() and toDouble() conversions, implements write() for all integral builtin types.
 *
 * Hold dynamic bit size even though it has sense only for dynamic bit fields (otherwise it's always set to 0).
 * This solution was chosen for simplicity.
 */
template <typename ALLOC, typename T>
class IntegralIntrospectableBase : public BuiltinIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_integral<T>::value, "T must be a signed integral type!");

    using Base = BuiltinIntrospectableBase<ALLOC, T>;

public:
    IntegralIntrospectableBase(const ITypeInfo& typeInfo, T value, uint8_t dynamicBitSize) :
            Base(typeInfo, value), m_dynamicBitSize(dynamicBitSize)
    {}

    virtual double toDouble() const override
    {
        return static_cast<double>(Base::getValue());
    }

    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return ::zserio::toString<RebindAlloc<ALLOC, char>>(Base::getValue(), allocator);
    }

    virtual void write(BitStreamWriter& writer) override
    {
        const ITypeInfo& typeInfo = Base::getTypeInfo();
        switch (typeInfo.getSchemaType())
        {
        case SchemaType::BOOL:
            writer.writeBool(static_cast<bool>(Base::getValue()));
            break;
        case SchemaType::INT8:
            writer.writeSignedBits(static_cast<int8_t>(Base::getValue()), 8);
            break;
        case SchemaType::INT16:
            writer.writeSignedBits(static_cast<int16_t>(Base::getValue()), 16);
            break;
        case SchemaType::INT32:
            writer.writeSignedBits(static_cast<int32_t>(Base::getValue()), 32);
            break;
        case SchemaType::INT64:
            writer.writeSignedBits64(static_cast<int64_t>(Base::getValue()), 64);
            break;
        case SchemaType::UINT8:
            writer.writeBits(static_cast<uint8_t>(Base::getValue()), 8);
            break;
        case SchemaType::UINT16:
            writer.writeBits(static_cast<uint16_t>(Base::getValue()), 16);
            break;
        case SchemaType::UINT32:
            writer.writeBits(static_cast<uint32_t>(Base::getValue()), 32);
            break;
        case SchemaType::UINT64:
            writer.writeBits64(static_cast<uint64_t>(Base::getValue()), 64);
            break;
        case SchemaType::VARINT16:
            writer.writeVarInt16(static_cast<int16_t>(Base::getValue()));
            break;
        case SchemaType::VARINT32:
            writer.writeVarInt32(static_cast<int32_t>(Base::getValue()));
            break;
        case SchemaType::VARINT64:
            writer.writeVarInt64(static_cast<int64_t>(Base::getValue()));
            break;
        case SchemaType::VARINT:
            writer.writeVarInt(static_cast<int64_t>(Base::getValue()));
            break;
        case SchemaType::VARUINT16:
            writer.writeVarUInt16(static_cast<uint16_t>(Base::getValue()));
            break;
        case SchemaType::VARUINT32:
            writer.writeVarUInt32(static_cast<uint32_t>(Base::getValue()));
            break;
        case SchemaType::VARUINT64:
            writer.writeVarUInt64(static_cast<uint64_t>(Base::getValue()));
            break;
        case SchemaType::VARUINT:
            writer.writeVarUInt(static_cast<uint64_t>(Base::getValue()));
            break;
        case SchemaType::VARSIZE:
            writer.writeVarSize(static_cast<uint32_t>(Base::getValue()));
            break;
        case SchemaType::FIXED_SIGNED_BITFIELD:
            switch (typeInfo.getCppType())
            {
            case CppType::INT8:
            case CppType::INT16:
            case CppType::INT32:
                writer.writeSignedBits(static_cast<int32_t>(Base::getValue()), typeInfo.getBitSize());
                break;
            case CppType::INT64:
                writer.writeSignedBits64(static_cast<int64_t>(Base::getValue()), typeInfo.getBitSize());
                break;
            default:
                throw CppRuntimeException("IntegralIntrospectableBase::write - "
                        "Unexpected type for fixed signed bitfield!");
            }
            break;
        case SchemaType::FIXED_UNSIGNED_BITFIELD:
            switch (typeInfo.getCppType())
            {
            case CppType::UINT8:
            case CppType::UINT16:
            case CppType::UINT32:
                writer.writeBits(static_cast<uint32_t>(Base::getValue()), typeInfo.getBitSize());
                break;
            case CppType::UINT64:
                writer.writeBits64(static_cast<uint64_t>(Base::getValue()), typeInfo.getBitSize());
                break;
            default:
                throw CppRuntimeException("IntegralIntrospectableBase::write - "
                        "Unexpected type for fixed unsigned bitfield!");
            }
            break;
        case SchemaType::DYNAMIC_SIGNED_BITFIELD:
            switch (typeInfo.getCppType())
            {
            case CppType::INT8:
            case CppType::INT16:
            case CppType::INT32:
                writer.writeSignedBits(static_cast<int32_t>(Base::getValue()), m_dynamicBitSize);
                break;
            case CppType::INT64:
                writer.writeSignedBits64(static_cast<int64_t>(Base::getValue()), m_dynamicBitSize);
                break;
            default:
                throw CppRuntimeException("IntegralIntrospectableBase::write - "
                        "Unexpected type for dynamic signed bitfield!");
            }
            break;
        case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
            switch (typeInfo.getCppType())
            {
            case CppType::UINT8:
            case CppType::UINT16:
            case CppType::UINT32:
                writer.writeBits(static_cast<uint32_t>(Base::getValue()), m_dynamicBitSize);
                break;
            case CppType::UINT64:
                writer.writeBits64(static_cast<uint64_t>(Base::getValue()), m_dynamicBitSize);
                break;
            default:
                throw CppRuntimeException("IntegralIntrospectableBase::write - "
                        "Unexpected type for dynamic unsigned bitfield!");
            }
            break;
        default:
            throw CppRuntimeException("IntegralIntrospectableBase::write - Unexpected integral type!");
        }
    }

private:
    uint8_t m_dynamicBitSize;
};

/**
 * Base class for signed integral introspectables.
 *
 * Implements toInt() conversion.
 */
template <typename ALLOC, typename T>
class SignedIntrospectableBase : public IntegralIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_signed<T>::value, "T must be a signed integral type!");

    using IntegralIntrospectableBase<ALLOC, T>::IntegralIntrospectableBase;
    using Base = IntegralIntrospectableBase<ALLOC, T>;

public:
    virtual int64_t toInt() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for unsigned integral introspectables.
 *
 * Implements toUInt() conversion.
 */
template <typename ALLOC, typename T>
class UnsignedIntrospectableBase : public IntegralIntrospectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_unsigned<T>::value, "T must be an unsigned integral type!");

    using IntegralIntrospectableBase<ALLOC, T>::IntegralIntrospectableBase;
    using Base = IntegralIntrospectableBase<ALLOC, T>;

public:
    virtual uint64_t toUInt() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of bool type.
 */
template <typename ALLOC>
class BoolIntrospectable : public UnsignedIntrospectableBase<ALLOC, bool>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, bool>;

public:
    BoolIntrospectable(const ITypeInfo& typeInfo, bool value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual bool getBool() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of int8_t type.
 */
template <typename ALLOC>
class Int8Introspectable : public SignedIntrospectableBase<ALLOC, int8_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int8_t>;

public:
    Int8Introspectable(const ITypeInfo& typeInfo, int8_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int8_t getInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of int16_t type.
 */
template <typename ALLOC>
class Int16Introspectable : public SignedIntrospectableBase<ALLOC, int16_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int16_t>;

public:
    Int16Introspectable(const ITypeInfo& typeInfo, int16_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int16_t getInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of int32_t type.
 */
template <typename ALLOC>
class Int32Introspectable : public SignedIntrospectableBase<ALLOC, int32_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int32_t>;

public:
    Int32Introspectable(const ITypeInfo& typeInfo, int32_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int32_t getInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of int64_t type.
 */
template <typename ALLOC>
class Int64Introspectable : public SignedIntrospectableBase<ALLOC, int64_t>
{
private:
    using Base = SignedIntrospectableBase<ALLOC, int64_t>;

public:
    Int64Introspectable(const ITypeInfo& typeInfo, int64_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int64_t getInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of uint8_t type.
 */
template <typename ALLOC>
class UInt8Introspectable : public UnsignedIntrospectableBase<ALLOC, uint8_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint8_t>;

public:
    UInt8Introspectable(const ITypeInfo& typeInfo, uint8_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint8_t getUInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of uint16_t type.
 */
template <typename ALLOC>
class UInt16Introspectable : public UnsignedIntrospectableBase<ALLOC, uint16_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint16_t>;

public:
    UInt16Introspectable(const ITypeInfo& typeInfo, uint16_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint16_t getUInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of uint32_t type.
 */
template <typename ALLOC>
class UInt32Introspectable : public UnsignedIntrospectableBase<ALLOC, uint32_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint32_t>;

public:
    UInt32Introspectable(const ITypeInfo& typeInfo, uint32_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint32_t getUInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Introspectable for values of uint64_t type.
 */
template <typename ALLOC>
class UInt64Introspectable : public UnsignedIntrospectableBase<ALLOC, uint64_t>
{
private:
    using Base = UnsignedIntrospectableBase<ALLOC, uint64_t>;

public:
    UInt64Introspectable(const ITypeInfo& typeInfo, uint64_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint64_t getUInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for floating point introspectables.
 */
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
        return static_cast<double>(getValue());
    }
};

/**
 * Introspectable for values of float type.
 */
template <typename ALLOC>
class FloatIntrospectable : public FloatingPointIntrospectableBase<ALLOC, float>
{
private:
    using Base = FloatingPointIntrospectableBase<ALLOC, float>;

public:
    FloatIntrospectable(const ITypeInfo& typeInfo, float value) :
            Base(typeInfo, value)
    {}

    virtual float getFloat() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) override
    {
        if (Base::getTypeInfo().getSchemaType() == SchemaType::FLOAT16)
            writer.writeFloat16(Base::getValue());
        else
            writer.writeFloat32(Base::getValue());
    }
};

/**
 * Introspectable for values of double type.
 */
template <typename ALLOC>
class DoubleIntrospectable : public FloatingPointIntrospectableBase<ALLOC, double>
{
private:
    using Base = FloatingPointIntrospectableBase<ALLOC, double>;

public:
    DoubleIntrospectable(const ITypeInfo& typeInfo, double value) :
            Base(typeInfo, value)
    {}

    virtual double getDouble() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) override
    {
        writer.writeFloat64(Base::getValue());
    }
};

/**
 * Introspectable for values of string type.
 */
template <typename ALLOC>
class StringIntrospectable : public BuiltinIntrospectableBase<ALLOC, StringView>
{
private:
    using Base = BuiltinIntrospectableBase<ALLOC, StringView>;

public:
    explicit StringIntrospectable(const ITypeInfo& typeInfo, StringView value) :
            Base(typeInfo, value)
    {}

    virtual StringView getString() const override
    {
        return Base::getValue();
    }

    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return stringViewToString(getString(), allocator);
    }

    virtual void write(BitStreamWriter& writer) override
    {
        writer.writeString(Base::getValue());
    }
};

/**
 * Introspectable for values of bit buffer type.
 */
template <typename ALLOC>
class BitBufferIntrospectable : public BuiltinIntrospectableBase<ALLOC, BasicBitBuffer<ALLOC>>
{
private:
    using Base = BuiltinIntrospectableBase<ALLOC, BasicBitBuffer<ALLOC>>;

public:
    explicit BitBufferIntrospectable(const ITypeInfo& typeInfo, const BasicBitBuffer<ALLOC>& value) :
            Base(typeInfo, value)
    {}

    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) override
    {
        writer.writeBitBuffer(Base::getValue());
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
    using Type = BoolIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int8_t>
{
    using Type = Int8Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int16_t>
{
    using Type = Int16Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int32_t>
{
    using Type = Int32Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, int64_t>
{
    using Type = Int64Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint8_t>
{
    using Type = UInt8Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint16_t>
{
    using Type = UInt16Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint32_t>
{
    using Type = UInt32Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, uint64_t>
{
    using Type = UInt64Introspectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, float>
{
    using Type = FloatIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, double>
{
    using Type = DoubleIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, string<RebindAlloc<ALLOC, char>>>
{
    using Type = StringIntrospectable<ALLOC>;
};

template <typename ALLOC>
struct IntrospectableTraits<ALLOC, BasicBitBuffer<ALLOC>>
{
    using Type = BitBufferIntrospectable<ALLOC>;
};

} // namespace detail

// TODO[Mi-L@]: Check if multiple inheritance is ok!
/**
 * Base class for introspectable which needs to hold an allocator.
 */
template <typename ALLOC>
class IntrospectableAllocatorHolderBase : public IntrospectableBase<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    IntrospectableAllocatorHolderBase(const ITypeInfo& typeInfo, const ALLOC& allocator) :
            IntrospectableBase<ALLOC>(typeInfo), AllocatorHolder<ALLOC>(allocator)
    {}
};

/**
 * Base class for arrays introspectables.
 *
 * Overrides all generic methods with default throw behaviour.
 */
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
    virtual StringView getString() const override;
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    virtual int64_t toInt() const override;
    virtual uint64_t toUInt() const override;
    virtual double toDouble() const override;
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const override;

    virtual void write(BitStreamWriter& writer) override;
};

/**
 * Introspetable for arrays of builtin types (except integral types).
 */
template <typename ALLOC, typename RAW_ARRAY>
class BuiltinIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;

    using BuiltinIntrospectable =
            typename detail::IntrospectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    BuiltinIntrospectableArray(const ITypeInfo& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return std::allocate_shared<BuiltinIntrospectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index));
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

/**
 * Introspectable for arrays of builtin integral types.
 *
 * Holds dynamic bit size even for types where it has no sense (in that case it's 0).
 * This solution was chosen for simplicity.
 */
template <typename ALLOC, typename RAW_ARRAY>
class IntegralIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;

    using IntegralIntrospectable =
            typename detail::IntrospectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    IntegralIntrospectableArray(const ITypeInfo& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray, uint8_t dynamicBitSize) :
            Base(typeInfo, allocator), m_rawArray(rawArray), m_dynamicBitSize(dynamicBitSize)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return std::allocate_shared<IntegralIntrospectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index), m_dynamicBitSize);
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
    uint8_t m_dynamicBitSize;
};

/**
 * Introspectable for arrays of compound types.
 */
template <typename ALLOC, typename RAW_ARRAY>
class CompoundIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    CompoundIntrospectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return m_rawArray.at(index).introspectable(Base::get_allocator());
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    RAW_ARRAY& m_rawArray;
};

/** Introspectable for arrays of bitmask types. */
template <typename ALLOC, typename RAW_ARRAY>
using BitmaskIntrospectableArray = CompoundIntrospectableArray<ALLOC, RAW_ARRAY>;

/** Introspectable for arrays of enum types. */
template <typename ALLOC, typename RAW_ARRAY>
class EnumIntrospectableArray : public IntrospectableArrayBase<ALLOC>
{
private:
    using Base = IntrospectableArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    EnumIntrospectableArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType>(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr at(size_t index) const override
    {
        return enumIntrospectable(m_rawArray.at(index), Base::get_allocator());
    }

    virtual typename IBasicIntrospectable<ALLOC>::Ptr operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

/**
 * Factory used to make it easier to create introspectable instances.
 *
 * Creates introspectables for all builtin types and for arrays.
 */
template <typename ALLOC>
class BasicIntrospectableFactory
{
public:
    static typename IBasicIntrospectable<ALLOC>::Ptr getBool(bool value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BoolIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getBool(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt8(int8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt8(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt16(int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt16(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt32(int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt32(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getInt64(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getInt64(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt8(uint8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt8(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt16(uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt16(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt32(uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt32(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getUInt64(uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getUInt64(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt16(
            int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt16(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt32(
            int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt32(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt64(
            int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt64(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarInt(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarInt(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt16(
            uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt16(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt32(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt32(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt64(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt64(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarUInt(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarUInt(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getVarSize(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getVarSize(), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat16(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FloatIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat16(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat32(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FloatIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat32(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFloat64(double value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DoubleIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFloat64(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getString(
            const string<RebindAlloc<ALLOC, char>>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getString(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getString(
            StringView value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getString(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getBitBuffer(
            const BasicBitBuffer<ALLOC>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitBufferIntrospectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getBitBuffer(), value);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<Int8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<Int32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedSignedBitField(
            uint8_t bitSize, int64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int64_t' value!";
        }
        return std::allocate_shared<Int64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedSignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<UInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<UInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<UInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getFixedUnsignedBitField(
            uint8_t bitSize, uint64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<UInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getFixedUnsignedBitField(bitSize), value, 0);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int8_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<Int8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int16_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int32_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<Int32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicSignedBitField(
            uint8_t maxBitSize, int64_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint8_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<UInt8Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint16_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<UInt16Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint32_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<UInt32Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static typename IBasicIntrospectable<ALLOC>::Ptr getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint64_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<UInt64Introspectable<ALLOC>>(
                allocator, BuiltinTypeInfo::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<!std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBuiltinArray(
            const ITypeInfo& typeInfo, const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BuiltinIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBuiltinArray(
            const ITypeInfo& typeInfo, const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() == SchemaType::DYNAMIC_SIGNED_BITFIELD ||
                typeInfo.getSchemaType() == SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getBuiltinArray") +
                    " - dynamic bit field array must be created with dynamicBitSize argument!";
        }

        return std::allocate_shared<IntegralIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, 0);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBuiltinArray(
            const ITypeInfo& typeInfo, const RAW_ARRAY& rawArray, uint8_t dynamicBitSize,
            const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() != SchemaType::DYNAMIC_SIGNED_BITFIELD &&
                typeInfo.getSchemaType() != SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicIntrospectableFactory::getBuiltinArray") +
                    " - expected a dynamic bit field array!";
        }

        return std::allocate_shared<IntegralIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getCompoundArray(
            RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<CompoundIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getBitmaskArray(
            RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitmaskIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static typename IBasicIntrospectable<ALLOC>::Ptr getEnumArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<EnumIntrospectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }
};

/** Typedef to the introspectable factroy provided for convenience - using default std::allocator<uint8_t>. */
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
StringView IntrospectableBase<ALLOC>::getString() const
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
void IntrospectableBase<ALLOC>::write(BitStreamWriter&)
{
    throw CppRuntimeException("Cannot write '") + getTypeInfo().getSchemaName() + "'! " +
            "Ensure that -withWriterCode option is used!";
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
StringView IntrospectableArrayBase<ALLOC>::getString() const
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

template <typename ALLOC>
void IntrospectableArrayBase<ALLOC>::write(BitStreamWriter&)
{
    throw CppRuntimeException("Introspectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

} // namespace zserio

#endif // ZSERIO_INTROSPECTABLE_H_INC
