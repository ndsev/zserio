#ifndef ZSERIO_REFLECTABLE_H_INC
#define ZSERIO_REFLECTABLE_H_INC

#include <functional>
#include <type_traits>

#include "zserio/AllocatorHolder.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/IReflectable.h"
#include "zserio/Span.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/Traits.h"
#include "zserio/TypeInfo.h"
#include "zserio/TypeInfoUtil.h"

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
    ~ReflectableBase() override = 0;

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

    const IBasicTypeInfo<ALLOC>& getTypeInfo() const override;
    bool isArray() const override;

    void initializeChildren() override;
    void initialize(const vector<AnyHolder<ALLOC>, ALLOC>& typeArguments) override;
    size_t initializeOffsets(size_t bitPosition) override;
    size_t initializeOffsets() override;
    size_t bitSizeOf(size_t bitPosition) const override;
    size_t bitSizeOf() const override;
    void write(BitStreamWriter& writer) const override;

    IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override;
    IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    IBasicReflectablePtr<ALLOC> createField(StringView name) override;
    void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override;
    IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override;
    IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;

    StringView getChoice() const override;

    IBasicReflectableConstPtr<ALLOC> find(StringView path) const override;
    IBasicReflectablePtr<ALLOC> find(StringView path) override;
    IBasicReflectableConstPtr<ALLOC> operator[](StringView path) const override;
    IBasicReflectablePtr<ALLOC> operator[](StringView path) override;

    size_t size() const override;
    void resize(size_t size) override;
    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override;
    IBasicReflectablePtr<ALLOC> at(size_t index) override;
    IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override;
    IBasicReflectablePtr<ALLOC> operator[](size_t index) override;
    void setAt(const AnyHolder<ALLOC>& value, size_t index) override;
    void append(const AnyHolder<ALLOC>& value) override;

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override;
    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override;
    AnyHolder<ALLOC> getAnyValue() const override;
    AnyHolder<ALLOC> getAnyValue() override;

    // exact checked getters
    bool getBool() const override;
    int8_t getInt8() const override;
    int16_t getInt16() const override;
    int32_t getInt32() const override;
    int64_t getInt64() const override;
    uint8_t getUInt8() const override;
    uint16_t getUInt16() const override;
    uint32_t getUInt32() const override;
    uint64_t getUInt64() const override;
    float getFloat() const override;
    double getDouble() const override;
    Span<const uint8_t> getBytes() const override;
    StringView getStringView() const override;
    const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    // convenience conversions
    int64_t toInt() const override;
    uint64_t toUInt() const override;
    double toDouble() const override;
    string<ALLOC> toString(const ALLOC& allocator) const override;
    string<ALLOC> toString() const override;

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

public:
    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(getValue()), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        // we have only const reference, thus return it
        return AnyHolder<ALLOC>(std::cref(getValue()), allocator);
    }

private:
    const T& m_value;
};

/**
 * Specialization of the BuiltinReflectableBase base class for numeric (arithmetic) types, string view and span.
 *
 * Hold the value instead of reference.
 */
template <typename ALLOC, typename T>
class BuiltinReflectableBase<ALLOC, T,
        typename std::enable_if<std::is_arithmetic<T>::value || std::is_same<T, StringView>::value ||
                is_span<T>::value>::type> : public ReflectableBase<ALLOC>
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

public:
    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(m_value, allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(m_value, allocator);
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
    IntegralReflectableBase(const IBasicTypeInfo<ALLOC>& typeInfo, T value) :
            Base(typeInfo, value)
    {}

    double toDouble() const override
    {
        return static_cast<double>(Base::getValue());
    }

    string<ALLOC> toString(const ALLOC& allocator) const override
    {
        return ::zserio::toString<ALLOC>(Base::getValue(), allocator);
    }
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

    using Base::Base;

public:
    int64_t toInt() const override
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

    using Base::Base;

public:
    uint64_t toUInt() const override
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
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getBool;

    explicit BoolReflectable(bool value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 1;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBool(Base::getValue());
    }

    bool getBool() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 8-bit signed integral reflectables.
 *
 * Implements getInt8() conversion.
 */
template <typename ALLOC>
class Int8ReflectableBase : public SignedReflectableBase<ALLOC, int8_t>
{
protected:
    using Base = SignedReflectableBase<ALLOC, int8_t>;

    using Base::Base;

public:
    int8_t getInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 16-bit signed integral reflectables.
 *
 * Implements getInt16() conversion.
 */
template <typename ALLOC>
class Int16ReflectableBase : public SignedReflectableBase<ALLOC, int16_t>
{
protected:
    using Base = SignedReflectableBase<ALLOC, int16_t>;

    using Base::Base;

public:
    int16_t getInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 32-bit signed integral reflectables.
 *
 * Implements getInt32() conversion.
 */
template <typename ALLOC>
class Int32ReflectableBase : public SignedReflectableBase<ALLOC, int32_t>
{
protected:
    using Base = SignedReflectableBase<ALLOC, int32_t>;

    using Base::Base;

public:
    int32_t getInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 64-bit signed integral reflectables.
 *
 * Implements getInt64() conversion.
 */
template <typename ALLOC>
class Int64ReflectableBase : public SignedReflectableBase<ALLOC, int64_t>
{
protected:
    using Base = SignedReflectableBase<ALLOC, int64_t>;

    using Base::Base;

public:
    int64_t getInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 8-bit unsigned integral reflectables.
 *
 * Implements getUInt8() conversion.
 */
template <typename ALLOC>
class UInt8ReflectableBase : public UnsignedReflectableBase<ALLOC, uint8_t>
{
protected:
    using Base = UnsignedReflectableBase<ALLOC, uint8_t>;

    using Base::Base;

public:
    uint8_t getUInt8() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 16-bit unsigned integral reflectables.
 *
 * Implements getUInt16() conversion.
 */
template <typename ALLOC>
class UInt16ReflectableBase : public UnsignedReflectableBase<ALLOC, uint16_t>
{
protected:
    using Base = UnsignedReflectableBase<ALLOC, uint16_t>;

    using Base::Base;

public:
    uint16_t getUInt16() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 32-bit unsigned integral reflectables.
 *
 * Implements getUInt32() conversion.
 */
template <typename ALLOC>
class UInt32ReflectableBase : public UnsignedReflectableBase<ALLOC, uint32_t>
{
protected:
    using Base = UnsignedReflectableBase<ALLOC, uint32_t>;

    using Base::Base;

public:
    uint32_t getUInt32() const override
    {
        return Base::getValue();
    }
};

/**
 * Base class for 64-bit unsigned integral reflectables.
 *
 * Implements getUInt64() conversion.
 */
template <typename ALLOC>
class UInt64ReflectableBase : public UnsignedReflectableBase<ALLOC, uint64_t>
{
protected:
    using Base = UnsignedReflectableBase<ALLOC, uint64_t>;

    using Base::Base;

public:
    uint64_t getUInt64() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for int8 type.
 */
template <typename ALLOC>
class Int8Reflectable : public Int8ReflectableBase<ALLOC>
{
private:
    using Base = Int8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getInt8;

    explicit Int8Reflectable(int8_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 8;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), 8);
    }
};

/**
 * Reflectable for int16 type.
 */
template <typename ALLOC>
class Int16Reflectable : public Int16ReflectableBase<ALLOC>
{
private:
    using Base = Int16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getInt16;

    explicit Int16Reflectable(int16_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 16;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), 16);
    }
};

/**
 * Reflectable for int32 type.
 */
template <typename ALLOC>
class Int32Reflectable : public Int32ReflectableBase<ALLOC>
{
private:
    using Base = Int32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getInt32;

    explicit Int32Reflectable(int32_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 32;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), 32);
    }
};

/**
 * Reflectable for int64 type.
 */
template <typename ALLOC>
class Int64Reflectable : public Int64ReflectableBase<ALLOC>
{
private:
    using Base = Int64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getInt64;

    explicit Int64Reflectable(int64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 64;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits64(Base::getValue(), 64);
    }
};

/**
 * Reflectable for uint8 type.
 */
template <typename ALLOC>
class UInt8Reflectable : public UInt8ReflectableBase<ALLOC>
{
private:
    using Base = UInt8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getUInt8;

    explicit UInt8Reflectable(uint8_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 8;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), 8);
    }
};

/**
 * Reflectable for uint16 type.
 */
template <typename ALLOC>
class UInt16Reflectable : public UInt16ReflectableBase<ALLOC>
{
private:
    using Base = UInt16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getUInt16;

    explicit UInt16Reflectable(uint16_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 16;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), 16);
    }
};

/**
 * Reflectable for uint32 type.
 */
template <typename ALLOC>
class UInt32Reflectable : public UInt32ReflectableBase<ALLOC>
{
private:
    using Base = UInt32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getUInt32;

    explicit UInt32Reflectable(uint32_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 32;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), 32);
    }
};

/**
 * Reflectable for uint64 type.
 */
template <typename ALLOC>
class UInt64Reflectable : public UInt64ReflectableBase<ALLOC>
{
private:
    using Base = UInt64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getUInt64;

    explicit UInt64Reflectable(uint64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 64;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits64(Base::getValue(), 64);
    }
};

template <typename ALLOC, typename T>
class FixedSignedBitFieldReflectable;

template <typename ALLOC>
class FixedSignedBitFieldReflectable<ALLOC, int8_t> : public Int8ReflectableBase<ALLOC>
{
private:
    using Base = Int8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedSignedBitField;

    FixedSignedBitFieldReflectable(int8_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("FixedSignedBitFieldReflectable ") <<
                    " - invalid bit size '" << bitSize << "' for 'int8_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedSignedBitFieldReflectable<ALLOC, int16_t> : public Int16ReflectableBase<ALLOC>
{
private:
    using Base = Int16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedSignedBitField;

    FixedSignedBitFieldReflectable(int16_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("FixedSignedBitFieldReflectable ") <<
                    " - invalid bit size '" << bitSize << "' for 'int16_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedSignedBitFieldReflectable<ALLOC, int32_t> : public Int32ReflectableBase<ALLOC>
{
private:
    using Base = Int32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedSignedBitField;

    FixedSignedBitFieldReflectable(int32_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("FixedSignedBitFieldReflectable ") <<
                    " - invalid bit size '" << bitSize << "' for 'int32_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedSignedBitFieldReflectable<ALLOC, int64_t> : public Int64ReflectableBase<ALLOC>
{
private:
    using Base = Int64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedSignedBitField;

    FixedSignedBitFieldReflectable(int64_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 32) // check for maximum bit size (64) is done in type info
        {
            throw CppRuntimeException("FixedSignedBitFieldReflectable ") <<
                    " - invalid bit size '" << bitSize << "' for 'int64_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits64(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC, typename T>
class FixedUnsignedBitFieldReflectable;

template <typename ALLOC>
class FixedUnsignedBitFieldReflectable<ALLOC, uint8_t> : public UInt8ReflectableBase<ALLOC>
{
private:
    using Base = UInt8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField;

    FixedUnsignedBitFieldReflectable(uint8_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize > 8)
        {
            throw CppRuntimeException("FixedUnsignedBitFieldReflectable") <<
                    " - invalid bit size '" << bitSize << "' for 'uint8_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedUnsignedBitFieldReflectable<ALLOC, uint16_t> : public UInt16ReflectableBase<ALLOC>
{
private:
    using Base = UInt16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField;

    FixedUnsignedBitFieldReflectable(uint16_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 8 || bitSize > 16)
        {
            throw CppRuntimeException("FixedUnsignedBitFieldReflectable") <<
                    " - invalid bit size '" << bitSize << "' for 'uint16_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedUnsignedBitFieldReflectable<ALLOC, uint32_t> : public UInt32ReflectableBase<ALLOC>
{
private:
    using Base = UInt32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField;

    FixedUnsignedBitFieldReflectable(uint32_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 16 || bitSize > 32)
        {
            throw CppRuntimeException("FixedUnsignedBitFieldReflectable") <<
                    " - invalid bit size '" << bitSize << "' for 'uint32_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC>
class FixedUnsignedBitFieldReflectable<ALLOC, uint64_t> : public UInt64ReflectableBase<ALLOC>
{
private:
    using Base = UInt64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField;

    FixedUnsignedBitFieldReflectable(uint64_t value, uint8_t bitSize) :
            Base(typeInfo(bitSize), value)
    {
        if (bitSize <= 32) // check for maximum bit size (64) is done in type info
        {
            throw CppRuntimeException("FixedUnsignedBitFieldReflectable") <<
                    " - invalid bit size '" << bitSize << "' for 'uint64_t' value!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return Base::getTypeInfo().getBitSize();
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits64(Base::getValue(), Base::getTypeInfo().getBitSize());
    }
};

template <typename ALLOC, typename T>
class DynamicSignedBitFieldReflectable;

template <typename ALLOC>
class DynamicSignedBitFieldReflectable<ALLOC, int8_t> : public Int8ReflectableBase<ALLOC>
{
private:
    using Base = Int8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField;

    DynamicSignedBitFieldReflectable(int8_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'int8_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicSignedBitFieldReflectable<ALLOC, int16_t> : public Int16ReflectableBase<ALLOC>
{
private:
    using Base = Int16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField;

    DynamicSignedBitFieldReflectable(int16_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'int16_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicSignedBitFieldReflectable<ALLOC, int32_t> : public Int32ReflectableBase<ALLOC>
{
private:
    using Base = Int32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField;

    DynamicSignedBitFieldReflectable(int32_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'int32_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicSignedBitFieldReflectable<ALLOC, int64_t> : public Int64ReflectableBase<ALLOC>
{
private:
    using Base = Int64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField;

    DynamicSignedBitFieldReflectable(int64_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 32) // check for maximum bit size (64) is done in type info
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'int64_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeSignedBits64(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC, typename T>
class DynamicUnsignedBitFieldReflectable;

template <typename ALLOC>
class DynamicUnsignedBitFieldReflectable<ALLOC, uint8_t> : public UInt8ReflectableBase<ALLOC>
{
private:
    using Base = UInt8ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField;

    DynamicUnsignedBitFieldReflectable(uint8_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize > 8)
        {
            throw CppRuntimeException("DynamicUnsignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'uint8_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicUnsignedBitFieldReflectable<ALLOC, uint16_t> : public UInt16ReflectableBase<ALLOC>
{
private:
    using Base = UInt16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField;

    DynamicUnsignedBitFieldReflectable(uint16_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 8 || maxBitSize > 16)
        {
            throw CppRuntimeException("DynamicUnsignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'uint16_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicUnsignedBitFieldReflectable<ALLOC, uint32_t> : public UInt32ReflectableBase<ALLOC>
{
private:
    using Base = UInt32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField;

    DynamicUnsignedBitFieldReflectable(uint32_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 16 || maxBitSize > 32)
        {
            throw CppRuntimeException("DynamicUnsignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'uint32_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

template <typename ALLOC>
class DynamicUnsignedBitFieldReflectable<ALLOC, uint64_t> : public UInt64ReflectableBase<ALLOC>
{
private:
    using Base = UInt64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField;

    DynamicUnsignedBitFieldReflectable(uint64_t value, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(typeInfo(maxBitSize), value), m_dynamicBitSize(dynamicBitSize)
    {
        if (maxBitSize <= 32) // check for maximum bit size (64) is done in type info
        {
            throw CppRuntimeException("DynamicUnsignedBitFieldReflectable") <<
                    " - invalid max bit size '" << maxBitSize << "' for 'uint64_t' value!";
        }

        if (dynamicBitSize > maxBitSize)
        {
            throw CppRuntimeException("DynamicSignedBitFieldReflectable - dynamic bit size '") <<
                    dynamicBitSize << "' is greater than max bit size '" << maxBitSize << "'!";
        }
    }

    size_t bitSizeOf(size_t) const override
    {
        return m_dynamicBitSize;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBits64(Base::getValue(), m_dynamicBitSize);
    }

private:
    uint8_t m_dynamicBitSize;
};

/**
 * Reflectable for varint16 type.
 */
template <typename ALLOC>
class VarInt16Reflectable : public Int16ReflectableBase<ALLOC>
{
private:
    using Base = Int16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarInt16;

    explicit VarInt16Reflectable(int16_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarInt16(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarInt16(Base::getValue());
    }
};

/**
 * Reflectable for varint32 type.
 */
template <typename ALLOC>
class VarInt32Reflectable : public Int32ReflectableBase<ALLOC>
{
private:
    using Base = Int32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarInt32;

    explicit VarInt32Reflectable(int32_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarInt32(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarInt32(Base::getValue());
    }
};

/**
 * Reflectable for varint64 type.
 */
template <typename ALLOC>
class VarInt64Reflectable : public Int64ReflectableBase<ALLOC>
{
private:
    using Base = Int64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarInt64;

    explicit VarInt64Reflectable(int64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarInt64(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarInt64(Base::getValue());
    }
};

/**
 * Reflectable for varint type.
 */
template <typename ALLOC>
class VarIntReflectable : public Int64ReflectableBase<ALLOC>
{
private:
    using Base = Int64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarInt;

    explicit VarIntReflectable(int64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarInt(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarInt(Base::getValue());
    }
};

/**
 * Reflectable for varuint16 type.
 */
template <typename ALLOC>
class VarUInt16Reflectable : public UInt16ReflectableBase<ALLOC>
{
private:
    using Base = UInt16ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarUInt16;

    explicit VarUInt16Reflectable(uint16_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarUInt16(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarUInt16(Base::getValue());
    }
};

/**
 * Reflectable for varuint32 type.
 */
template <typename ALLOC>
class VarUInt32Reflectable : public UInt32ReflectableBase<ALLOC>
{
private:
    using Base = UInt32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarUInt32;

    explicit VarUInt32Reflectable(uint32_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarUInt32(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarUInt32(Base::getValue());
    }
};

/**
 * Reflectable for varuint64 type.
 */
template <typename ALLOC>
class VarUInt64Reflectable : public UInt64ReflectableBase<ALLOC>
{
private:
    using Base = UInt64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarUInt64;

    explicit VarUInt64Reflectable(uint64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarUInt64(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarUInt64(Base::getValue());
    }
};

/**
 * Reflectable for varuint type.
 */
template <typename ALLOC>
class VarUIntReflectable : public UInt64ReflectableBase<ALLOC>
{
private:
    using Base = UInt64ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarUInt;

    explicit VarUIntReflectable(uint64_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarUInt(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarUInt(Base::getValue());
    }
};

/**
 * Reflectable for varsize type.
 */
template <typename ALLOC>
class VarSizeReflectable : public UInt32ReflectableBase<ALLOC>
{
private:
    using Base = UInt32ReflectableBase<ALLOC>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getVarSize;

    explicit VarSizeReflectable(uint32_t value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfVarUInt(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeVarSize(Base::getValue());
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
    double toDouble() const override
    {
        return static_cast<double>(getValue());
    }
};

/**
 * Reflectable for values of 16-bit float type.
 */
template <typename ALLOC>
class Float16Reflectable : public FloatingPointReflectableBase<ALLOC, float>
{
private:
    using Base = FloatingPointReflectableBase<ALLOC, float>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFloat16;

    explicit Float16Reflectable(float value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 16;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeFloat16(Base::getValue());
    }

    float getFloat() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of 32-bit float type.
 */
template <typename ALLOC>
class Float32Reflectable : public FloatingPointReflectableBase<ALLOC, float>
{
private:
    using Base = FloatingPointReflectableBase<ALLOC, float>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFloat32;

    explicit Float32Reflectable(float value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 32;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeFloat32(Base::getValue());
    }

    float getFloat() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of double type.
 */
template <typename ALLOC>
class Float64Reflectable : public FloatingPointReflectableBase<ALLOC, double>
{
private:
    using Base = FloatingPointReflectableBase<ALLOC, double>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getFloat64;

    explicit Float64Reflectable(double value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return 64;
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeFloat64(Base::getValue());
    }

    double getDouble() const override
    {
        return Base::getValue();
    }
};

/**
 * Reflectable for values of bytes type.
 */
template <typename ALLOC>
class BytesReflectable : public BuiltinReflectableBase<ALLOC, Span<const uint8_t>>
{
private:
    using Base = BuiltinReflectableBase<ALLOC, Span<const uint8_t>>;

public:
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getBytes;

    explicit BytesReflectable(Span<const uint8_t> value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfBytes(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBytes(Base::getValue());
    }

    Span<const uint8_t> getBytes() const override
    {
        return Base::getValue();
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
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getString;

    explicit StringReflectable(StringView value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfString(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeString(Base::getValue());
    }

    StringView getStringView() const override
    {
        return Base::getValue();
    }

    string<ALLOC> toString(const ALLOC& allocator) const override
    {
        return zserio::toString(Base::getValue(), allocator);
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
    static constexpr auto typeInfo = BuiltinTypeInfo<ALLOC>::getBitBuffer;

    explicit BitBufferReflectable(const BasicBitBuffer<ALLOC>& value) :
            Base(typeInfo(), value)
    {}

    size_t bitSizeOf(size_t) const override
    {
        return zserio::bitSizeOfBitBuffer(Base::getValue());
    }

    void write(BitStreamWriter& writer) const override
    {
        writer.writeBitBuffer(Base::getValue());
    }

    const BasicBitBuffer<ALLOC>& getBitBuffer() const override
    {
        return Base::getValue();
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
                [name](const BasicFunctionInfo<ALLOC>& functionInfo)
                {
                    return functionInfo.schemaName == name;
                }
        );
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
    using Base::Base;
    using Base::getTypeInfo;

    void initializeChildren() override;
    void initialize(const vector<AnyHolder<ALLOC>, ALLOC>& typeArguments) override;
    size_t initializeOffsets(size_t bitPosition) override;

    IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override;
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
    using Base::Base;
    using Base::getTypeInfo;

    bool isArray() const override
    {
        return true;
    }

    void initializeChildren() override;
    void initialize(const vector<AnyHolder<ALLOC>, ALLOC>& typeArguments) override;
    size_t initializeOffsets(size_t bitPosition) override;
    size_t bitSizeOf(size_t bitPosition) const override;
    void write(BitStreamWriter& writer) const override;

    IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override;
    IBasicReflectablePtr<ALLOC> getField(StringView name) override;
    IBasicReflectablePtr<ALLOC> createField(StringView name) override;
    void setField(StringView name, const AnyHolder<ALLOC>& value) override;
    IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override;
    IBasicReflectablePtr<ALLOC> getParameter(StringView name) override;
    IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override;
    IBasicReflectablePtr<ALLOC> callFunction(StringView name) override;

    StringView getChoice() const override;

    IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override;
    IBasicReflectablePtr<ALLOC> operator[](size_t index) override;

    bool getBool() const override;
    int8_t getInt8() const override;
    int16_t getInt16() const override;
    int32_t getInt32() const override;
    int64_t getInt64() const override;
    uint8_t getUInt8() const override;
    uint16_t getUInt16() const override;
    uint32_t getUInt32() const override;
    uint64_t getUInt64() const override;
    float getFloat() const override;
    double getDouble() const override;
    Span<const uint8_t> getBytes() const override;
    StringView getStringView() const override;
    const BasicBitBuffer<ALLOC>& getBitBuffer() const override;

    int64_t toInt() const override;
    uint64_t toUInt() const override;
    double toDouble() const override;
    string<ALLOC> toString(const ALLOC& allocator) const override;
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
    using Base::Base;
    using Base::getTypeInfo;

    void resize(size_t index) override;
    IBasicReflectablePtr<ALLOC> at(size_t index) override;
    IBasicReflectablePtr<ALLOC> operator[](size_t index) override;
    void setAt(const AnyHolder<ALLOC>& value, size_t index) override;
    void append(const AnyHolder<ALLOC>& value) override;

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override;
};

/**
 * Reflectable for arrays of builtin types (except bit field arrays).
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class BuiltinReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    BuiltinReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(ElementReflectable::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(Base::get_allocator(), m_rawArray[index]);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class BuiltinReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;

    BuiltinReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementReflectable::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(Base::get_allocator(), m_rawArray[index]);
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(Base::get_allocator(), m_rawArray[index]);
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        m_rawArray[index] = value.template get<typename RAW_ARRAY::value_type>();
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/**
 * Reflectable for arrays of fixed bit fields.
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class FixedBitFieldReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    FixedBitFieldReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray, uint8_t bitSize) :
            Base(ElementReflectable::typeInfo(bitSize), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], getTypeInfo().getBitSize());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class FixedBitFieldReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;

    FixedBitFieldReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray, uint8_t bitSize) :
            Base(ElementReflectable::typeInfo(bitSize), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], getTypeInfo().getBitSize());
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], getTypeInfo().getBitSize());
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        m_rawArray[index] = value.template get<typename RAW_ARRAY::value_type>();
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/**
 * Reflectable for arrays of dynamic bit fields.
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class DynamicBitFieldReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    DynamicBitFieldReflectableConstArray(const ALLOC& allocator,
            const RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(ElementReflectable::typeInfo(maxBitSize), allocator),
            m_rawArray(rawArray), m_maxBitSize(maxBitSize), m_dynamicBitSize(dynamicBitSize)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], m_maxBitSize, m_dynamicBitSize);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

private:
    const RAW_ARRAY& m_rawArray;
    const uint8_t m_maxBitSize;
    const uint8_t m_dynamicBitSize;
};

template <typename ALLOC, typename RAW_ARRAY, typename ELEMENT_REFLECTABLE>
class DynamicBitFieldReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementReflectable = ELEMENT_REFLECTABLE;

public:
    using Base::getTypeInfo;

    DynamicBitFieldReflectableArray(const ALLOC& allocator,
            RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize) :
            Base(ElementReflectable::typeInfo(maxBitSize), allocator),
            m_rawArray(rawArray), m_maxBitSize(maxBitSize), m_dynamicBitSize(dynamicBitSize)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], m_maxBitSize, m_dynamicBitSize);
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return std::allocate_shared<ElementReflectable>(
                Base::get_allocator(), m_rawArray[index], m_maxBitSize, m_dynamicBitSize);
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        m_rawArray[index] = value.template get<typename RAW_ARRAY::value_type>();
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
    }

private:
    RAW_ARRAY& m_rawArray;
    const uint8_t m_maxBitSize;
    const uint8_t m_dynamicBitSize;
};
/** \} */

/**
 * Typedef to a builtin array.
 */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
using BoolReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, BoolReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using BoolReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, BoolReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Int8ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, Int8Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Int8ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Int8Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Int16ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, Int16Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Int16ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Int16Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Int32ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, Int32Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Int32ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Int32Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Int64ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, Int64Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Int64ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Int64Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using UInt8ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, UInt8Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using UInt8ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, UInt8Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using UInt16ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, UInt16Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using UInt16ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, UInt16Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using UInt32ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, UInt32Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using UInt32ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, UInt32Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using UInt64ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, UInt64Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using UInt64ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, UInt64Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using FixedSignedBitFieldReflectableConstArray = FixedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY,
        FixedSignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;
template <typename ALLOC, typename RAW_ARRAY>
using FixedSignedBitFieldReflectableArray = FixedBitFieldReflectableArray<ALLOC, RAW_ARRAY,
        FixedSignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;

template <typename ALLOC, typename RAW_ARRAY>
using FixedUnsignedBitFieldReflectableConstArray = FixedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY,
        FixedUnsignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;
template <typename ALLOC, typename RAW_ARRAY>
using FixedUnsignedBitFieldReflectableArray = FixedBitFieldReflectableArray<ALLOC, RAW_ARRAY,
        FixedUnsignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;

template <typename ALLOC, typename RAW_ARRAY>
using DynamicSignedBitFieldReflectableConstArray = DynamicBitFieldReflectableConstArray<ALLOC, RAW_ARRAY,
        DynamicSignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;
template <typename ALLOC, typename RAW_ARRAY>
using DynamicSignedBitFieldReflectableArray = DynamicBitFieldReflectableArray<ALLOC, RAW_ARRAY,
        DynamicSignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;

template <typename ALLOC, typename RAW_ARRAY>
using DynamicUnsignedBitFieldReflectableConstArray = DynamicBitFieldReflectableConstArray<ALLOC, RAW_ARRAY,
        DynamicUnsignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;
template <typename ALLOC, typename RAW_ARRAY>
using DynamicUnsignedBitFieldReflectableArray = DynamicBitFieldReflectableArray<ALLOC, RAW_ARRAY,
        DynamicUnsignedBitFieldReflectable<ALLOC, typename RAW_ARRAY::value_type>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarInt16ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarInt16Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarInt16ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarInt16Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarInt32ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarInt32Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarInt32ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarInt32Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarInt64ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarInt64Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarInt64ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarInt64Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarIntReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY, VarIntReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarIntReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarIntReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarUInt16ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarUInt16Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarUInt16ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarUInt16Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarUInt32ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarUInt32Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarUInt32ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarUInt32Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarUInt64ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarUInt64Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarUInt64ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarUInt64Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarUIntReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarUIntReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarUIntReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarUIntReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using VarSizeReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        VarSizeReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using VarSizeReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, VarSizeReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Float16ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        Float16Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Float16ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Float16Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Float32ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        Float32Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Float32ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Float32Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using Float64ReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        Float64Reflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using Float64ReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, Float64Reflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using BytesReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        BytesReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using BytesReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, BytesReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using StringReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        StringReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using StringReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, StringReflectable<ALLOC>>;

template <typename ALLOC, typename RAW_ARRAY>
using BitBufferReflectableConstArray = BuiltinReflectableConstArray<ALLOC, RAW_ARRAY,
        BitBufferReflectable<ALLOC>>;
template <typename ALLOC, typename RAW_ARRAY>
using BitBufferReflectableArray = BuiltinReflectableArray<ALLOC, RAW_ARRAY, BitBufferReflectable<ALLOC>>;
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
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    CompoundReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
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
    using Base::getTypeInfo;

    CompoundReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        m_rawArray[index] = value.template get<typename RAW_ARRAY::value_type>();
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        m_rawArray.push_back(value.template get<typename RAW_ARRAY::value_type>());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
    }

private:
    RAW_ARRAY& m_rawArray;
};
/** \} */

/** Reflectable for arrays of bitmask types. */
/** \{ */
template <typename ALLOC, typename RAW_ARRAY>
class BitmaskReflectableConstArray : public ReflectableConstArrayBase<ALLOC>
{
private:
    using Base = ReflectableConstArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;

public:
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    BitmaskReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

private:
    const RAW_ARRAY& m_rawArray;
};

template <typename ALLOC, typename RAW_ARRAY>
class BitmaskReflectableArray : public ReflectableArrayBase<ALLOC>
{
private:
    using Base = ReflectableArrayBase<ALLOC>;

    using ElementType = typename RAW_ARRAY::value_type;
    using UnderlyingElementType = typename ElementType::underlying_type;

public:
    using Base::getTypeInfo;

    BitmaskReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(ElementType::typeInfo(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return m_rawArray[index].reflectable(Base::get_allocator());
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        if (value.template isType<ElementType>())
            m_rawArray[index] = value.template get<ElementType>();
        else
            m_rawArray[index] = ElementType(value.template get<UnderlyingElementType>());
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        if (value.template isType<ElementType>())
            m_rawArray.push_back(value.template get<ElementType>());
        else
            m_rawArray.push_back(ElementType(value.template get<UnderlyingElementType>()));
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
    }

private:
    RAW_ARRAY& m_rawArray;
};
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
    using Base::getTypeInfo;
    using Base::at;
    using Base::operator[];
    using Base::getAnyValue;

    EnumReflectableConstArray(const ALLOC& allocator, const RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType, ALLOC>(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return enumReflectable(m_rawArray[index], Base::get_allocator());
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
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
    using UnderlyingElementType = typename std::underlying_type<ElementType>::type;

public:
    using Base::getTypeInfo;

    EnumReflectableArray(const ALLOC& allocator, RAW_ARRAY& rawArray) :
            Base(enumTypeInfo<ElementType, ALLOC>(), allocator), m_rawArray(rawArray)
    {}

    size_t size() const override
    {
        return m_rawArray.size();
    }

    void resize(size_t size) override
    {
        m_rawArray.resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return enumReflectable(m_rawArray[index], Base::get_allocator());
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        return enumReflectable(m_rawArray[index], Base::get_allocator());
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        if (index >= size())
        {
            throw CppRuntimeException("Index ") << index << " out of range for reflectable array '" <<
                    getTypeInfo().getSchemaName() << "' of size " << size() << "!";
        }

        if (value.template isType<ElementType>())
            m_rawArray[index] = value.template get<ElementType>();
        else
            m_rawArray[index] = valueToEnum<ElementType>(value.template get<UnderlyingElementType>());
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        if (value.template isType<ElementType>())
            m_rawArray.push_back(value.template get<ElementType>());
        else
            m_rawArray.push_back(valueToEnum<ElementType>(value.template get<UnderlyingElementType>()));
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return AnyHolder<ALLOC>(std::cref(m_rawArray), allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return AnyHolder<ALLOC>(std::ref(m_rawArray), allocator);
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
    ReflectableOwner() :
            ReflectableOwner(ALLOC())
    {}

    explicit ReflectableOwner(const ALLOC& allocator) :
            m_object(allocator),
            m_reflectable(m_object.reflectable(allocator))
    {}

    const IBasicTypeInfo<ALLOC>& getTypeInfo() const override
    {
        return m_reflectable->getTypeInfo();
    }

    bool isArray() const override
    {
        return m_reflectable->isArray();
    }

    void initializeChildren() override
    {
        m_reflectable->initializeChildren();
    }

    void initialize(const vector<AnyHolder<ALLOC>, ALLOC>& typeArguments) override
    {
        m_reflectable->initialize(typeArguments);
    }

    size_t initializeOffsets(size_t bitPosition) override
    {
        return m_reflectable->initializeOffsets(bitPosition);
    }

    size_t initializeOffsets() override
    {
        return initializeOffsets(0);
    }

    size_t bitSizeOf(size_t bitPosition) const override
    {
        return m_reflectable->bitSizeOf(bitPosition);
    }

    size_t bitSizeOf() const override
    {
        return bitSizeOf(0);
    }

    void write(BitStreamWriter& writer) const override
    {
        m_reflectable->write(writer);
    }

    IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override
    {
        return m_reflectable->getField(name);
    }

    IBasicReflectablePtr<ALLOC> getField(StringView name) override
    {
        return m_reflectable->getField(name);
    }

    IBasicReflectablePtr<ALLOC> createField(StringView name) override
    {
        return m_reflectable->createField(name);
    }

    void setField(StringView name, const AnyHolder<ALLOC>& value) override
    {
        m_reflectable->setField(name, value);
    }

    IBasicReflectableConstPtr<ALLOC> getParameter(StringView name) const override
    {
        return m_reflectable->getParameter(name);
    }

    IBasicReflectablePtr<ALLOC> getParameter(StringView name) override
    {
        return m_reflectable->getParameter(name);
    }

    IBasicReflectableConstPtr<ALLOC> callFunction(StringView name) const override
    {
        return m_reflectable->callFunction(name);
    }

    IBasicReflectablePtr<ALLOC> callFunction(StringView name) override
    {
        return m_reflectable->callFunction(name);
    }

    StringView getChoice() const override
    {
        return m_reflectable->getChoice();
    }

    IBasicReflectableConstPtr<ALLOC> find(StringView path) const override
    {
        return m_reflectable->find(path);
    }

    IBasicReflectablePtr<ALLOC> find(StringView path) override
    {
        return m_reflectable->find(path);
    }

    IBasicReflectableConstPtr<ALLOC> operator[](StringView path) const override
    {
        return m_reflectable->operator[](path);
    }

    IBasicReflectablePtr<ALLOC> operator[](StringView path) override
    {
        return m_reflectable->operator[](path);
    }

    size_t size() const override
    {
        return m_reflectable->size();
    }

    void resize(size_t size) override
    {
        m_reflectable->resize(size);
    }

    IBasicReflectableConstPtr<ALLOC> at(size_t index) const override
    {
        return m_reflectable->at(index);
    }

    IBasicReflectablePtr<ALLOC> at(size_t index) override
    {
        return m_reflectable->at(index);
    }

    IBasicReflectableConstPtr<ALLOC> operator[](size_t index) const override
    {
        return m_reflectable->operator[](index);
    }

    IBasicReflectablePtr<ALLOC> operator[](size_t index) override
    {
        return m_reflectable->operator[](index);
    }

    void setAt(const AnyHolder<ALLOC>& value, size_t index) override
    {
        m_reflectable->setAt(value, index);
    }

    void append(const AnyHolder<ALLOC>& value) override
    {
        m_reflectable->append(value);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) const override
    {
        return m_reflectable->getAnyValue(allocator);
    }

    AnyHolder<ALLOC> getAnyValue(const ALLOC& allocator) override
    {
        return m_reflectable->getAnyValue(allocator);
    }

    AnyHolder<ALLOC> getAnyValue() const override
    {
        return getAnyValue(ALLOC());
    }

    AnyHolder<ALLOC> getAnyValue() override
    {
        return getAnyValue(ALLOC());
    }

    // exact checked getters
    bool getBool() const override { return m_reflectable->getBool(); }
    int8_t getInt8() const override { return m_reflectable->getInt8(); }
    int16_t getInt16() const override { return m_reflectable->getInt16(); }
    int32_t getInt32() const override { return m_reflectable->getInt32(); }
    int64_t getInt64() const override { return m_reflectable->getInt64(); }
    uint8_t getUInt8() const override { return m_reflectable->getUInt8(); }
    uint16_t getUInt16() const override { return m_reflectable->getUInt16(); }
    uint32_t getUInt32() const override { return m_reflectable->getUInt32(); }
    uint64_t getUInt64() const override { return m_reflectable->getUInt64(); }
    float getFloat() const override { return m_reflectable->getFloat(); }
    double getDouble() const override { return m_reflectable->getDouble(); }
    Span<const uint8_t> getBytes() const override { return m_reflectable->getBytes(); }
    StringView getStringView() const override { return m_reflectable->getStringView(); }
    const BasicBitBuffer<ALLOC>& getBitBuffer() const override { return m_reflectable->getBitBuffer(); }

    // convenience conversions
    int64_t toInt() const override { return m_reflectable->toInt(); }
    uint64_t toUInt() const override { return m_reflectable->toUInt(); }
    double toDouble() const override { return m_reflectable->toDouble(); }
    string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator) const override
    {
        return m_reflectable->toString(allocator);
    }
    string<RebindAlloc<ALLOC, char>> toString() const override
    {
        return toString(ALLOC());
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
        return std::allocate_shared<BoolReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getInt8(int8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int8Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getInt16(int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getInt32(int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getInt64(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getUInt8(uint8_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt8Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getUInt16(uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getUInt32(uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getUInt64(uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64Reflectable<ALLOC>>(allocator, value);
    }

    template <typename T, typename std::enable_if<std::is_signed<T>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getFixedSignedBitField(
            T value, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedSignedBitFieldReflectable<ALLOC, T>>(allocator, value, bitSize);
    }

    template <typename T, typename std::enable_if<std::is_unsigned<T>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitField(
            T value, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedUnsignedBitFieldReflectable<ALLOC, T>>(allocator, value, bitSize);
    }

    template <typename T, typename std::enable_if<std::is_signed<T>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            T value, uint8_t maxBitSize, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicSignedBitFieldReflectable<ALLOC, T>>(
                allocator, value, maxBitSize, dynamicBitSize);
    }

    // for dynamic signed bit field given by a type reference (e.g. parameter, function return type)
    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitField(
            int64_t value, uint8_t maxBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize != 64)
        {
            throw CppRuntimeException("ReflectableFactory::getDynamicSignedBitField - ") <<
                    "maxBitSize != 64 for referenced dynamic bit field!";
        }

        return getDynamicSignedBitField(value, maxBitSize, maxBitSize, allocator);
    }

    template <typename T, typename std::enable_if<std::is_unsigned<T>::value, int>::type = 0>
    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            T value, uint8_t maxBitSize, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicUnsignedBitFieldReflectable<ALLOC, T>>(
                allocator, value, maxBitSize, dynamicBitSize);
    }

    // for dynamic unsigned bit field given a by type reference (e.g. parameter, function return type)
    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitField(
            uint64_t value, uint8_t maxBitSize, const ALLOC& allocator = ALLOC())
    {
        if (maxBitSize != 64)
        {
            throw CppRuntimeException("ReflectableFactory::getDynamicUnsignedBitField - ") <<
                    "maxBitSize != 64 for referenced dynamic bit field!";
        }

        return getDynamicUnsignedBitField(value, maxBitSize, maxBitSize, allocator);
    }

    static IBasicReflectablePtr<ALLOC> getVarInt16(
            int16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt16Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarInt32(
            int32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt32Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarInt64(
            int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt64Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarInt(int64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarIntReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt16(
            uint16_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt16Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt32(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt32Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt64(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt64Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarUInt(
            uint64_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUIntReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getVarSize(
            uint32_t value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarSizeReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getFloat16(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float16Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getFloat32(float value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float32Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getFloat64(double value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float64Reflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getBytes(Span<const uint8_t> value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BytesReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getString(StringView value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringReflectable<ALLOC>>(allocator, value);
    }

    static IBasicReflectablePtr<ALLOC> getBitBuffer(
            const BasicBitBuffer<ALLOC>& value, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitBufferReflectable<ALLOC>>(allocator, value);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getBoolArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BoolReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getBoolArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BoolReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getInt8Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int8ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getInt8Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int8ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getInt16Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getInt16Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int16ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getInt32Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getInt32Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int32ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getInt64Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getInt64Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Int64ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getUInt8Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt8ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getUInt8Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt8ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getUInt16Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getUInt16Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt16ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getUInt32Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getUInt32Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt32ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getUInt64Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getUInt64Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<UInt64ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getFixedSignedBitFieldArray(
            const RAW_ARRAY& rawArray, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedSignedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, bitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getFixedSignedBitFieldArray(
                RAW_ARRAY& rawArray, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedSignedBitFieldReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, bitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getFixedUnsignedBitFieldArray(
            const RAW_ARRAY& rawArray, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedUnsignedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, bitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getFixedUnsignedBitFieldArray(
            RAW_ARRAY& rawArray, uint8_t bitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<FixedUnsignedBitFieldReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, bitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getDynamicSignedBitFieldArray(
            const RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize,
            const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicSignedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, maxBitSize, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getDynamicSignedBitFieldArray(
            RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicSignedBitFieldReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, maxBitSize, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getDynamicUnsignedBitFieldArray(
            const RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize,
            const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicUnsignedBitFieldReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, maxBitSize, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getDynamicUnsignedBitFieldArray(
            RAW_ARRAY& rawArray, uint8_t maxBitSize, uint8_t dynamicBitSize, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<DynamicUnsignedBitFieldReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray, maxBitSize, dynamicBitSize);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarInt16Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt16ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarInt16Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt16ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarInt32Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt32ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarInt32Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt32ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarInt64Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt64ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarInt64Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarInt64ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarIntArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarIntReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarIntArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarIntReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarUInt16Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt16ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarUInt16Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt16ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarUInt32Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt32ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarUInt32Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt32ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarUInt64Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt64ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarUInt64Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUInt64ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarUIntArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUIntReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarUIntArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarUIntReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getVarSizeArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarSizeReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getVarSizeArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<VarSizeReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getFloat16Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float16ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getFloat16Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float16ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getFloat32Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float32ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getFloat32Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float32ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getFloat64Array(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float64ReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getFloat64Array(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<Float64ReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getBytesArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BytesReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getBytesArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BytesReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getStringArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getStringArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<StringReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectableConstPtr<ALLOC> getBitBufferArray(
            const RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitBufferReflectableConstArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
    }

    template <typename RAW_ARRAY>
    static IBasicReflectablePtr<ALLOC> getBitBufferArray(RAW_ARRAY& rawArray, const ALLOC& allocator = ALLOC())
    {
        return std::allocate_shared<BitBufferReflectableArray<ALLOC, RAW_ARRAY>>(
                allocator, allocator, rawArray);
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
ReflectableBase<ALLOC>::~ReflectableBase() = default;

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
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::initialize(const vector<AnyHolder<ALLOC>, ALLOC>&)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no type arguments!";
}

template <typename ALLOC>
size_t ReflectableBase<ALLOC>::initializeOffsets(size_t)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
size_t ReflectableBase<ALLOC>::initializeOffsets()
{
    return initializeOffsets(0);
}

template <typename ALLOC>
size_t ReflectableBase<ALLOC>::bitSizeOf(size_t) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not implemented!";
}

template <typename ALLOC>
size_t ReflectableBase<ALLOC>::bitSizeOf() const
{
    return bitSizeOf(0);
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::write(BitStreamWriter&) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not implemented!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no fields to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no fields to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::createField(StringView)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no fields to create!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no fields to set!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no parameters to get!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no parameters to get!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no functions to call!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' has no functions to call!";
}

template <typename ALLOC>
StringView ReflectableBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is neither choice nor union!";
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
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::resize(size_t)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::at(size_t) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::at(size_t)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableBase<ALLOC>::operator[](size_t) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableBase<ALLOC>::operator[](size_t)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::setAt(const AnyHolder<ALLOC>&, size_t)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
void ReflectableBase<ALLOC>::append(const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not an array!";
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableBase<ALLOC>::getAnyValue(const ALLOC&) const
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not implemented!";
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableBase<ALLOC>::getAnyValue(const ALLOC&)
{
    throw CppRuntimeException("Type '") << getTypeInfo().getSchemaName() << "' is not implemented!";
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableBase<ALLOC>::getAnyValue() const
{
    return getAnyValue(ALLOC());
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableBase<ALLOC>::getAnyValue()
{
    return getAnyValue(ALLOC());
}

template <typename ALLOC>
bool ReflectableBase<ALLOC>::getBool() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not boolean type!";
}

template <typename ALLOC>
int8_t ReflectableBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not int8 type!";
}

template <typename ALLOC>
int16_t ReflectableBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not int16 type!";
}

template <typename ALLOC>
int32_t ReflectableBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not int32 type!";
}

template <typename ALLOC>
int64_t ReflectableBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not int64 type!";
}

template <typename ALLOC>
uint8_t ReflectableBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not uint8 type!";
}

template <typename ALLOC>
uint16_t ReflectableBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not uint16 type!";
}

template <typename ALLOC>
uint32_t ReflectableBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not uint32 type!";
}

template <typename ALLOC>
uint64_t ReflectableBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not uint64 type!";
}

template <typename ALLOC>
float ReflectableBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not float type!";
}

template <typename ALLOC>
double ReflectableBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not double type!";
}

template <typename ALLOC>
Span<const uint8_t> ReflectableBase<ALLOC>::getBytes() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not bytes type!";
}

template <typename ALLOC>
StringView ReflectableBase<ALLOC>::getStringView() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not string type!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& ReflectableBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException("'") << getTypeInfo().getSchemaName() << "' is not an extern type!";
}

template <typename ALLOC>
int64_t ReflectableBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Conversion from '") << getTypeInfo().getSchemaName() <<
            "' to signed integer is not available!";
}

template <typename ALLOC>
uint64_t ReflectableBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Conversion from '") << getTypeInfo().getSchemaName() <<
            "' to unsigned integer is not available!";
}

template <typename ALLOC>
double ReflectableBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Conversion from '") << getTypeInfo().getSchemaName() <<
            "' to double is not available!";
}

template <typename ALLOC>
string<ALLOC> ReflectableBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Conversion from '") << getTypeInfo().getSchemaName() <<
            "' to string is not available!";
}

template <typename ALLOC>
string<ALLOC> ReflectableBase<ALLOC>::toString() const
{
    return toString(ALLOC());
}

template <typename ALLOC>
void ReflectableConstAllocatorHolderBase<ALLOC>::initializeChildren()
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
void ReflectableConstAllocatorHolderBase<ALLOC>::initialize(const vector<AnyHolder<ALLOC>, ALLOC>&)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
size_t ReflectableConstAllocatorHolderBase<ALLOC>::initializeOffsets(size_t)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
void ReflectableConstAllocatorHolderBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableConstAllocatorHolderBase<ALLOC>::getAnyValue(const ALLOC&)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is constant!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::initializeChildren()
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::initialize(const vector<AnyHolder<ALLOC>, ALLOC>&)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
size_t ReflectableArrayBase<ALLOC>::initializeOffsets(size_t)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
size_t ReflectableArrayBase<ALLOC>::bitSizeOf(size_t) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::write(BitStreamWriter&) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::getField(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::getField(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::createField(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
void ReflectableArrayBase<ALLOC>::setField(StringView, const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::getParameter(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::getParameter(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::callFunction(StringView) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::callFunction(StringView)
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
StringView ReflectableArrayBase<ALLOC>::getChoice() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
IBasicReflectableConstPtr<ALLOC> ReflectableArrayBase<ALLOC>::operator[](size_t index) const
{
    return this->at(index);
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableArrayBase<ALLOC>::operator[](size_t index)
{
    return this->at(index);
}

template <typename ALLOC>
bool ReflectableArrayBase<ALLOC>::getBool() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
int8_t ReflectableArrayBase<ALLOC>::getInt8() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
int16_t ReflectableArrayBase<ALLOC>::getInt16() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
int32_t ReflectableArrayBase<ALLOC>::getInt32() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
int64_t ReflectableArrayBase<ALLOC>::getInt64() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
uint8_t ReflectableArrayBase<ALLOC>::getUInt8() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
uint16_t ReflectableArrayBase<ALLOC>::getUInt16() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
uint32_t ReflectableArrayBase<ALLOC>::getUInt32() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
uint64_t ReflectableArrayBase<ALLOC>::getUInt64() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
float ReflectableArrayBase<ALLOC>::getFloat() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
double ReflectableArrayBase<ALLOC>::getDouble() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
Span<const uint8_t> ReflectableArrayBase<ALLOC>::getBytes() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
StringView ReflectableArrayBase<ALLOC>::getStringView() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
const BasicBitBuffer<ALLOC>& ReflectableArrayBase<ALLOC>::getBitBuffer() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
int64_t ReflectableArrayBase<ALLOC>::toInt() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
uint64_t ReflectableArrayBase<ALLOC>::toUInt() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
double ReflectableArrayBase<ALLOC>::toDouble() const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
string<ALLOC> ReflectableArrayBase<ALLOC>::toString(const ALLOC&) const
{
    throw CppRuntimeException("Reflectable is an array '") << getTypeInfo().getSchemaName() << "[]'!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::resize(size_t)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstArrayBase<ALLOC>::at(size_t)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> ReflectableConstArrayBase<ALLOC>::operator[](size_t)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::setAt(const AnyHolder<ALLOC>&, size_t)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

template <typename ALLOC>
void ReflectableConstArrayBase<ALLOC>::append(const AnyHolder<ALLOC>&)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

template <typename ALLOC>
AnyHolder<ALLOC> ReflectableConstArrayBase<ALLOC>::getAnyValue(const ALLOC&)
{
    throw CppRuntimeException("Reflectable '") << getTypeInfo().getSchemaName() << "' is a constant array!";
}

} // namespace zserio

#endif // ZSERIO_REFLECTABLE_H_INC
