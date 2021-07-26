#ifndef ZSERIO_ARRAY_TRAITS_H_INC
#define ZSERIO_ARRAY_TRAITS_H_INC

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
    /**
     * Constructor.
     *
     * \param numBits Num bits of the array element.
     */
    explicit BitFieldArrayTraits(uint8_t numBits) :
            m_numBits(numBits)
    {}

    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t) const
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
    template <typename RAW_ARRAY>
    size_t initializeOffsets(const RAW_ARRAY&, size_t bitPosition, size_t) const
    {
        return bitPosition + m_numBits;
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t) const
    {
        rawArray.push_back(detail::read_bits<T>(in, m_numBits));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of the element to write.
     */
    template <typename RAW_ARRAY>
    void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index) const
    {
        detail::write_bits(out, rawArray.at(index), m_numBits);
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
    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t)
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
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY&, size_t bitPosition, size_t)
    {
        return bitPosition + NUM_BITS;
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(detail::read_bits<T>(in, NUM_BITS));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        detail::write_bits(out, rawArray.at(index), NUM_BITS);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;

private:
    static const uint8_t NUM_BITS = sizeof(T) * 8;
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarInt16(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarInt16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarInt16(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarInt32(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarInt32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarInt32(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarInt64(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarInt64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarInt64(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarUInt16(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarUInt16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarUInt16(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarUInt32(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarUInt32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarUInt32(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarUInt64(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarUInt64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarUInt64(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarInt(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarInt());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarInt(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t , size_t index)
    {
        return zserio::bitSizeOfVarUInt(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarUInt());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeVarUInt(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits specialization for Zserio varsize type.
 */
struct VarSizeArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfVarSize(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readVarSize());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write
     */
    template <typename RAW_ARRAY>
    void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index) const
    {
        out.writeVarSize(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio float16 type.
 */
struct Float16ArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t)
    {
        return 16;
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readFloat16());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeFloat16(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float32 type.
 */
struct Float32ArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t)
    {
        return 32;
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readFloat32());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeFloat32(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio float64 type.
 */
struct Float64ArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t)
    {
        return 64;
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readFloat64());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeFloat64(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio bool type.
 */
struct BoolArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY&, size_t, size_t)
    {
        return 1;
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readBool());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeBool(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = true;
};

/**
 * Array traits for Zserio string type.
 */
class StringArrayTraits
{
public:
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfString(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readString<RebindAlloc<typename RAW_ARRAY::allocator_type, char>>(
                rawArray.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeString(rawArray.at(index));
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio extern bit buffer type.
 */
struct BitBufferArrayTraits
{
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOfBitBuffer(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return bitPosition + bitSizeOf(rawArray, bitPosition, index);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(in.readBitBuffer<RebindAlloc<typename RAW_ARRAY::allocator_type, uint8_t>>(
                rawArray.get_allocator()));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        out.writeBitBuffer(rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return zserio::bitSizeOf(rawArray.at(index));
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return zserio::initializeOffsets(bitPosition, rawArray.at(index));
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.push_back(zserio::read<T>(in));
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        zserio::write(out, rawArray.at(index));
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
    /**
     * Calculates bit size of the array element.
     *
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t, size_t index)
    {
        return rawArray.at(index).bitSizeOf();
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return rawArray.at(index).initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     */
    template <typename RAW_ARRAY>
    static void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t)
    {
        rawArray.emplace_back(in);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(const RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        rawArray.at(index).write(out, NO_PRE_WRITE_ACTION);
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
    explicit ObjectArrayTraits(const ELEMENT_FACTORY& elementFactory) :
            m_elementFactory(elementFactory)
    {}

    /**
     * Calculates bit size of the array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Bit size of the array element.
     */
    template <typename RAW_ARRAY>
    static size_t bitSizeOf(const RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return rawArray.at(index).bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param index Index of element to use.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    template <typename RAW_ARRAY>
    static size_t initializeOffsets(RAW_ARRAY& rawArray, size_t bitPosition, size_t index)
    {
        return rawArray.at(index).initializeOffsets(bitPosition);
    }

    /**
     * Reads the single array element.
     *
     * \param in Bit stream reader.
     * \param index Index need in case of parameterized type which depends on the current index.
     */
    template <typename RAW_ARRAY>
    void read(RAW_ARRAY& rawArray, BitStreamReader& in, size_t index) const
    {
        m_elementFactory.create(rawArray, in, index);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param index Index of element to write.
     */
    template <typename RAW_ARRAY>
    static void write(RAW_ARRAY& rawArray, BitStreamWriter& out, size_t index)
    {
        rawArray.at(index).write(out, NO_PRE_WRITE_ACTION);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;

private:
    ELEMENT_FACTORY m_elementFactory;
};

} // namespace zserio

#endif // ZSERIO_ARRAY_TRAITS_H_INC
