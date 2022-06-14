#ifndef ZSERIO_REFLECTABLE_H_INC
#define ZSERIO_REFLECTABLE_H_INC

#include <type_traits>

#include "zserio/AllocatorHolder.h"
#include "zserio/IReflectable.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/TypeInfo.h"
#include "zserio/TypeInfoUtil.h"
#include "zserio/BitSizeOfCalculator.h"

namespace zserio
{

/**
 * Base class for all reflectable implementations.
 *
 * Implements the find() feature and overrides all generic methods except of write() and bitSizeOf() with
 * default throw behavior.
 */
template <typename ALLOC>
class ReflectableBase : public IBasicReflectable<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param typeInfo Type info of the reflected object.
     */
    explicit ReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo);

    /** Destructor. */
    virtual ~ReflectableBase() override = 0;

    /**
    * Copying and moving is disallowed!
    * \{
    */
    ReflectableBase(const ReflectableBase&) = delete;
    ReflectableBase& operator=(const ReflectableBase&) = delete;

    ReflectableBase(const ReflectableBase&&) = delete;
    ReflectableBase& operator=(const ReflectableBase&&) = delete;
    /**
    * \}
    */

    virtual const IBasicTypeInfo<ALLOC>& getTypeInfo() const override;
    virtual bool isArray() const override;

    virtual void initializeChildren() override;

    virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    virtual IBasicReflectablePtr<ALLOC> createField(StringView name) override;
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    virtual IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    virtual IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;

    virtual StringView getChoice() const override;

    virtual IBasicReflectableConstPtr<ALLOC> find(StringView path) const override;
    virtual IBasicReflectablePtr<ALLOC> find(StringView path) override;
    virtual IBasicReflectableConstPtr<ALLOC> operator[](StringView path) const override;
    virtual IBasicReflectablePtr<ALLOC> operator[](StringView path) override;

    virtual size_t size() const override;
    virtual void resize(size_t size) override;
    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override;
    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override;
    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override;
    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override;
    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override;
    virtual void append(const AnyHolder<ALLOC>& value) override;

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
    virtual string<ALLOC> toString(const ALLOC& allocator = ALLOC()) const override;

private:
    const IBasicTypeInfo<ALLOC>& m_typeInfo;
};

/**
 * Base class for all builtin reflectables.
 */
template <typename ALLOC, typename T, typename = void>
class BuiltinReflectableBase : public ReflectableBase<ALLOC>
{
private:
    using Base = ReflectableBase<ALLOC>;

protected:
    BuiltinReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo, const T& value) :
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
 * Specialization of the BuiltinReflectableBase base class for numeric (arithmetic) types.
 *
 * Hold the value instead of reference.
 */
template <typename ALLOC, typename T>
class BuiltinReflectableBase<ALLOC, T,
        typename std::enable_if<std::is_arithmetic<T>::value || std::is_same<T, StringView>::value>::type> :
                public ReflectableBase<ALLOC>
{
private:
    using Base = ReflectableBase<ALLOC>;

protected:
    BuiltinReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo, T value) :
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
 * Base class for integral reflectables.
 *
 * Implements toString() and toDouble() conversions, implements write() for all integral builtin types.
 *
 * Hold dynamic bit size even though it has sense only for dynamic bit fields (otherwise it's always set to 0).
 * This solution was chosen for simplicity.
 */
template <typename ALLOC, typename T>
class IntegralReflectableBase : public BuiltinReflectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_integral<T>::value, "T must be a signed integral type!");

    using Base = BuiltinReflectableBase<ALLOC, T>;

public:
    IntegralReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo, T value, uint8_t dynamicBitSize) :
            Base(typeInfo, value), m_dynamicBitSize(dynamicBitSize)
    {}

    virtual double toDouble() const override
    {
        return static_cast<double>(Base::getValue());
    }

    virtual string<ALLOC> toString(const ALLOC& allocator) const override
    {
        return ::zserio::toString<ALLOC>(Base::getValue(), allocator);
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        const IBasicTypeInfo<ALLOC>& typeInfo = Base::getTypeInfo();
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
                throw CppRuntimeException("IntegralReflectableBase::write - "
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
                throw CppRuntimeException("IntegralReflectableBase::write - "
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
                throw CppRuntimeException("IntegralReflectableBase::write - "
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
                throw CppRuntimeException("IntegralReflectableBase::write - "
                        "Unexpected type for dynamic unsigned bitfield!");
            }
            break;
        default:
            throw CppRuntimeException("IntegralReflectableBase::write - Unexpected integral type!");
        }
    }

    virtual size_t bitSizeOf(size_t) const override
    {
        const IBasicTypeInfo<ALLOC>& typeInfo = Base::getTypeInfo();
        switch (typeInfo.getSchemaType())
        {
        case SchemaType::BOOL:
            return 1;
        case SchemaType::INT8:
            return 8;
        case SchemaType::INT16:
            return 16;
        case SchemaType::INT32:
            return 32;
        case SchemaType::INT64:
            return 64;
        case SchemaType::UINT8:
            return 8;
        case SchemaType::UINT16:
            return 16;
        case SchemaType::UINT32:
            return 32;
        case SchemaType::UINT64:
            return 64;
        case SchemaType::VARINT16:
            return zserio::bitSizeOfVarInt16(static_cast<int16_t>(Base::getValue()));
        case SchemaType::VARINT32:
            return zserio::bitSizeOfVarInt32(static_cast<int32_t>(Base::getValue()));
        case SchemaType::VARINT64:
            return zserio::bitSizeOfVarInt64(static_cast<int64_t>(Base::getValue()));
        case SchemaType::VARINT:
            return zserio::bitSizeOfVarInt(static_cast<int64_t>(Base::getValue()));
        case SchemaType::VARUINT16:
            return zserio::bitSizeOfVarUInt16(static_cast<uint16_t>(Base::getValue()));
        case SchemaType::VARUINT32:
            return zserio::bitSizeOfVarUInt32(static_cast<uint32_t>(Base::getValue()));
        case SchemaType::VARUINT64:
            return zserio::bitSizeOfVarUInt64(static_cast<uint64_t>(Base::getValue()));
        case SchemaType::VARUINT:
            return zserio::bitSizeOfVarUInt(static_cast<uint64_t>(Base::getValue()));
        case SchemaType::VARSIZE:
            return zserio::bitSizeOfVarSize(static_cast<uint32_t>(Base::getValue()));
        case SchemaType::FIXED_SIGNED_BITFIELD:
            return typeInfo.getBitSize();
        case SchemaType::FIXED_UNSIGNED_BITFIELD:
            return typeInfo.getBitSize();
        case SchemaType::DYNAMIC_SIGNED_BITFIELD:
            return m_dynamicBitSize;
        case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
            return m_dynamicBitSize;
        default:
            throw CppRuntimeException("IntegralReflectableBase::bitSizeOf - Unexpected integral type!");
        }
    }

private:
    uint8_t m_dynamicBitSize;
};

/**
 * Base class for signed integral reflectables.
 *
 * Implements toInt() conversion.
 */
template <typename ALLOC, typename T>
class SignedReflectableBase : public IntegralReflectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_signed<T>::value, "T must be a signed integral type!");

    using Base = IntegralReflectableBase<ALLOC, T>;

    using Base::IntegralReflectableBase;

public:
    virtual int64_t toInt() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for unsigned integral reflectables.
 *
 * Implements toUInt() conversion.
 */
template <typename ALLOC, typename T>
class UnsignedReflectableBase : public IntegralReflectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_unsigned<T>::value, "T must be an unsigned integral type!");

    using Base = IntegralReflectableBase<ALLOC, T>;

    using Base::IntegralReflectableBase;

public:
    virtual uint64_t toUInt() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of bool type.
 */
template <typename ALLOC>
class BoolReflectable : public UnsignedReflectableBase<ALLOC, bool>
{
private:
    using Base = UnsignedReflectableBase<ALLOC, bool>;

public:
    BoolReflectable(const IBasicTypeInfo<ALLOC>& typeInfo, bool value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual bool getBool() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of int8_t type.
 */
template <typename ALLOC>
class Int8Reflectable : public SignedReflectableBase<ALLOC, int8_t>
{
private:
    using Base = SignedReflectableBase<ALLOC, int8_t>;

public:
    Int8Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, int8_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int8_t getInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of int16_t type.
 */
template <typename ALLOC>
class Int16Reflectable : public SignedReflectableBase<ALLOC, int16_t>
{
private:
    using Base = SignedReflectableBase<ALLOC, int16_t>;

public:
    Int16Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, int16_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int16_t getInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of int32_t type.
 */
template <typename ALLOC>
class Int32Reflectable : public SignedReflectableBase<ALLOC, int32_t>
{
private:
    using Base = SignedReflectableBase<ALLOC, int32_t>;

public:
    Int32Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, int32_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int32_t getInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of int64_t type.
 */
template <typename ALLOC>
class Int64Reflectable : public SignedReflectableBase<ALLOC, int64_t>
{
private:
    using Base = SignedReflectableBase<ALLOC, int64_t>;

public:
    Int64Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, int64_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual int64_t getInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of uint8_t type.
 */
template <typename ALLOC>
class UInt8Reflectable : public UnsignedReflectableBase<ALLOC, uint8_t>
{
private:
    using Base = UnsignedReflectableBase<ALLOC, uint8_t>;

public:
    UInt8Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, uint8_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint8_t getUInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of uint16_t type.
 */
template <typename ALLOC>
class UInt16Reflectable : public UnsignedReflectableBase<ALLOC, uint16_t>
{
private:
    using Base = UnsignedReflectableBase<ALLOC, uint16_t>;

public:
    UInt16Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, uint16_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint16_t getUInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of uint32_t type.
 */
template <typename ALLOC>
class UInt32Reflectable : public UnsignedReflectableBase<ALLOC, uint32_t>
{
private:
    using Base = UnsignedReflectableBase<ALLOC, uint32_t>;

public:
    UInt32Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, uint32_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint32_t getUInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of uint64_t type.
 */
template <typename ALLOC>
class UInt64Reflectable : public UnsignedReflectableBase<ALLOC, uint64_t>
{
private:
    using Base = UnsignedReflectableBase<ALLOC, uint64_t>;

public:
    UInt64Reflectable(const IBasicTypeInfo<ALLOC>& typeInfo, uint64_t value, uint8_t dynamicBitSize) :
            Base(typeInfo, value, dynamicBitSize)
    {}

    virtual uint64_t getUInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for floating point reflectables.
 */
template <typename ALLOC, typename T>
class FloatingPointReflectableBase : public BuiltinReflectableBase<ALLOC, T>
{
protected:
    static_assert(std::is_floating_point<T>::value, "T must be a floating point type!");

    using BuiltinReflectableBase<ALLOC, T>::getValue;
    using BuiltinReflectableBase<ALLOC, T>::BuiltinReflectableBase;

public:
    virtual double toDouble() const override
    {
        return static_cast<double>(getValue());
    }
};

/**
 * Reflectable for values of float type.
 */
template <typename ALLOC>
class FloatReflectable : public FloatingPointReflectableBase<ALLOC, float>
{
private:
    using Base = FloatingPointReflectableBase<ALLOC, float>;

public:
    FloatReflectable(const IBasicTypeInfo<ALLOC>& typeInfo, float value) :
            Base(typeInfo, value)
    {}

    virtual float getFloat() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        if (Base::getTypeInfo().getSchemaType() == SchemaType::FLOAT16)
            writer.writeFloat16(Base::getValue());
        else
            writer.writeFloat32(Base::getValue());
    }

    virtual size_t bitSizeOf(size_t) const override
    {
        if (Base::getTypeInfo().getSchemaType() == SchemaType::FLOAT16)
            return 16;
        else
            return 32;
    }
};

/**
 * Reflectable for values of double type.
 */
template <typename ALLOC>
class DoubleReflectable : public FloatingPointReflectableBase<ALLOC, double>
{
private:
    using Base = FloatingPointReflectableBase<ALLOC, double>;

public:
    DoubleReflectable(const IBasicTypeInfo<ALLOC>& typeInfo, double value) :
            Base(typeInfo, value)
    {}

    virtual double getDouble() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        writer.writeFloat64(Base::getValue());
    }

    virtual size_t bitSizeOf(size_t) const override
    {
        return 64;
    }
};

/**
 * Reflectable for values of string type.
 */
template <typename ALLOC>
class StringReflectable : public BuiltinReflectableBase<ALLOC, StringView>
{
private:
    using Base = BuiltinReflectableBase<ALLOC, StringView>;

public:
    explicit StringReflectable(const IBasicTypeInfo<ALLOC>& typeInfo, StringView value) :
            Base(typeInfo, value)
    {}

    virtual StringView getString() const override
    {
        return Base::getValue();
    }

    virtual string<ALLOC> toString(const ALLOC& allocator) const override
    {
        return zserio::toString(getString(), allocator);
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        writer.writeString(Base::getValue());
    }

    virtual size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfString(Base::getValue());
    }
};

/**
 * Reflectable for values of bit buffer type.
 */
template <typename ALLOC>
class BitBufferReflectable : public BuiltinReflectableBase<ALLOC, BasicBitBuffer<ALLOC>>
{
private:
    using Base = BuiltinReflectableBase<ALLOC, BasicBitBuffer<ALLOC>>;

public:
    explicit BitBufferReflectable(const IBasicTypeInfo<ALLOC>& typeInfo, const BasicBitBuffer<ALLOC>& value) :
            Base(typeInfo, value)
    {}

    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override
    {
        return Base::getValue();
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        writer.writeBitBuffer(Base::getValue());
    }

    virtual size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfBitBuffer(Base::getValue());
    }
};

namespace detail
{

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> getFieldFromObject(const IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& fields = typeInfo.getFields();
        auto fieldsIt = std::find_if(fields.begin(), fields.end(),
                [name](const BasicFieldInfo<ALLOC>& fieldInfo) { return fieldInfo.schemaName == name; });
        if (fieldsIt != fields.end())
            return object.getField(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> getFieldFromObject(IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& fields = typeInfo.getFields();
        auto fieldsIt = std::find_if(fields.begin(), fields.end(),
                [name](const BasicFieldInfo<ALLOC>& fieldInfo) { return fieldInfo.schemaName == name; });
        if (fieldsIt != fields.end())
            return object.getField(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> getParameterFromObject(const IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& parameters = typeInfo.getParameters();
        auto parametersIt = std::find_if(parameters.begin(), parameters.end(),
                [name](const BasicParameterInfo<ALLOC>& parameterInfo)
                        { return parameterInfo.schemaName == name; });
        if (parametersIt != parameters.end())
            return object.getParameter(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> getParameterFromObject(IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& parameters = typeInfo.getParameters();
        auto parametersIt = std::find_if(parameters.begin(), parameters.end(),
                [name](const BasicParameterInfo<ALLOC>& parameterInfo)
                        { return parameterInfo.schemaName == name; });
        if (parametersIt != parameters.end())
            return object.getParameter(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> callFunctionInObject(const IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& functions = typeInfo.getFunctions();
        auto functionsIt = std::find_if(functions.begin(), functions.end(),
                [name](const BasicFunctionInfo<ALLOC>& functionInfo)
                        { return functionInfo.schemaName == name; });
        if (functionsIt != functions.end())
            return object.callFunction(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> callFunctionInObject(IBasicReflectable<ALLOC>& object, StringView name)
{
    const auto& typeInfo = object.getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        const auto& functions = typeInfo.getFunctions();
        auto functionsIt = std::find_if(functions.begin(), functions.end(),
                [name](const BasicFunctionInfo<ALLOC>& functionInfo) { return functionInfo.schemaName == name; });
        if (functionsIt != functions.end())
            return object.callFunction(name);
    }

    return nullptr;
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> getFromObject(
        const IBasicReflectable<ALLOC>& object, StringView path, size_t pos)
{
    try
    {
        const size_t dotPos = path.find('.', pos);
        const bool isLast = dotPos == StringView::npos;
        const StringView name = path.substr(pos, dotPos == StringView::npos ? StringView::npos : dotPos - pos);

        auto field = getFieldFromObject(object, name);
        if (field)
            return isLast ? field : getFromObject(*field, path, dotPos + 1);

        auto parameter = getParameterFromObject(object, name);
        if (parameter)
            return isLast ? parameter : getFromObject(*parameter, path, dotPos + 1);

        auto functionResult = callFunctionInObject(object, name);
        if (functionResult)
            return isLast ? functionResult : getFromObject(*functionResult, path, dotPos + 1);
    }
    catch (const CppRuntimeException&)
    {}

    return nullptr;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> getFromObject(IBasicReflectable<ALLOC>& object, StringView path, size_t pos)
{
    try
    {
        const size_t dotPos = path.find('.', pos);
        const bool isLast = dotPos == StringView::npos;
        const StringView name = path.substr(pos, dotPos == StringView::npos ? StringView::npos : dotPos - pos);

        auto field = getFieldFromObject(object, name);
        if (field)
            return isLast ? field : getFromObject(*field, path, dotPos + 1);

        auto parameter = getParameterFromObject(object, name);
        if (parameter)
            return isLast ? parameter : getFromObject(*parameter, path, dotPos + 1);

        auto functionResult = callFunctionInObject(object, name);
        if (functionResult)
            return isLast ? functionResult : getFromObject(*functionResult, path, dotPos + 1);
    }
    catch (const CppRuntimeException&)
    {}

    return nullptr;
}

template <typename ALLOC, typename T>
struct ReflectableTraits
{
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, bool>
{
    using Type = BoolReflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, int8_t>
{
    using Type = Int8Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, int16_t>
{
    using Type = Int16Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, int32_t>
{
    using Type = Int32Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, int64_t>
{
    using Type = Int64Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, uint8_t>
{
    using Type = UInt8Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, uint16_t>
{
    using Type = UInt16Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, uint32_t>
{
    using Type = UInt32Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, uint64_t>
{
    using Type = UInt64Reflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, float>
{
    using Type = FloatReflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, double>
{
    using Type = DoubleReflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, string<ALLOC>>
{
    using Type = StringReflectable<ALLOC>;
};

template <typename ALLOC>
struct ReflectableTraits<ALLOC, BasicBitBuffer<ALLOC>>
{
    using Type = BitBufferReflectable<ALLOC>;
};

} // namespace detail

/**
 * Base class for reflectable which needs to hold an allocator.
 */
template <typename ALLOC>
class ReflectableAllocatorHolderBase : public ReflectableBase<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    ReflectableAllocatorHolderBase(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator) :
            ReflectableBase<ALLOC>(typeInfo), AllocatorHolder<ALLOC>(allocator)
    {}
};

/**
 * Base class for constant reflectable which needs to hold an allocator.
 *
 * Overrides non constant methods and throws exception with info about constness.
 */
template <typename ALLOC>
class ReflectableConstAllocatorHolderBase : public ReflectableAllocatorHolderBase<ALLOC>
{
private:
    using Base = ReflectableAllocatorHolderBase<ALLOC>;

public:
    using Base::ReflectableAllocatorHolderBase;
    using Base::getTypeInfo;

    virtual void initializeChildren() override;
    virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    virtual IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    virtual IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;
};

/**
 * Base class for reflectable arrays.
 *
 * Overrides all generic methods with default throw behaviour.
 */
template <typename ALLOC>
class ReflectableArrayBase : public ReflectableAllocatorHolderBase<ALLOC>
{
private:
    using Base = ReflectableAllocatorHolderBase<ALLOC>;

public:
    using Base::ReflectableAllocatorHolderBase;
    using Base::getTypeInfo;

    virtual bool isArray() const override
    {
        return true;
    }

    virtual void initializeChildren() override;

    virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    virtual IBasicReflectablePtr<ALLOC> createField(StringView name) override;
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    virtual IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    virtual IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override;
    virtual IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;

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
    virtual string<ALLOC> toString(const ALLOC& allocator = ALLOC()) const override;

    virtual void write(BitStreamWriter& writer) const override;
    virtual size_t bitSizeOf(size_t) const override;
};

/**
 * Base class for constant reflectable arrays.
 *
 * Overrides non constant methods and throws exception with info about constness.
 */
template <typename ALLOC>
class ReflectableConstArrayBase : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

public:
    using Base::ReflectableArrayBase;
    using Base::getTypeInfo;

    virtual void resize(size_t index) override;
    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override;
    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override;
    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override;
    virtual void append(const AnyHolder<ALLOC>& value) override;
};

/**
 * Reflectable for arrays of builtin types (except integral types).
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
class BuiltinReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using BuiltinReflectable =
            typename detail::ReflectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    using Base::at;
    using Base::operator[];

    BuiltinReflectableConstArray(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return std::allocate_shared<BuiltinReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index));
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class BuiltinReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using BuiltinReflectable =
            typename detail::ReflectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    BuiltinReflectableArray(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(typeInfo, allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return std::allocate_shared<BuiltinReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index));
    }

    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return std::allocate_shared<BuiltinReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index));
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return at(index);
    }

    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_rawArray.at(index) = value.template get<typename RAW_ARRAY::value_type>();
    }

    virtual void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/**
 * Reflectable for arrays of builtin integral types.
 *
 * Holds dynamic bit size even for types where it has no sense (in that case it's 0).
 * This solution was chosen for simplicity.
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
class IntegralReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using IntegralReflectable =
            typename detail::ReflectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    using Base::at;
    using Base::operator[];

    IntegralReflectableConstArray(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator,
            const RAW_ARRAY& rawArray, uint8_t dynamicBitSize) :
            Base(typeInfo, allocator), m_rawArray(rawArray), m_dynamicBitSize(dynamicBitSize)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return std::allocate_shared<IntegralReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index), m_dynamicBitSize);
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC, typename RAW_ARRAY>
class IntegralReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using IntegralReflectable =
            typename detail::ReflectableTraits<ALLOC, typename RAW_ARRAY::value_type>::Type;

public:
    IntegralReflectableArray(const IBasicTypeInfo<ALLOC>& typeInfo, const ALLOC& allocator,
            RAW_ARRAY& rawArray, uint8_t dynamicBitSize) :
            Base(typeInfo, allocator), m_rawArray(rawArray), m_dynamicBitSize(dynamicBitSize)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return std::allocate_shared<IntegralReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index), m_dynamicBitSize);
    }

    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return std::allocate_shared<IntegralReflectable>(
                Base::get_allocator(), Base::getTypeInfo(), m_rawArray.at(index), m_dynamicBitSize);
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return at(index);
    }

    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_rawArray.at(index) = value.template get<typename RAW_ARRAY::value_type>();
    }

    virtual void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

private:
    RAW_ARRAY& m_rawArray;
    uint8_t m_dynamicBitSize;
};
/** \} */

/**
 * Reflectable for arrays of compound types.
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
class CompoundReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    using Base::at;
    using Base::operator[];

    CompoundReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return m_rawArray.at(index).reflectable(Base::get_allocator());
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class CompoundReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    CompoundReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return m_rawArray.at(index).reflectable(Base::get_allocator());
    }

    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return m_rawArray.at(index).reflectable(Base::get_allocator());
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return at(index);
    }

    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_rawArray.at(index) = value.template get<typename RAW_ARRAY::value_type>();
    }

    virtual void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/** Reflectable for arrays of bitmask types. */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
using BitmaskReflectableConstArray = CompoundReflectableConstArray<ALLOC, RAW_ARRAY>;

template <typename ALLOC, typename RAW_ARRAY>
using BitmaskReflectableArray = CompoundReflectableArray<ALLOC, RAW_ARRAY>;
/** \} */

/** Reflectable for arrays of enum types. */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
class EnumReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    using Base::at;
    using Base::operator[];

    EnumReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType>(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return enumReflectable(m_rawArray.at(index), Base::get_allocator());
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class EnumReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    EnumReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType>(), allocator), m_rawArray(rawArray)
    {}

    virtual size_t size() const override
    {
        return m_rawArray.size();
    }

    virtual void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return enumReflectable(m_rawArray.at(index), Base::get_allocator());
    }

    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return enumReflectable(m_rawArray.at(index), Base::get_allocator());
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return at(index);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return at(index);
    }

    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_rawArray.at(index) = value.template get<typename RAW_ARRAY::value_type>();
    }

    virtual void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/**
 * Wrapper around reflectable which actually owns the reflected object.
 *
 * This is needed in ZserioTreeCreator to be able to generically create the new instance of a zserio object.
 */
template <typename T, typename ALLOC = typename T::allocator_type>
class ReflectableOwner : public IBasicReflectable<ALLOC>
{
public:
    ReflectableOwner(const ALLOC& allocator = ALLOC()) :
            m_object(allocator),
            m_reflectable(m_object.reflectable(allocator))
    {}

    virtual const IBasicTypeInfo<ALLOC>& getTypeInfo() const override
    {
        return m_reflectable->getTypeInfo();
    }

    virtual bool isArray() const override
    {
        return m_reflectable->isArray();
    }

    virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override
    {
        return m_reflectable->getField(name);
    }

    virtual IBasicReflectablePtr<ALLOC> getField(StringView name) override
    {
        return m_reflectable->getField(name);
    }

    virtual IBasicReflectablePtr<ALLOC> createField(StringView name) override
    {
        return m_reflectable->createField(name);
    }

    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) override
    {
        m_reflectable->setField(name, value);
    }

    virtual IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override
    {
        return m_reflectable->getParameter(name);
    }

    virtual IBasicReflectablePtr<ALLOC> getParameter(StringView name) override
    {
        return m_reflectable->getParameter(name);
    }

    virtual IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override
    {
        return m_reflectable->callFunction(name);
    }

    virtual IBasicReflectablePtr<ALLOC> callFunction(StringView name) override
    {
        return m_reflectable->callFunction(name);
    }

    virtual StringView getChoice() const override
    {
        return m_reflectable->getChoice();
    }

    virtual IBasicReflectableConstPtr<ALLOC> find(StringView path) const override
    {
        return m_reflectable->find(path);
    }

    virtual IBasicReflectablePtr<ALLOC> find(StringView path) override
    {
        return m_reflectable->find(path);
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](StringView path) const override
    {
        return m_reflectable->operator[](path);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](StringView path) override
    {
        return m_reflectable->operator[](path);
    }

    virtual size_t size() const override
    {
        return m_reflectable->size();
    }

    virtual void resize(size_t size) override
    {
        m_reflectable->resize(size);
    }

    virtual IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return m_reflectable->at(index);
    }

    virtual IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return m_reflectable->at(index);
    }

    virtual IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return m_reflectable->operator[](index);
    }

    virtual IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return m_reflectable->operator[](index);
    }

    virtual void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_reflectable->setAt(value, index);
    }

    virtual void append(const AnyHolder<ALLOC>& value) override
    {
        m_reflectable->append(value);
    }

    // exact checked getters
    virtual bool getBool() const override { return m_reflectable->getBool(); }
    virtual int8_t getInt8() const override { return m_reflectable->getInt8(); }
    virtual int16_t getInt16() const override { return m_reflectable->getInt16(); }
    virtual int32_t getInt32() const override { return m_reflectable->getInt32(); }
    virtual int64_t getInt64() const override { return m_reflectable->getInt64(); }
    virtual uint8_t getUInt8() const override { return m_reflectable->getUInt8(); }
    virtual uint16_t getUInt16() const override { return m_reflectable->getUInt16(); }
    virtual uint32_t getUInt32() const override { return m_reflectable->getUInt32(); }
    virtual uint64_t getUInt64() const override { return m_reflectable->getUInt64(); }
    virtual float getFloat() const override { return m_reflectable->getFloat(); }
    virtual double getDouble() const override { return m_reflectable->getDouble(); }
    virtual StringView getString() const override { return m_reflectable->getString(); }
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const override { return m_reflectable->getBitBuffer(); }

    // convenience conversions
    virtual int64_t toInt() const override { return m_reflectable->toInt(); }
    virtual uint64_t toUInt() const override { return m_reflectable->toUInt(); }
    virtual double toDouble() const override { return m_reflectable->toDouble(); }
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const override
    {
        return m_reflectable->toString(allocator);
    }

    virtual void initializeChildren() override
    {
        m_reflectable->initializeChildren();
    }

    virtual void write(BitStreamWriter& writer) const override
    {
        m_reflectable->write(writer);
    }

    virtual size_t bitSizeOf(size_t bitPosition = 0) const override
    {
        return m_reflectable->bitSizeOf(bitPosition);
    }

private:
    T m_object;
    IBasicReflectablePtr<ALLOC> m_reflectable;
};

/**
 * Factory used to make it easier to create reflectable instances.
 *
 * Creates reflectables for all builtin types and for arrays.
 */
template <typename ALLOC>
class BasicReflectableFactory
{
public:
    static IBasicReflectablePtr<ALLOC> getBool(bool value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BoolReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getBool(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getInt8(int8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int8Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getInt8(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getInt16(int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getInt16(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getInt32(int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getInt32(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getInt64(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getInt64(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getUInt8(uint8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt8Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getUInt8(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getUInt16(uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getUInt16(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getUInt32(uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getUInt32(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getUInt64(uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getUInt64(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarInt16(
            int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarInt16(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarInt32(
            int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarInt32(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarInt64(
            int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarInt64(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarInt(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarInt(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt16(
            uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarUInt16(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt32(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarUInt32(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt64(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarUInt64(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarUInt(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getVarSize(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getVarSize(), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFloat16(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FloatReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getFloat16(), value);
    }

    static IBasicReflectablePtr<ALLOC> getFloat32(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FloatReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getFloat32(), value);
    }

    static IBasicReflectablePtr<ALLOC> getFloat64(double value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DoubleReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getFloat64(), value);
    }

    static IBasicReflectablePtr<ALLOC> getString(
            const string<ALLOC>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getString(), value);
    }

    static IBasicReflectablePtr<ALLOC> getString(
            StringView value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getString(), value);
    }

    static IBasicReflectablePtr<ALLOC> getBitBuffer(
            const BasicBitBuffer<ALLOC>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitBufferReflectable<ALLOC>>(
                allocator, BuiltinTypeInfo<ALLOC>::getBitBuffer(), value);
    }

    static IBasicReflectablePtr<ALLOC> getFixedSignedBitField(
            uint8_t bitSize, int8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<Int8Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedSignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedSignedBitField(
            uint8_t bitSize, int16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int16Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedSignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedSignedBitField(
            uint8_t bitSize, int32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<Int32Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedSignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedSignedBitField(
            uint8_t bitSize, int64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedSignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'int64_t' value!";
        }
        return std::allocate_shared<Int64Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedSignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitField(
            uint8_t bitSize, uint8_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<UInt8Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitField(
            uint8_t bitSize, uint16_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<UInt16Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitField(
            uint8_t bitSize, uint32_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitField(
            uint8_t bitSize, uint64_t value, const ALLOC& allocator = ALLOC())
    {
        if (bitSize <= 32 || bitSize > 64)
        {
            throw CppRuntimeException("BasicReflectableFactory::getFixedUnsignedBitField") +
                    " - invalid bit size '" + bitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(bitSize), value, static_cast<uint8_t>(0));
    }

    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            uint8_t maxBitSize, int8_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int8_t' value!";
        }
        return std::allocate_shared<Int8Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            uint8_t maxBitSize, int16_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int16Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            uint8_t maxBitSize, int32_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int32_t' value!";
        }
        return std::allocate_shared<Int32Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            uint8_t maxBitSize, int64_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicSignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'int16_t' value!";
        }
        return std::allocate_shared<Int64Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint8_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint8_t' value!";
        }
        return std::allocate_shared<UInt8Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint16_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint16_t' value!";
        }
        return std::allocate_shared<UInt16Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint32_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint32_t' value!";
        }
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            uint8_t maxBitSize, uint64_t value, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize <= 32 || maxBitSize > 64)
        {
            throw CppRuntimeException("BasicReflectableFactory::getDynamicUnsignedBitField") +
                    " - invalid max bit size '" + maxBitSize + "' for 'uint64_t' value!";
        }
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(allocator,
                BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField(maxBitSize), value, dynamicBitSize);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<!std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectableConstPtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BuiltinReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<!std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BuiltinReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectableConstPtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() == SchemaType::DYNAMIC_SIGNED_BITFIELD ||
                typeInfo.getSchemaType() == SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicReflectableFactory::getBuiltinArray") +
                    " - dynamic bit field array must be created with dynamicBitSize argument!";
        }

        return std::allocate_shared<IntegralReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, static_cast<uint8_t>(0));
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() == SchemaType::DYNAMIC_SIGNED_BITFIELD ||
                typeInfo.getSchemaType() == SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicReflectableFactory::getBuiltinArray") +
                    " - dynamic bit field array must be created with dynamicBitSize argument!";
        }

        return std::allocate_shared<IntegralReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, static_cast<uint8_t>(0));
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectableConstPtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, const RAW_ARRAY& rawArray, uint8_t dynamicBitSize,
            const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() != SchemaType::DYNAMIC_SIGNED_BITFIELD &&
                typeInfo.getSchemaType() != SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicReflectableFactory::getBuiltinArray") +
                    " - expected a dynamic bit field array!";
        }

        return std::allocate_shared<IntegralReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, dynamicBitSize);
    }

    template <typename RAW_ARRAY,
            typename std::enable_if<std::is_integral<typename RAW_ARRAY::value_type>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getBuiltinArray(
            const IBasicTypeInfo<ALLOC>& typeInfo, RAW_ARRAY& rawArray, uint8_t dynamicBitSize,
            const ALLOC& allocator = ALLOC())
    {
        if (typeInfo.getSchemaType() != SchemaType::DYNAMIC_SIGNED_BITFIELD &&
                typeInfo.getSchemaType() != SchemaType::DYNAMIC_UNSIGNED_BITFIELD)
        {
            throw CppRuntimeException("BasicReflectableFactory::getBuiltinArray") +
                    " - expected a dynamic bit field array!";
        }

        return std::allocate_shared<IntegralReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, typeInfo, allocator, rawArray, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getCompoundArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<CompoundReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getCompoundArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<CompoundReflectableArray<ALLOC, RAW_ARRAY>>(allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getBitmaskArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitmaskReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getBitmaskArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitmaskReflectableArray<ALLOC, RAW_ARRAY>>(allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getEnumArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<EnumReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getEnumArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<EnumReflectableArray<ALLOC, RAW_ARRAY>>(allocator, allocator, rawArray);
    }
};

/** Typedef to the reflectable factory provided for convenience - using default std::allocator<uint8_t>. */
using ReflectableFactory = BasicReflectableFactory<std::allocator<uint8_t>>;

template <typename ALLOC>
ReflectableBase<ALLOC>::ReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo) :
        m_typeInfo(typeInfo)
{}

template <typename ALLOC>
ReflectableBase<ALLOC>::~ReflectableBase()
{}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& ReflectableBase<ALLOC>::getTypeInfo() const
{
    return m_typeInfo;
}

template <typename ALLOC>
bool ReflectableBase<ALLOC>::isArray() const
{
    return false;
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::initializeChildren()
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not a compound type!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::createField(StringView)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to create!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no fields to set!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no parameters to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no parameters to get!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no functions to call!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' has no functions to call!";
}

template <typename ALLOC>
StringView ReflectableBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is neither choice nor union!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::find(StringView path) const
{
    return detail::getFromObject(*this, path, 0);
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::find(StringView path)
{
    return detail::getFromObject(*this, path, 0);
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::operator[](StringView path) const
{
    return find(path);
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::operator[](StringView path)
{
    return find(path);
}

template <typename ALLOC>
size_t ReflectableBase<ALLOC>::size() const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::resize(size_t)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::at(size_t) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::at(size_t)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::operator[](size_t) const
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::operator[](size_t)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::setAt(const AnyHolder<ALLOC>&, size_t)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::append(const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Type '") + getTypeInfo().getSchemaName() + "' is not an array!";
}

template <typename ALLOC>
bool ReflectableBase<ALLOC>::getBool() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not boolean type!";
}

template <typename ALLOC>
int8_t ReflectableBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int8 type!";
}

template <typename ALLOC>
int16_t ReflectableBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int16 type!";
}

template <typename ALLOC>
int32_t ReflectableBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int32 type!";
}

template <typename ALLOC>
int64_t ReflectableBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not int64 type!";
}

template <typename ALLOC>
uint8_t ReflectableBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint8 type!";
}

template <typename ALLOC>
uint16_t ReflectableBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint16 type!";
}

template <typename ALLOC>
uint32_t ReflectableBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint32 type!";
}

template <typename ALLOC>
uint64_t ReflectableBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not uint64 type!";
}

template <typename ALLOC>
float ReflectableBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not float type!";
}

template <typename ALLOC>
double ReflectableBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not double type!";
}

template <typename ALLOC>
StringView ReflectableBase<ALLOC>::getString() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not string type!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& ReflectableBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException(getTypeInfo().getSchemaName()) + "' is not an extern type!";
}

template <typename ALLOC>
int64_t ReflectableBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to signed integer is not available!";
}

template <typename ALLOC>
uint64_t ReflectableBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to unsigned integer is not available!";
}

template <typename ALLOC>
double ReflectableBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to double is not available!";
}

template <typename ALLOC>
string<ALLOC> ReflectableBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Conversion from '") + getTypeInfo().getSchemaName() +
            "' to string is not available!";
}

template <typename ALLOC>
void ReflectableConstAllocatorHolderBase<ALLOC>::initializeChildren()
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant!";
}

template <typename ALLOC>
void ReflectableConstAllocatorHolderBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::initializeChildren()
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::createField(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
StringView ReflectableArrayBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
bool ReflectableArrayBase<ALLOC>::getBool() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int8_t ReflectableArrayBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int16_t ReflectableArrayBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int32_t ReflectableArrayBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int64_t ReflectableArrayBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint8_t ReflectableArrayBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint16_t ReflectableArrayBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint32_t ReflectableArrayBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint64_t ReflectableArrayBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
float ReflectableArrayBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
double ReflectableArrayBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
StringView ReflectableArrayBase<ALLOC>::getString() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& ReflectableArrayBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
int64_t ReflectableArrayBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
uint64_t ReflectableArrayBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
double ReflectableArrayBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
string<ALLOC> ReflectableArrayBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::write(BitStreamWriter&) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
size_t ReflectableArrayBase<ALLOC>::bitSizeOf(size_t) const
{
    throw CppRuntimeException("Reflectable is an array '") + getTypeInfo().getSchemaName() + "[]'!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::resize(size_t)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstArrayBase<ALLOC>::at(size_t)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstArrayBase<ALLOC>::operator[](size_t)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant array!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::setAt(const AnyHolder<ALLOC>&, size_t)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant array!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::append(const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable '") + getTypeInfo().getSchemaName() + "' is constant array!";
}

} // namespace zserio

#endif // ZSERIO_REFLECTABLE_H_INC
