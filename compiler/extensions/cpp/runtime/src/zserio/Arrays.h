#ifndef ZSERIO_ARRAYS_H_INC
#define ZSERIO_ARRAYS_H_INC

#include <type_traits>
#include <vector>
#include <string>

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarSizeUtil.h"
#include "zserio/PreWriteAction.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"

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
 * Array traits for bit field Zserio types (int:5, bit:5, etc...).
 */
template <typename T>
class BitFieldArrayTraits
{
public:
    /** Type of the single array element. */
    typedef T type;

    /**
     * Constructor.
     *
     * \param numBits Num bits of the array element.
     */
    explicit BitFieldArrayTraits(uint8_t numBits) : m_numBits(numBits)
    {
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    size_t bitSizeOf(size_t, type) const
    {
        return bitSizeOf();
    }

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    size_t bitSizeOf() const
    {
        return m_numBits;
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    size_t initializeOffsets(size_t bitPosition, type) const
    {
        return bitPosition + m_numBits;
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t) const
    {
        array.push_back(detail::read_bits<type>(in, m_numBits));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    void write(BitStreamWriter& out, type value) const
    {
        detail::write_bits(out, value, m_numBits);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;

private:
    uint8_t m_numBits;
};

/**
 * Array traits for fixed integer Zserio types (int16, uint16, int32, uint32, etc...).
 */
template <typename T>
struct StdIntArrayTraits
{
    /** Type of the single array element. */
    typedef T type;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type)
    {
        return bitSizeOf();
    }

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
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + NUM_BITS;
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(detail::read_bits<type>(in, NUM_BITS));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        detail::write_bits(out, value, NUM_BITS);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;

private:
    static const uint8_t NUM_BITS = sizeof(type) * 8;
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
    /** Type of the single array element. */
    typedef int16_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt16(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarInt16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt16(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;

};

/**
 * Array traits specialization for Zserio varint32 type.
 */
template <>
struct VarIntNNArrayTraits<int32_t>
{
    /** Type of the single array element. */
    typedef int32_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt32(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarInt32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt32(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varint64 type.
 */
template <>
struct VarIntNNArrayTraits<int64_t>
{
    /** Type of the single array element. */
    typedef int64_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt64(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarInt64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt64(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint16 type.
 */
template <>
struct VarIntNNArrayTraits<uint16_t>
{
    /** Type of the single array element. */
    typedef uint16_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt16(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarUInt16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt16(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint32 type.
 */
template <>
struct VarIntNNArrayTraits<uint32_t>
{
    /** Type of the single array element. */
    typedef uint32_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt32(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarUInt32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt32(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint64 type.
 */
template <>
struct VarIntNNArrayTraits<uint64_t>
{
    /** Type of the single array element. */
    typedef uint64_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt64(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarUInt64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt64(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
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
    /** Type of the single array element. */
    typedef int64_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::bitSizeOfVarInt(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarInt());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varuint type.
 */
template <>
struct VarIntArrayTraits<uint64_t>
{
    /** Type of the single array element. */
    typedef uint64_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::bitSizeOfVarUInt(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarUInt());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varsize type.
 */
struct VarSizeArrayTraits
{
    /** Type of the single array element. */
    typedef uint32_t type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarSize(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readVarSize());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarSize(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio float16 type.
 */
struct Float16ArrayTraits
{
    /** Type of the single array element. */
    typedef float type;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type)
    {
        return bitSizeOf();
    }

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
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readFloat16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat16(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float32 type.
 */
struct Float32ArrayTraits
{
    /** Type of the single array element. */
    typedef float type;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type)
    {
        return bitSizeOf();
    }

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
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readFloat32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat32(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float64 type.
 */
struct Float64ArrayTraits
{
    /** Type of the single array element. */
    typedef double type;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type)
    {
        return bitSizeOf();
    }

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
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readFloat64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat64(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio bool type.
 */
struct BoolArrayTraits
{
    /** Type of the single array element. */
    typedef bool type;

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type)
    {
        return bitSizeOf();
    }

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
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readBool());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeBool(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio string type.
 */
template <template <typename> class ALLOC = std::allocator>
class StringArrayTraits
{
public:
    /** Type of the single array element. */
    typedef std::basic_string<char, std::char_traits<char>, ALLOC<char>> type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, const type& value)
    {
        return zserio::bitSizeOfString(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, const type& value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    static void read(std::vector<type, ALLOC<type>>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readString<ALLOC<char>>(array.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, const type& value)
    {
        out.writeString(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio extern bit buffer type.
 */
template <template <typename> class ALLOC = std::allocator>
struct BitBufferArrayTraits
{
    /** Type of the single array element. */
    typedef zserio::BasicBitBuffer<ALLOC<uint8_t>> type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, const type& value)
    {
        return zserio::bitSizeOfBitBuffer(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, const type& value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    static void read(std::vector<type, ALLOC<type>>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readBitBuffer(ALLOC<uint8_t>(array.get_allocator())));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, const type& value)
    {
        out.writeBitBuffer(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio enumeration type.
 */
template <typename T>
struct EnumArrayTraits
{
    /** Type of the single array element. */
    typedef T type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOf(value);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return zserio::initializeOffsets(bitPosition, value);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.push_back(zserio::read<type>(in));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        zserio::write(out, value);
    }

    // Be aware that T can be varuint, so bitSizeOf cannot return constant value.
    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio bitmask type.
 */
template <typename T>
struct BitmaskArrayTraits
{
    /** Type of the single array element. */
    typedef T type;

    /**
     * Calculates bit size of the array element.
     *
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t, type value)
    {
        return value.bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return value.initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     */
    template <typename ALLOC>
    static void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, const type& value)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

    // Be aware that T can be varuint, so bitSizeOf cannot return constant value.
    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio structure, choice and union types.
 */
template <typename T, typename ELEMENT_FACTORY>
class ObjectArrayTraits
{
public:
    /**
     * Constructor.
     *
     * \param elementFactory Factory which knows how to create a single array element.
     */
    explicit ObjectArrayTraits(const ELEMENT_FACTORY& elementFactory) : m_elementFactory(elementFactory)
    {
    }

    /** Type of the single array element. */
    typedef T type;

    /**
     * Calculates bit size of the array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t bitPosition, const type& value)
    {
        return value.bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type& value)
    {
        return value.initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     * \param index Index need in case of parameterized type which depends on the current index.
     */
    template <typename ALLOC>
    void read(std::vector<type, ALLOC>& array, BitStreamReader& in, size_t index) const
    {
        m_elementFactory.create(array, in, index);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type& value)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;

private:
    ELEMENT_FACTORY m_elementFactory;
};

/**
 * Helper function to make ObjectArrayTraits (with template arguments deduction).
 *
 * \return Object array traits.
 */
template <typename T, typename ELEMENT_FACTORY>
ObjectArrayTraits<T, ELEMENT_FACTORY> makeObjectArrayTraits(const ELEMENT_FACTORY& elementFactory)
{
    return ObjectArrayTraits<T, ELEMENT_FACTORY>(elementFactory);
}

namespace detail
{

// dummy offset initializer used for arrays which don't need to initialize offsets
struct DummyOffsetInitializer
{
    void initializeOffset(size_t, size_t) const {}
};


// dummy offset checker used for arrays which don't need to check offsets.
struct DummyOffsetChecker
{
    void checkOffset(size_t, size_t) const {}
};

template <typename ARRAY_TRAITS, typename ALLOC>
size_t bitSizeOf(const ARRAY_TRAITS& arrayTraits,
        const std::vector<typename ARRAY_TRAITS::type, ALLOC>& rawArray, size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return rawArray.empty() ? 0 : arrayTraits.bitSizeOf(bitPosition, rawArray.at(0)) * rawArray.size();

    size_t endBitPosition = bitPosition;
    for (const typename ARRAY_TRAITS::type& element : rawArray)
        endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS, typename ALLOC>
size_t bitSizeOfAligned(const ARRAY_TRAITS& arrayTraits,
        const std::vector<typename ARRAY_TRAITS::type, ALLOC>& rawArray, size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    const size_t arraySize = rawArray.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && arraySize > 0)
    {
        const size_t elementBitSize = arrayTraits.bitSizeOf(bitPosition, rawArray.at(0));
        endBitPosition = alignTo(8, endBitPosition);
        endBitPosition += (arraySize - 1) * alignTo(8, elementBitSize) + elementBitSize;
    }
    else
    {
        for (const typename ARRAY_TRAITS::type& element : rawArray)
        {
            endBitPosition = alignTo(8, endBitPosition);
            endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);
        }
    }

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS, typename ALLOC>
size_t initializeOffsets(const ARRAY_TRAITS& arrayTraits,
        std::vector<typename ARRAY_TRAITS::type, ALLOC>& array, size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
    {
        return bitPosition +
                (array.empty() ? 0 : array.size() * arrayTraits.bitSizeOf(bitPosition, array.at(0)));
    }

    size_t endBitPosition = bitPosition;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool, ALLOC> returns rvalue
    for (auto&& element : array)
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);

    return endBitPosition;
}

template <typename ARRAY_TRAITS, typename ALLOC, typename OFFSET_INITIALIZER>
size_t initializeOffsetsAligned(const ARRAY_TRAITS& arrayTraits,
        std::vector<typename ARRAY_TRAITS::type, ALLOC>& array, size_t bitPosition,
        const OFFSET_INITIALIZER& offsetInitializer)
{
    size_t endBitPosition = bitPosition;
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool, ALLOC> returns rvalue
    for (auto&& element : array)
    {
        endBitPosition = alignTo(8, endBitPosition);
        offsetInitializer.initializeOffset(index, bitsToBytes(endBitPosition));
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);
        index++;
    }

    return endBitPosition;
}

// helper to generate valid code in read switch
template <typename ARRAY_TRAITS>
typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, size_t>::type implicitArrayLength(
        const ARRAY_TRAITS& arrayTraits, BitStreamReader& in)
{
    const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
    return remainingBits / arrayTraits.bitSizeOf();
}

template <typename ARRAY_TRAITS>
typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, size_t>::type implicitArrayLength(
        const ARRAY_TRAITS&, BitStreamReader&)
{
    return 0; // this will be NEVER used
}

template <typename ARRAY_TRAITS, typename ALLOC>
void read(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC>& rawArray,
        BitStreamReader& in, size_t size)
{
    rawArray.clear();
    rawArray.reserve(size);
    for (size_t index = 0; index < size; ++index)
        arrayTraits.read(rawArray, in, index);
}

template <typename ARRAY_TRAITS, typename ALLOC, typename OFFSET_CHECKER>
void readAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC>& rawArray,
        BitStreamReader& in, size_t size, const OFFSET_CHECKER& offsetChecker)
{
    rawArray.clear();
    rawArray.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(in.getBitPosition()));
        arrayTraits.read(rawArray, in, index);
    }
}

template <typename ARRAY_TRAITS, typename ALLOC>
void write(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC>& array,
        BitStreamWriter& out)
{
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool, ALLOC> returns rvalue
    for (auto&& element : array)
        arrayTraits.write(out, element);
}

template <typename ARRAY_TRAITS, typename ALLOC, typename OFFSET_CHECKER>
void writeAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC>& array,
        BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool, ALLOC> returns rvalue
    for (auto&& element : array)
    {
        out.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(out.getBitPosition()));
        arrayTraits.write(out, element);
        index++;
    }
}

} // namespace detail

enum ArrayType
{
    NORMAL,
    IMPLICIT,
    ALIGNED,
    AUTO,
    ALIGNED_AUTO
};

template <ArrayType ARRAY_TYPE, typename ARRAY_TRAITS, typename ALLOC,
        typename OFFSET_INITIALIZER = detail::DummyOffsetInitializer,
        typename OFFSET_CHECKER = detail::DummyOffsetChecker>
class Array
{
public:
    Array(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray,
            const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker) :
            m_arrayTraits(arrayTraits), m_rawArray(std::move(rawArray)),
            m_offsetInitializer(offsetInitializer), m_offsetChecker(offsetChecker)
    {}

    explicit Array(const ARRAY_TRAITS& arrayTraits,
            const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
            const ALLOC& allocator) :
            m_arrayTraits(arrayTraits), m_rawArray(allocator),
            m_offsetInitializer(offsetInitializer), m_offsetChecker(offsetChecker)
    {}

    Array(const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arraySizeArg,
            const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
            const ALLOC& allocator) :
            m_arrayTraits(arrayTraits), m_rawArray(allocator),
            m_offsetInitializer(offsetInitializer), m_offsetChecker(offsetChecker)
    {
        static_assert(ARRAY_TYPE != ArrayType::IMPLICIT || ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT,
                "Implicit array elements must have constant bit size!");

        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
                detail::read(m_arrayTraits, m_rawArray, in, arraySizeArg);
                break;
            case ArrayType::IMPLICIT:
                {
                    const size_t arraySize = detail::implicitArrayLength(m_arrayTraits, in);
                    detail::read(m_arrayTraits, m_rawArray, in, arraySize);
                }
                break;
            case ArrayType::ALIGNED:
                detail::readAligned(m_arrayTraits, m_rawArray, in, arraySizeArg, m_offsetChecker);
                break;
            case ArrayType::AUTO:
                {
                    const uint32_t arraySize = in.readVarSize();
                    detail::read(m_arrayTraits, m_rawArray, in, arraySize);
                }
                break;
            case ArrayType::ALIGNED_AUTO:
                {
                    const uint32_t arraySize = in.readVarSize();
                    detail::readAligned(m_arrayTraits, m_rawArray, in, arraySize, m_offsetChecker);
                }
                break;
        }
    }

    /**
    * Initializes array elements using the given element initializer.
    *
    * \param elementInitializer Initializer which knows how to initialize a single array element.
    */
    template <typename ELEMENT_INITIALIZER>
    void initializeElements(const ELEMENT_INITIALIZER& elementInitializer)
    {
        size_t index = 0;
        for (auto&& element : m_rawArray)
        {
            elementInitializer.initialize(element, index);
            index++;
        }
    }

    /**
     * Gets raw array.
     *
     * \return Constant reference to the raw array.
     */
    const std::vector<typename ARRAY_TRAITS::type, ALLOC>& getRawArray() const
    {
        return m_rawArray;
    }

    /**
     * Gets raw array.
     *
     * \return Reference to the raw array.
     */
    std::vector<typename ARRAY_TRAITS::type, ALLOC>& getRawArray()
    {
        return m_rawArray;
    }

    bool operator==(const Array& other) const
    {
        return m_rawArray == other.m_rawArray;
    }

    uint32_t hashCode() const
    {
        return calcHashCode(HASH_SEED, m_rawArray);
    }

    /**
    * Calculates bit size of this array.
    *
    * \param bitPosition Current bit position.
    *
    * \return Bit size of the array.
    */
    size_t bitSizeOf(size_t bitPosition = 0) const
    {
        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                return detail::bitSizeOf(m_arrayTraits, m_rawArray, bitPosition);
            case ArrayType::ALIGNED:
                return detail::bitSizeOfAligned(m_arrayTraits, m_rawArray, bitPosition);
            case ArrayType::AUTO:
            {
                const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(convertSizeToUInt32(m_rawArray.size()));
                return lengthBitSizeOf + detail::bitSizeOf(
                        m_arrayTraits, m_rawArray, lengthBitSizeOf + bitPosition);
            }
            case ArrayType::ALIGNED_AUTO:
            {
                const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(convertSizeToUInt32(m_rawArray.size()));
                return lengthBitSizeOf + detail::bitSizeOfAligned(
                        m_arrayTraits, m_rawArray, lengthBitSizeOf + bitPosition);
            }
        }
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        switch(ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                return detail::initializeOffsets(m_arrayTraits, m_rawArray, bitPosition);
            case ArrayType::ALIGNED:
                return detail::initializeOffsetsAligned(
                        m_arrayTraits, m_rawArray, bitPosition, m_offsetInitializer);
            case ArrayType::AUTO:
                {
                    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(
                            convertSizeToUInt32(m_rawArray.size()));
                    return detail::initializeOffsets( m_arrayTraits, m_rawArray, bitPosition + lengthBitSizeOf);
                }
            case ArrayType::ALIGNED_AUTO:
                {
                    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(
                            convertSizeToUInt32(m_rawArray.size()));
                    return detail::initializeOffsetsAligned(
                            m_arrayTraits, m_rawArray, bitPosition + lengthBitSizeOf, m_offsetInitializer);
                }
        }
    }

    void write(BitStreamWriter& out)
    {
        switch (ARRAY_TYPE)
        {
            case ArrayType::NORMAL:
            case ArrayType::IMPLICIT:
                detail::write(m_arrayTraits, m_rawArray, out);
                break;
            case ArrayType::ALIGNED:
                detail::writeAligned(m_arrayTraits, m_rawArray, out, m_offsetChecker);
                break;
            case ArrayType::AUTO:
                out.writeVarSize(convertSizeToUInt32(m_rawArray.size()));
                detail::write(m_arrayTraits, m_rawArray, out);
                break;
            case ArrayType::ALIGNED_AUTO:
                out.writeVarSize(convertSizeToUInt32(m_rawArray.size()));
                detail::writeAligned(m_arrayTraits, m_rawArray, out, m_offsetChecker);
                break;
        }
    }

private:
    ARRAY_TRAITS m_arrayTraits;
    std::vector<typename ARRAY_TRAITS::type, ALLOC> m_rawArray;
    OFFSET_INITIALIZER m_offsetInitializer;
    OFFSET_CHECKER m_offsetChecker;
};

// NORMAL array

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::NORMAL, ARRAY_TRAITS, ALLOC> makeArray(
        const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray)
{
    return Array<ArrayType::NORMAL, ARRAY_TRAITS, ALLOC>(arrayTraits, std::move(rawArray),
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker());
}

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::NORMAL, ARRAY_TRAITS, ALLOC> readArray(
    const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arraySize, const ALLOC& allocator = ALLOC())
{
    return Array<ArrayType::NORMAL, ARRAY_TRAITS, ALLOC>(arrayTraits, in, arraySize,
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker(), allocator);
}

// IMPLICIT array

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::IMPLICIT, ARRAY_TRAITS, ALLOC> makeImplicitArray(
        const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray)
{
    return Array<ArrayType::IMPLICIT, ARRAY_TRAITS, ALLOC>(arrayTraits, std::move(rawArray),
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker());
}

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::IMPLICIT, ARRAY_TRAITS, ALLOC> readImplicitArray(
        const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, const ALLOC& allocator = ALLOC())
{
    return Array<ArrayType::IMPLICIT, ARRAY_TRAITS, ALLOC>(arrayTraits, in, 0,
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker(), allocator);
}

// ALIGNED array

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER, typename OFFSET_CHECKER,
        typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::ALIGNED, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER> makeAlignedArray(
        const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray,
        const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker)
{
    return Array<ArrayType::ALIGNED, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER>(
            arrayTraits, std::move(rawArray), offsetInitializer, offsetChecker);
}

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER, typename OFFSET_CHECKER,
        typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::ALIGNED, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER> readAlignedArray(
        const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, size_t arraySize,
        const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
        const ALLOC& allocator = ALLOC())
{
    return Array<ArrayType::ALIGNED, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER>(
            arrayTraits, in, arraySize, offsetInitializer, offsetChecker, allocator);
}

// AUTO array

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::AUTO, ARRAY_TRAITS, ALLOC> makeAutoArray(
        const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray)
{
    return Array<ArrayType::AUTO, ARRAY_TRAITS, ALLOC>(arrayTraits, std::move(rawArray),
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker());
}

template <typename ARRAY_TRAITS, typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::AUTO, ARRAY_TRAITS, ALLOC> readAutoArray(
        const ARRAY_TRAITS& arrayTraits, BitStreamReader& in, const ALLOC& allocator = ALLOC())
{
    return Array<ArrayType::AUTO, ARRAY_TRAITS, ALLOC>(arrayTraits, in, 0,
            detail::DummyOffsetInitializer(), detail::DummyOffsetChecker(), allocator);
}

// ALIGNED_AUTO array

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER, typename OFFSET_CHECKER,
        typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::ALIGNED_AUTO, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER> makeAlignedAutoArray(
        const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type, ALLOC> rawArray,
        const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker)
{
    return Array<ArrayType::ALIGNED_AUTO, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER>(
            arrayTraits, std::move(rawArray), offsetInitializer, offsetChecker);
}

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER, typename OFFSET_CHECKER,
        typename ALLOC = std::allocator<typename ARRAY_TRAITS::type>>
Array<ArrayType::ALIGNED_AUTO, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER> readAlignedAutoArray(
        const ARRAY_TRAITS& arrayTraits, BitStreamReader& in,
        const OFFSET_INITIALIZER& offsetInitializer, const OFFSET_CHECKER& offsetChecker,
        const ALLOC& allocator = ALLOC())
{
    return Array<ArrayType::ALIGNED_AUTO, ARRAY_TRAITS, ALLOC, OFFSET_INITIALIZER, OFFSET_CHECKER>(
            arrayTraits, in, 0, offsetInitializer, offsetChecker, allocator);
}

} // namespace zserio

#endif // ZSERIO_ARRAYS_H_INC
