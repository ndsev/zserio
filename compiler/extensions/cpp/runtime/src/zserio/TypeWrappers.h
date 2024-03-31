#pragma once
#include <zserio/Types.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/BitStreamReader.h>
#include <zserio/String.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/ITypeInfo.h>
#include <zserio/AllocatorPropagatingCopy.h>
#include <zserio/pmr/PolymorphicAllocator.h>
#include <zserio/ArrayTraits.h>
#include <zserio/Reflectable.h>

// implicit ctor and onversion operators for easy usage
// and constants =assignments

namespace zserio
{

enum VARTYPE : int {
    VAR = 1000,
    VAR16, 
    VAR32,
    VAR64,
    VARSIZE,
};

/**
* Integral type wrapper
* 
* \param T value_type which appears in its API
*
* \param N number of stored bits or one of the VAR enums
*/
template <class T, int N = 8*sizeof(T)>
struct Integer 
{
    using value_type = T;
    using allocator_type = ::std::allocator<uint8_t>;

    explicit Integer(const allocator_type& = {}) 
        : m_value_()
    {
    }
    /*explicit*/ Integer(T v)
        : m_value_(v)
    {}
    explicit Integer(zserio::BitStreamReader& in, const allocator_type & = {})
    {
        read(in);
    }
    Integer(const Integer& a)
        : m_value_(a.m_value_)
    {}
    Integer(zserio::PropagateAllocatorT, const Integer& a, const allocator_type& allocator)
        : m_value_(::zserio::allocatorPropagatingCopy(a.m_value_, allocator))
    {}
    T get() const
    {
        return m_value_;
    }
    void set(T value)
    {
        m_value_ = value;
    }
    operator T () const 
    {
        return m_value_;
    }
    /*bool operator== (const Integer& c) const
    {
        return m_value_ == c.m_value_;
    }
    bool operator< (const Integer& c) const
    {
        return m_value_ < c.m_value_;
    }*/
    size_t bitSizeOf(size_t bitPosition) const
    {
        return bitSizeOfImpl(std::bool_constant<N >= VAR>());
    }
    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }
    void write(::zserio::BitStreamWriter& out) const
    {
        writeImpl(out, std::bool_constant<N >= VAR>());
    }
    void read(::zserio::BitStreamReader& in)
    {
        readImpl(in, std::bool_constant<N >= VAR>());
    }
    //todo: ZserioPackingContext

private:
    size_t bitSizeOfImpl(std::false_type /*fixed*/)
    {
        return UINT8_C(N);
    }
    size_t bitSizeOfImpl(std::true_type /*variable*/)
    {
        return bitSizeOfVar(std::is_signed<T>(), std::integral_constant<int, N>());
    }
    size_t bitSizeOfVar(std::false_type /*signed*/, std::integral_constant<int, VAR>)
    {
        return zserio::bitSizeOfVarUInt(m_value_);
    }
    size_t bitSizeOfVar(std::false_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        return zserio::bitSizeOfVarUInt16(m_value_);
    }
    size_t bitSizeOfVar(std::false_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        return zserio::bitSizeOfVarUInt32(m_value_);
    }
    size_t bitSizeOfVar(std::false_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        return zserio::bitSizeOfVarUInt64(m_value_);
    }
    size_t bitSizeOfVar(std::false_type /*signed*/, std::integral_constant<int, VARSIZE>)
    {
        return zserio::bitSizeOfVarSize(m_value_);
    }
    size_t bitSizeOfVar(std::true_type /*signed*/, std::integral_constant<int, VAR>)
    {
        return zserio::bitSizeOfVarInt(m_value_);
    }
    size_t bitSizeOfVar(std::true_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        return zserio::bitSizeOfVarInt16(m_value_);
    }
    size_t bitSizeOfVar(std::true_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        return zserio::bitSizeOfVarInt32(m_value_);
    }
    size_t bitSizeOfVar(std::true_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        return zserio::bitSizeOfVarInt64(m_value_);
    }
    
    void writeImpl(::zserio::BitStreamWriter& out, std::false_type /*fixed*/)
    {
        writeFixed(out, std::is_signed<T>(), std::bool_constant<N <= 32>());
    }
    void writeImpl(::zserio::BitStreamWriter& out, std::true_type /*variable*/)
    {
        writeVar(out, std::is_signed<T>(), std::integral_constant<int, N>());
    }
    void writeFixed(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::false_type /*small*/) const
    {
        out.writeBits(m_value_, UINT8_C(N));
    }
    void writeFixed(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::true_type /*big*/) const
    {
        out.writeBits64(m_value_, UINT8_C(N));
    }
    void writeFixed(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::false_type /*small*/) const
    {
        out.writeSignedBits(m_value_, UINT8_C(N));
    }
    void writeFixed(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::true_type /*big*/) const
    {
        out.writeSignedBits64(m_value_, UINT8_C(N));
    }
    void writeVar(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::integral_constant<int, VAR>)
    {
        out.writeVarUInt(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        out.writeVarUInt16(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        out.writeVarUInt32(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        out.writeVarUInt64(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::false_type /*signed*/, std::integral_constant<int, VARSIZE>)
    {
        out.writeVarSize(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::integral_constant<int, VAR>)
    {
        out.writeVarInt(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        out.writeVarInt16(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        out.writeVarInt32(m_value_);
    }
    void writeVar(::zserio::BitStreamWriter& out, std::true_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        out.writeVarInt64(m_value_);
    }

    void readImpl(::zserio::BitStreamReader& in, std::false_type /*fixed*/)
    {
        readFixed(in, std::is_signed<T>(), std::bool_constant<N <= 32>());
    }
    void readImpl(::zserio::BitStreamReader& in, std::true_type /*variable*/)
    {
        readVar(in, std::is_signed<T>(), std::integral_constant<int, N>());
    }
    void readFixed(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::false_type /*small*/) const
    {
        m_value_ = in.readBits(m_value_, UINT8_C(N));
    }
    void readFixed(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::true_type /*big*/) const
    {
        m_value_ = in.readBits64(m_value_, UINT8_C(N));
    }
    void readFixed(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::false_type /*small*/) const
    {
        m_value_ = in.readSignedBits(m_value_, UINT8_C(N));
    }
    void readFixed(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::true_type /*big*/) const
    {
        m_value_ = in.readSignedBits64(m_value_, UINT8_C(N));
    }
    void readVar(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::integral_constant<int, VAR>)
    {
        m_value_ = in.readVarUInt(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        m_value_ = in.readVarUInt16(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        m_value_ = in.readVarUInt32(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        m_value_ = in.readVarUInt64(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::false_type /*signed*/, std::integral_constant<int, VARSIZE>)
    {
        m_value_ = in.readVarSize(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::integral_constant<int, VAR>)
    {
        m_value_ = in.readVarInt(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::integral_constant<int, VAR16>)
    {
        m_value_ = in.readVarInt16(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::integral_constant<int, VAR32>)
    {
        m_value_ = in.readVarInt32(m_value_);
    }
    void readVar(::zserio::BitStreamReader& in, std::true_type /*signed*/, std::integral_constant<int, VAR64>)
    {
        m_value_ = in.readVarInt64(m_value_);
    }

    T m_value_;
};

template <class T, int N>
inline uint32_t calcHashCode(uint32_t seed, const Integer<T, N>& a)
{
    return zserio::calcHashCode(seed, a.get());
}

template <class T, int N>
inline CppRuntimeException& operator<<(CppRuntimeException& exception, const Integer<T, N>& value)
{
    return exception << static_cast<Integer<T, N>::value_type>(value);
}

/**
* Float type wrapper 
*/
using Boolean = Integer<bool, 1>;

/**
* Float type wrapper
* 
* \param T value_type which appears in its API
*
* \param N unmber of stored bits
*/
template <class T, int N = 8*sizeof(T)>
struct Float
{
    using value_type = T;
    using allocator_type = ::std::allocator<uint8_t>;
    
    explicit Float(const allocator_type& = {}) 
        : m_value_()
    {
    }
    /*explicit*/ Float(T v)
        : m_value_(v)
    {}
    explicit Float(zserio::BitStreamReader& in, const allocator_type & = {})
    {
        read(in);
    }
    Float(const Float& a)
        : m_value_(a.m_value_)
    {}
    Float(zserio::PropagateAllocatorT, const Float& a, const allocator_type& allocator)
        : m_value_(::zserio::allocatorPropagatingCopy(a.m_value_, allocator))
    {}
    T get() const
    {
        return m_value_;
    }
    void set(T value)
    {
        m_value_ = value;
    }
    operator T () const 
    {
        return m_value_;
    }
    /*bool operator== (const Float& c) const
    {
        return m_value_ == c.m_value_;
    }
    bool operator< (const Float& c) const
    {
        return m_value_ < c.m_value_;
    }*/
    size_t bitSizeOf(size_t bitPosition) const
    {
        return UINT8_C(N);
    }
    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + UINT8_C(N);
    }
    void write(::zserio::BitStreamWriter& out) const
    {
        writeImpl(out, std::is_signed<T>, std::integral_constant<int, N>());
    }
    void read(::zserio::BitStreamReader& in)
    {
        readImpl(in, std::integral_constant<int, N>());
    }
    //todo: ZserioPackingContext

private:
    void writeImpl(::zserio::BitStreamWriter& out, std::integral_constant<int, 16>)
    {
        out.writeFloat16(m_value_);
    }
    void writeImpl(::zserio::BitStreamWriter& out, std::integral_constant<int, 32>)
    {
        out.writeFloat32(m_value_);
    }
    void writeImpl(::zserio::BitStreamWriter& out, std::integral_constant<int, 64>)
    {
        out.writeFloat64(m_value_);
    }
    void readImpl(::zserio::BitStreamReader& in, std::integral_constant<int, 16>)
    {
        m_value_ = in.readFloat16(m_value_);
    }
    void readImpl(::zserio::BitStreamReader& in, std::integral_constant<int, 32>)
    {
       m_value_ = in.readFloat32(m_value_);
    }
    void readImpl(::zserio::BitStreamReader& in, std::integral_constant<int, 64>)
    {
        m_value_ = in.readFloat64(m_value_);
    }  

    T m_value_;
};

template <class T, int N>
inline uint32_t calcHashCode(uint32_t seed, const Float<T, N>& a)
{
    return zserio::calcHashCode(seed, a.get());
}

template <class T, int N>
inline CppRuntimeException& operator<<(CppRuntimeException& exception, const Float<T, N>& value)
{
    return exception << static_cast<Float<T, N>::value_type>(value);
}


/**
* String type wrapper
*/
template <class ALLOC = std::allocator<char>>
struct BasicString
{
    using value_type = zserio::string<ALLOC>;
    using allocator_type = ALLOC;

    explicit BasicString(const allocator_type& allocator = {})
        : m_value_(allocator)
    {}
    /* template <class ALLOC>
    explicit BasicString(const zserio::string<ALLOC>& s)
        : m_value_(s)
    {}*/
    //template <class ALLOC>
    explicit BasicString(zserio::string<ALLOC>&& s) noexcept
        : m_value_(std::move(s))
    {}
    explicit BasicString(const zserio::string<ALLOC>& s)
        : m_value_(s)
    {}
    //template <class ALLOC>
    explicit BasicString(const char* s, const ALLOC& allocator)
        : m_value_(s, allocator)
    {}
    explicit BasicString(zserio::BitStreamReader& in, const allocator_type& allocator = {})
    {
        read(in, allocator);
    }
    BasicString(const BasicString& s) 
        : m_value_(s.m_value_)
    {}
    BasicString(BasicString&& s) noexcept
        : m_value_(std::move(s.m_value_))
    {}
    BasicString(zserio::PropagateAllocatorT, const BasicString& a, const allocator_type& allocator)
        : m_value_(::zserio::allocatorPropagatingCopy(a.m_value_, allocator))
    {}
    BasicString& operator= (const BasicString& s)
    {
        m_value_ = s.m_value_;
        return *this;
    }
    BasicString& operator= (BasicString&& s) noexcept
    {
        m_value_ = std::move(s.m_value_);
        return *this;
    }
    BasicString& operator= (const char* s)
    {
        m_value_ = s;
    }
    const value_type& get() const
    {
        return m_value_;
    }
    value_type& get()
    {
        return m_value_;
    }
    void set(const value_type& value)
    {
        m_value_ = value;
    }
    void set(value_type&& value)
    {
        m_value_ = std::move(value);
    }
    operator const value_type& () const
    {
        return m_value_;
    }
    operator StringView() const
    {
        return m_value_;
    }
    const char* c_str() const
    {
        return m_value_.c_str();
    }
    bool operator== (const BasicString& c) const
    {
        return m_value_ == c.m_value_;
    }
    bool operator< (const BasicString& c) const
    {
        return m_value_ < c.m_value_;
    }
    size_t bitSizeOf(size_t bitPosition) const
    {
        return ::zserio::bitSizeOfString(m_value_);
    }
    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + ::zserio::bitSizeOfString(m_value_);
    }
    void write(::zserio::BitStreamWriter& out) const
    {
        out.writeString(m_value_);
    }
    void read(::zserio::BitStreamReader& in, const allocator_type& allocator)
    {
        m_value_ = static_cast<value_type>(in.readString(allocator));
    }
    //todo: ZserioPackingContext
private:
    value_type m_value_;
};

template <class ALLOC>
inline uint32_t calcHashCode(uint32_t seed, const BasicString<ALLOC>& a)
{
    return zserio::calcHashCode(seed, a.get());
}

template <class ALLOC>
inline CppRuntimeException& operator<<(CppRuntimeException& exception, const BasicString<ALLOC>& value)
{
    return exception << value.c_str();
}

using String = BasicString<>;

namespace pmr
{
    using String = zserio::BasicString<PropagatingPolymorphicAllocator<char>>;
}

// ArrayTraits.h

template <class T, int N>
struct VarIntNNArrayTraits<Integer<T, N>> : VarIntNNArrayTraits<T>
{};

template <class T, int N>
struct VarIntArrayTraits<Integer<T, N>> : VarIntArrayTraits<T>
{};

namespace detail
{

template <class T, int N>
inline void write_bits(BitStreamWriter& out, Integer<T, N> val, uint8_t numBits)
{
    write_bits(out, (T)val, numBits);
}

template <class T, int N>
inline Integer<T, N> read_bits_impl(Integer<T, N>*, BitStreamReader& in, uint8_t numBits)
{
    return read_bits<T>(in, numBits);
}

} // namespace detail

// Reflectable.h

template <typename ALLOC, typename T, int N>
class FixedSignedBitFieldReflectable<ALLOC, zserio::Integer<T, N>> 
    : public FixedSignedBitFieldReflectable<ALLOC, T>
{
public:
    using FixedSignedBitFieldReflectable<ALLOC, T>::FixedSignedBitFieldReflectable;
};

template <typename ALLOC, typename T, int N>
class FixedUnsignedBitFieldReflectable<ALLOC, zserio::Integer<T, N>> 
    : public FixedUnsignedBitFieldReflectable<ALLOC, T>
{
public:
    using FixedUnsignedBitFieldReflectable<ALLOC, T>::FixedUnsignedBitFieldReflectable;
};

template <typename ALLOC, typename T, int N>
class DynamicSignedBitFieldReflectable<ALLOC, zserio::Integer<T, N>> 
    : public DynamicSignedBitFieldReflectable<ALLOC, T>
{
public:
    using DynamicSignedBitFieldReflectable<ALLOC, T>::DynamicSignedBitFieldReflectable;
};

template <typename ALLOC, typename T, int N>
class DynamicUnsignedBitFieldReflectable<ALLOC, zserio::Integer<T, N>> 
    : public DynamicUnsignedBitFieldReflectable<ALLOC, T>
{
public:
    using DynamicUnsignedBitFieldReflectable<ALLOC, T>::DynamicUnsignedBitFieldReflectable;
};

}
