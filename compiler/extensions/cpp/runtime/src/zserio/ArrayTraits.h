#ifndef ZSERIO_ARRAY_TRAITS_H_INC
#define ZSERIO_ARRAY_TRAITS_H_INC

#include <type_traits>
#include <string>
#include <vector>

#include "zserio/BitPositionUtil.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/Enums.h"
#include "zserio/Traits.h"
#include "zserio/SizeConvertUtil.h"
#include "zserio/DeltaContext.h"

namespace zserio
{

namespace detail
{

template <typename T>
T read_bits(BitStreamReader& in, uint8_t numBits);

template <>
inline int8_t read_bits<int8_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<int8_t>(in.readSignedBits(numBits));
}

template <>
inline int16_t read_bits<int16_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<int16_t>(in.readSignedBits(numBits));
}

template <>
inline int32_t read_bits<int32_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readSignedBits(numBits);
}

template <>
inline int64_t read_bits<int64_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readSignedBits64(numBits);
}

template <>
inline uint8_t read_bits<uint8_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<uint8_t>(in.readBits(numBits));
}

template <>
inline uint16_t read_bits<uint16_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<uint16_t>(in.readBits(numBits));
}

template <>
inline uint32_t read_bits<uint32_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readBits(numBits);
}

template <>
inline uint64_t read_bits<uint64_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readBits64(numBits);
}

template <typename T>
void write_bits(BitStreamWriter& out, T value, uint8_t numBits);

template <>
inline void write_bits<int8_t>(BitStreamWriter& out, int8_t value, uint8_t numBits)
{
    out.writeSignedBits(static_cast<int32_t>(value), numBits);
}

template <>
inline void write_bits<int16_t>(BitStreamWriter& out, int16_t value, uint8_t numBits)
{
    out.writeSignedBits(static_cast<int32_t>(value), numBits);
}

template <>
inline void write_bits<int32_t>(BitStreamWriter& out, int32_t value, uint8_t numBits)
{
    out.writeSignedBits(value, numBits);
}

template <>
inline void write_bits<int64_t>(BitStreamWriter& out, int64_t value, uint8_t numBits)
{
    out.writeSignedBits64(value, numBits);
}

template <>
inline void write_bits<uint8_t>(BitStreamWriter& out, uint8_t value, uint8_t numBits)
{
    out.writeBits(static_cast<uint32_t>(value), numBits);
}

template <>
inline void write_bits<uint16_t>(BitStreamWriter& out, uint16_t value, uint8_t numBits)
{
    out.writeBits(static_cast<uint32_t>(value), numBits);
}

template <>
inline void write_bits<uint32_t>(BitStreamWriter& out, uint32_t value, uint8_t numBits)
{
    out.writeBits(value, numBits);
}

template <>
inline void write_bits<uint64_t>(BitStreamWriter& out, uint64_t value, uint8_t numBits)
{
    out.writeBits64(value, numBits);
}

} // namespace detail

/**
 * Array traits for bit field Zserio types.
 *
 * These traits are used for all fixed bit fields (int:N or bit:N) or for dynamic bit fields which
 * have constant bit size available during generation (int<N> or bit<N>).
 */
template <typename T, uint8_t NUM_BITS>
class BitFieldArrayTraits
{
public:
    /** Element type. */
    using ElementType = T;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return NUM_BITS;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType)
    {
        return bitPosition + NUM_BITS;
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return detail::read_bits<T>(in, NUM_BITS);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        detail::write_bits(out, element, NUM_BITS);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for dynamic bit field Zserio types with dynamic bit size (int<bitSize> or bit<bitSize>).
 *
 * Used for dynamic bit fields with length known at compile time - i.e. which depends on a constant expression
 * and doesn't need an owner.
 */
template <typename T, typename ELEMENT_BIT_SIZE, typename = void>
class DynamicBitFieldArrayTraits
{
public:
    /** Element type. */
    using ElementType = T;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return ELEMENT_BIT_SIZE::get();
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType)
    {
        return bitPosition + bitSizeOf();
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return detail::read_bits<T>(in, ELEMENT_BIT_SIZE::get());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        detail::write_bits(out, element, ELEMENT_BIT_SIZE::get());
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for dynamic bit field Zserio types with dynamic bit size (int<bitSize> or bit<bitSize>).
 *
 * Specialization for dynamic bit fields which length depends on an arbitrary expression - i.e. which may
 * need an owner.
 */
template <typename T, typename ELEMENT_BIT_SIZE>
class DynamicBitFieldArrayTraits<T, ELEMENT_BIT_SIZE,
        typename std::enable_if<has_owner_type<ELEMENT_BIT_SIZE>::value>::type>
{
public:
    /** Element type. */
    using ElementType = T;

    /** Typedef for the array's owner type. */
    using OwnerType = typename ELEMENT_BIT_SIZE::OwnerType;

    /**
     * Calculates bit size of the array element.
     *
     * \param owner Owner of the current array.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(const OwnerType& owner)
    {
        return ELEMENT_BIT_SIZE::get(owner);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param owner Owner of the current array.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(const OwnerType& owner, ElementType)
    {
        return bitSizeOf(owner);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param owner Owner of the current array.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(const OwnerType& owner, size_t, ElementType)
    {
        return bitSizeOf(owner);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param owner Owner of the current array.
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(const OwnerType& owner, size_t bitPosition, ElementType)
    {
        return bitPosition + bitSizeOf(owner);
    }

    /**
     * Reads the single array element.
     *
     * \param owner Owner of the current array.
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(const OwnerType& owner, BitStreamReader& in, size_t = 0)
    {
        return detail::read_bits<T>(in, ELEMENT_BIT_SIZE::get(owner));
    }

    /**
     * Writes the single array element.
     *
     * \param owner Owner of the current array.
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(const OwnerType& owner, BitStreamWriter& out, ElementType element)
    {
        detail::write_bits(out, element, ELEMENT_BIT_SIZE::get(owner));
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for fixed integer Zserio types (int16, uint16, int32, uint32, etc...).
 */
template <typename T>
struct StdIntArrayTraits
{
    /** Element type. */
    using ElementType = T;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return NUM_BITS;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType)
    {
        return bitPosition + NUM_BITS;
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return detail::read_bits<T>(in, NUM_BITS);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        detail::write_bits(out, element, NUM_BITS);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;

private:
    static constexpr uint8_t NUM_BITS = sizeof(T) * 8;
};

/**
 * Array traits for variable integer Zserio types (varint16, varuint16, etc...).
 */
template <typename T>
struct VarIntNNArrayTraits;

/**
 * Array traits specialization for Zserio varint16 type.
 */
template <>
struct VarIntNNArrayTraits<int16_t>
{
    /** Element type. */
    using ElementType = int16_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarInt16(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarInt16();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarInt16(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varint32 type.
 */
template <>
struct VarIntNNArrayTraits<int32_t>
{
    /** Element type. */
    using ElementType = int32_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarInt32(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarInt32();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarInt32(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varint64 type.
 */
template <>
struct VarIntNNArrayTraits<int64_t>
{
    /** Element type. */
    using ElementType = int64_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarInt64(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarInt64();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarInt64(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint16 type.
 */
template <>
struct VarIntNNArrayTraits<uint16_t>
{
    /** Element type. */
    using ElementType = uint16_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarUInt16(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarUInt16();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarUInt16(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint32 type.
 */
template <>
struct VarIntNNArrayTraits<uint32_t>
{
    /** Element type. */
    using ElementType = uint32_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarUInt32(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarUInt32();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarUInt32(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint64 type.
 */
template <>
struct VarIntNNArrayTraits<uint64_t>
{
    /** Element type. */
    using ElementType = uint64_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarUInt64(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarUInt64();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarUInt64(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for big variable integer Zserio types (varint, varuint).
 */
template <typename T>
struct VarIntArrayTraits;

/**
 * Array traits specialization for Zserio varint type.
 */
template <>
struct VarIntArrayTraits<int64_t>
{
    /** Element type. */
    using ElementType = int64_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarInt(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarInt();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarInt(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint type.
 */
template <>
struct VarIntArrayTraits<uint64_t>
{
    /** Element type. */
    using ElementType = uint64_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarUInt(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarUInt();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarUInt(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varsize type.
 */
struct VarSizeArrayTraits
{
    /** Element type. */
    using ElementType = uint32_t;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(ElementType element)
    {
        return zserio::bitSizeOfVarSize(element);
    }

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readVarSize();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeVarSize(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio float16 type.
 */
struct Float16ArrayTraits
{
    /** Element type. */
    using ElementType = float;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return 16;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readFloat16();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeFloat16(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float32 type.
 */
struct Float32ArrayTraits
{
    /** Element type. */
    using ElementType = float;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return 32;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readFloat32();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeFloat32(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float64 type.
 */
struct Float64ArrayTraits
{
    /** Element type. */
    using ElementType = double;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return 64;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readFloat64();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeFloat64(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio bool type.
 */
struct BoolArrayTraits
{
    /** Element type. */
    using ElementType = bool;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf()
    {
        return 1;
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType)
    {
        return bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return in.readBool();
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        out.writeBool(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio bytes type.
 */
template <template <typename> class ALLOC = std::allocator>
struct BasicBytesArrayTraits
{
    /** ElementType */
    using ElementType = zserio::vector<uint8_t, ALLOC<uint8_t>>;

    /** Allocator type. */
    using allocator_type = typename ElementType::allocator_type;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, const ElementType& element)
    {
        return zserio::bitSizeOfBytes(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, const ElementType& element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param rawArray Raw array to use.
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t = 0)
    {
        rawArray.emplace_back(in.readBytes(rawArray.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, const ElementType& element)
    {
        out.writeBytes(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

using BytesArrayTraits = BasicBytesArrayTraits<>;

/**
 * Array traits for Zserio string type.
 */
template <template <typename> class ALLOC = std::allocator>
struct BasicStringArrayTraits
{
    /** Element type. */
    using ElementType = zserio::string<ALLOC<char>>;

    /** Allocator type. */
    using allocator_type = typename ElementType::allocator_type;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, const ElementType& element)
    {
        return zserio::bitSizeOfString(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, const ElementType& element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param rawArray Raw array to use.
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t = 0)
    {
        rawArray.emplace_back(in.readString(rawArray.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, const ElementType& element)
    {
        out.writeString(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

using StringArrayTraits = BasicStringArrayTraits<>;

/**
 * Array traits for Zserio extern bit buffer type.
 */
template <template <typename> class ALLOC = std::allocator>
struct BasicBitBufferArrayTraits
{
    /** Element type. */
    using ElementType = BasicBitBuffer<ALLOC<uint8_t>>;

    /** Allocator type. */
    using allocator_type = typename ElementType::allocator_type;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, const ElementType& element)
    {
        return zserio::bitSizeOfBitBuffer(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, const ElementType& element)
    {
        return bitPosition + bitSizeOf(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param rawArray Raw array to use.
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t = 0)
    {
        rawArray.emplace_back(in.readBitBuffer(rawArray.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, const ElementType& element)
    {
        out.writeBitBuffer(element);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

using BitBufferArrayTraits = BasicBitBufferArrayTraits<>;

/**
 * Array traits for Zserio enumeration type.
 */
template <typename T>
struct EnumArrayTraits
{
    /** Element type. */
    using ElementType = T;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return zserio::bitSizeOf(element);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return zserio::initializeOffsets(bitPosition, element);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return zserio::read<ElementType>(in);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        zserio::write(out, element);
    }

    // Be aware that T can be varuint, so bitSizeOf cannot return constant value.
    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio bitmask type.
 */
template <typename T>
struct BitmaskArrayTraits
{
    /** Element type. */
    using ElementType = T;

    /**
     * Calculates bit size of the array element.
     *
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, ElementType element)
    {
        return element.bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, ElementType element)
    {
        return element.initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(BitStreamReader& in, size_t = 0)
    {
        return ElementType(in);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(BitStreamWriter& out, ElementType element)
    {
        element.write(out);
    }

    // Be aware that T can be varuint, so bitSizeOf cannot return constant value.
    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio structure, choice and union types.
 */
template <typename T, typename ELEMENT_FACTORY>
class ObjectArrayTraits
{
public:
    /** Element type. */
    using ElementType = T;

    /** Allocator type. */
    using allocator_type = typename ElementType::allocator_type;

    /** Typedef for the array's owner type. */
    using OwnerType = typename ELEMENT_FACTORY::OwnerType;

    /**
     * Calculates bit size of the array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use for calculation.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(const OwnerType&, size_t bitPosition, const ElementType& element)
    {
        return element.bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param element Element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(OwnerType&, size_t bitPosition, ElementType& element)
    {
        return element.initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param owner Owner of the array.
     * \param rawArray Raw array to use.
     * \param in Bit stream reader.
     * \param index Index need in case of parameterized type which depends on the current index.
     */
    template <typename RAW_ARRAY>
    static void read(OwnerType& owner, RAW_ARRAY& rawArray,
            BitStreamReader& in, size_t index)
    {
        ELEMENT_FACTORY::create(owner, rawArray, in, index);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param element Element to write.
     */
    static void write(const OwnerType&, BitStreamWriter& out, const ElementType& element)
    {
        element.write(out);
    }

    /** Determines whether the bit size of the single element is constant. */
    static constexpr bool IS_BITSIZEOF_CONSTANT = false;
};

namespace detail
{

template <typename ARRAY_TRAITS>
struct packed_array_traits_overloaded : std::false_type
{};

template <typename T, typename ELEMENT_FACTORY>
struct packed_array_traits_overloaded<ObjectArrayTraits<T, ELEMENT_FACTORY>> : std::true_type
{};

} // namespace detail

/**
 * Packed array traits.
 *
 * Packed array traits are used for all packable built-in types. Works with a single DeltaContext.
 */
template <typename ARRAY_TRAITS, typename = void>
class PackedArrayTraits
{
public:
    /** Typedef for array traits. */
    using ArrayTraits = ARRAY_TRAITS;

    /** Element type. */
    using ElementType = typename ARRAY_TRAITS::ElementType;

    /**
     * Calls context initialization step for the current element.
     *
     * Available for traits which do not need the owner.
     *
     * \param deltaContext Delta context.
     * \param element Current element.
     */
    static void initContext(DeltaContext& deltaContext, ElementType element)
    {
        deltaContext.template init<ArrayTraits>(element);
    }

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * Available for traits which do not need the owner.
     *
     * \param deltaContext Delta context.
     * \param element Current element.
     *
     * \return Length of the array element stored in the bit stream in bits.
     */
    static size_t bitSizeOf(DeltaContext& deltaContext, size_t, ElementType element)
    {
        return deltaContext.template bitSizeOf<ArrayTraits>(element);
    }

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * Available for traits which do not need the owner.
     *
     * \param deltaContext Delta context.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Updated bit stream position which points to the first bit after this element.
     */
    static size_t initializeOffsets(DeltaContext& deltaContext, size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(deltaContext, bitPosition, element);
    }

    /**
     * Reads an element from the bit stream.
     *
     * Available for traits which do not need the owner.
     *
     * \param deltaContext Delta context.
     * \param in Bit stream reader.
     *
     * \return Read element value.
     */
    static ElementType read(DeltaContext& deltaContext, BitStreamReader& in, size_t = 0)
    {
        return deltaContext.template read<ArrayTraits>(in);
    }

    /**
     * Writes the element to the bit stream.
     *
     * Available for traits which do not need the owner.
     *
     * \param deltaContext Delta context.
     * \param out Bit stream writer.
     * \param element Element to write.
     */
    static void write(DeltaContext& deltaContext, BitStreamWriter& out, ElementType element)
    {
        deltaContext.template write<ArrayTraits>(out, element);
    }
};

/**
 * Specialization of packed array traits for traits which needs the array's owner.
 *
 * This packed array traits are used for all packable built-in types which needs the array's owner.
 */
template <typename ARRAY_TRAITS>
class PackedArrayTraits<ARRAY_TRAITS, typename std::enable_if<has_owner_type<ARRAY_TRAITS>::value>::type>
{
public:
    /** Typedef for array traits. */
    using ArrayTraits = ARRAY_TRAITS;

    /** Element type. */
    using ElementType = typename ARRAY_TRAITS::ElementType;

    /** Typedef for the array's owner type. */
    using OwnerType = typename ARRAY_TRAITS::OwnerType;

    /**
     * Calls context initialization step for the current element.
     *
     * Available for traits which need the owner.
     *
     * \param owner Owner of the current array.
     * \param deltaContext Delta context node which keeps the context.
     * \param element Current element.
     */
    static void initContext(const OwnerType& owner, DeltaContext& deltaContext, ElementType element)
    {
        deltaContext.template init<ArrayTraits>(owner, element);
    }

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * Available for traits which need the owner.
     *
     * \param owner Owner of the current array.
     * \param deltaContext Delta context.
     * \param element Current element.
     *
     * \return Length of the array element stored in the bit stream in bits.
     */
    static size_t bitSizeOf(const OwnerType& owner,
            DeltaContext& deltaContext, size_t, ElementType element)
    {
        return deltaContext.template bitSizeOf<ArrayTraits>(owner, element);
    }

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * Available for traits which need the owner.
     *
     * \param owner Owner of the current array.
     * \param deltaContext Delta context.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Updated bit stream position which points to the first bit after this element.
     */
    static size_t initializeOffsets(const OwnerType& owner,
            DeltaContext& deltaContext, size_t bitPosition, ElementType element)
    {
        return bitPosition + bitSizeOf(owner, deltaContext, bitPosition, element);
    }

    /**
     * Reads an element from the bit stream.
     *
     * Available for traits which need the owner.
     *
     * \param owner Owner of the current array.
     * \param deltaContext Delta context.
     * \param in Bit stream reader.
     *
     * \return Read element value.
     */
    static ElementType read(const OwnerType& owner, DeltaContext& deltaContext, BitStreamReader& in, size_t = 0)
    {
        return deltaContext.template read<ArrayTraits>(owner, in);
    }

    /**
     * Writes the element to the bit stream.
     *
     * Available for traits which need the owner.
     *
     * \param owner Owner of the current array.
     * \param deltaContext Delta context.
     * \param out Bit stream writer.
     * \param element Element to write.
     */
    static void write(const OwnerType& owner,
            DeltaContext& deltaContext, BitStreamWriter& out, ElementType element)
    {
        deltaContext.template write<ArrayTraits>(owner, out, element);
    }
};

/**
 * Specialization of packed array traits for Zserio enums.
 */
template <typename T>
class PackedArrayTraits<EnumArrayTraits<T>>
{
public:
    /** Typedef for array traits. */
    using ArrayTraits = EnumArrayTraits<T>;

    /** Element type. */
    using ElementType = T;

    /**
     * Calls context initialization step for the current element.
     *
     * \param deltaContext Delta context.
     * \param element Current element.
     */
    static void initContext(DeltaContext& deltaContext, ElementType element)
    {
        zserio::initPackingContext(deltaContext, element);
    }

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * \param deltaContext Delta context.
     * \param element Current element.
     *
     * \return Length of the array element stored in the bit stream in bits.
     */
    static size_t bitSizeOf(DeltaContext& deltaContext, size_t, ElementType element)
    {
        return zserio::bitSizeOf(deltaContext, element);
    }

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * \param deltaContext Delta context.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Updated bit stream position which points to the first bit after this element.
     */
    static size_t initializeOffsets(DeltaContext& deltaContext,
            size_t bitPosition, ElementType element)
    {
        return zserio::initializeOffsets(deltaContext, bitPosition, element);
    }

    /**
     * Reads an element from the bit stream.
     *
     * \param deltaContext Delta context.
     * \param in Bit stream reader.
     *
     * \return Read element value.
     */
    static ElementType read(DeltaContext& deltaContext, BitStreamReader& in, size_t = 0)
    {
        return zserio::read<ElementType>(deltaContext, in);
    }

    /**
     * Writes the element to the bit stream.
     *
     * \param deltaContext Delta context.
     * \param out Bit stream writer.
     * \param element Element to write.
     */
    static void write(DeltaContext& deltaContext, BitStreamWriter& out, ElementType element)
    {
        zserio::write(deltaContext, out, element);
    }
};

/**
 * Specialization of packed array traits for Zserio bitmasks.
 */
template <typename T>
class PackedArrayTraits<BitmaskArrayTraits<T>>
{
public:
    /** Typedef for array traits. */
    using ArrayTraits = BitmaskArrayTraits<T>;

    /** Element type. */
    using ElementType = T;

    /**
     * Calls context initialization step for the current element.
     *
     * \param deltaContext Delta context.
     */
    static void initContext(DeltaContext& deltaContext, const ElementType& element)
    {
        element.initPackingContext(deltaContext);
    }

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * \param deltaContext Delta context.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Length of the array element stored in the bit stream in bits.
     */
    static size_t bitSizeOf(DeltaContext& deltaContext, size_t bitPosition, const ElementType& element)
    {
        return element.bitSizeOf(deltaContext, bitPosition);
    }

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * \param deltaContext Delta context.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Updated bit stream position which points to the first bit after this element.
     */
    static size_t initializeOffsets(DeltaContext& deltaContext, size_t bitPosition, const ElementType& element)
    {
        return element.initializeOffsets(deltaContext, bitPosition);
    }

    /**
     * Reads an element from the bit stream.
     *
     * \param deltaContext Delta context.
     * \param in Bit stream reader.
     *
     * \return Read element.
     */
    static ElementType read(DeltaContext& deltaContext, BitStreamReader& in, size_t = 0)
    {
        return ElementType(deltaContext, in);
    }

    /**
     * Writes the element to the bit stream.
     *
     * \param deltaContext Delta context.
     * \param out Bit stream writer.
     * \param element Element to write.
     */
    static void write(DeltaContext& deltaContext, BitStreamWriter& out, const ElementType& element)
    {
        element.write(deltaContext, out);
    }
};

// note: enable_if is needed to be more specific then specialization for types which needs owner type
/**
 * Specialization of packed array traits for Zserio objects.
 */
template <typename T, typename ELEMENT_FACTORY>
class PackedArrayTraits<ObjectArrayTraits<T, ELEMENT_FACTORY>,
        typename std::enable_if<has_owner_type<ObjectArrayTraits<T, ELEMENT_FACTORY>>::value>::type>
{
public:
    /** Typedef for array traits. */
    using ArrayTraits = ObjectArrayTraits<T, ELEMENT_FACTORY>;

    /** Element type. */
    using ElementType = T;

    /** Allocator type. */
    using allocator_type = typename T::allocator_type;

    /** Typedef for the array's owner type. */
    using OwnerType = typename ArrayTraits::OwnerType;

    /**
     * Calls context initialization step for the current element.
     *
     * \param packingContext Packing context node which keeps the appropriate subtree of contexts.
     */
    template <typename PACKING_CONTEXT>
    static void initContext(const typename ArrayTraits::OwnerType&,
            PACKING_CONTEXT& packingContext, const ElementType& element)
    {
        element.initPackingContext(packingContext);
    }

    /**
     * Returns length of the array element stored in the bit stream in bits.
     *
     * \param packingContext Packing context node which keeps the appropriate subtree of contexts.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Length of the array element stored in the bit stream in bits.
     */
    template <typename PACKING_CONTEXT>
    static size_t bitSizeOf(const typename ArrayTraits::OwnerType&,
            PACKING_CONTEXT& packingContext, size_t bitPosition, const ElementType& element)
    {
        return element.bitSizeOf(packingContext, bitPosition);
    }

    /**
     * Calls indexed offsets initialization for the current element.
     *
     * \param packingContext Packing context node which keeps the appropriate subtree of contexts.
     * \param bitPosition Current bit stream position.
     * \param element Current element.
     *
     * \return Updated bit stream position which points to the first bit after this element.
     */
    template <typename PACKING_CONTEXT>
    static size_t initializeOffsets(const typename ArrayTraits::OwnerType&,
            PACKING_CONTEXT& packingContext, size_t bitPosition, ElementType& element)
    {
        return element.initializeOffsets(packingContext, bitPosition);
    }

    /**
     * Reads an element from the bit stream.
     *
     * \param owner Owner of the array.
     * \param rawArray Raw array to use.
     * \param packingContext Packing context node which keeps the appropriate subtree of contexts.
     * \param in Bit stream reader.
     * \param allocator Allocator to use.
     * \param index Index of the current element.
     */
    template <typename RAW_ARRAY, typename PACKING_CONTEXT>
    static void read(typename ArrayTraits::OwnerType& owner, RAW_ARRAY& rawArray,
            PACKING_CONTEXT& packingContext, BitStreamReader& in, size_t index)
    {
        ELEMENT_FACTORY::create(owner, rawArray, packingContext, in, index);
    }

    /**
     * Writes the element to the bit stream.
     *
     * \param packingContext Packing context node which keeps the appropriate subtree of contexts.
     * \param out Bit stream writer.
     * \param element Element to write.
     */
    template <typename PACKING_CONTEXT>
    static void write(const typename ArrayTraits::OwnerType&,
            PACKING_CONTEXT& packingContext, BitStreamWriter& out, const ElementType& element)
    {
        element.write(packingContext, out);
    }
};

} // namespace zserio

#endif // ZSERIO_ARRAY_TRAITS_H_INC
