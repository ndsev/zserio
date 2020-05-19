#ifndef ZSERIO_ARRAYS_H_INC
#define ZSERIO_ARRAYS_H_INC

#include <type_traits>
#include <vector>

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarSizeUtil.h"
#include "zserio/PreWriteAction.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"

namespace zserio
{

/**
 * Initializes array elements using the given element initializer.
 *
 * \param array Array to initialize.
 * \param elementInitializer Initializer which knows how to initialize a single array element.
 */
template <typename T, typename ELEMENT_INITIALIZER>
void initializeElements(std::vector<T>& array, const ELEMENT_INITIALIZER& elementInitializer)
{
    size_t index = 0;
    for (auto&& element : array)
    {
        elementInitializer.initialize(element, index);
        index++;
    }
}

/**
 * Calculates bit size of the given array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to calculate bit size of a single element.
 * \param array Array to calculate the bit size for.
 * \param bitPosition Current bit position.
 *
 * \return Bit size of the array.
 */
template <typename ARRAY_TRAITS>
size_t bitSizeOf(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return array.empty() ? 0 : arrayTraits.bitSizeOf(bitPosition, array.at(0)) * array.size();

    size_t endBitPosition = bitPosition;
    for (const typename ARRAY_TRAITS::type& element : array)
        endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);

    return endBitPosition - bitPosition;
}

/**
 * Calculates bit size of the given aligned array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to calculate bit size of a single element.
 * \param array Array to calculate the bit size for.
 * \param bitPosition Current bit position.
 *
 * \return Bit size of the array.
 */
template <typename ARRAY_TRAITS>
size_t bitSizeOfAligned(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    const size_t arraySize = array.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && arraySize > 0)
    {
        const size_t elementBitSize = arrayTraits.bitSizeOf(bitPosition, array.at(0));
        endBitPosition = alignTo(8, endBitPosition);
        endBitPosition += (arraySize - 1) * alignTo(8, elementBitSize) + elementBitSize;
    }
    else
    {
        for (const typename ARRAY_TRAITS::type& element : array)
        {
            endBitPosition = alignTo(8, endBitPosition);
            endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);
        }
    }

    return endBitPosition - bitPosition;
}

/**
 * Calculates bit size of the given auto array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to calculate bit size of a single element.
 * \param array Array to calculate the bit size for.
 * \param bitPosition Current bit position.
 *
 * \return Bit size of the array.
 */
template <typename ARRAY_TRAITS>
size_t bitSizeOfAuto(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(array.size());

    return lengthBitSizeOf + bitSizeOf(arrayTraits, array, bitPosition + lengthBitSizeOf);
}

/**
 * Calculates bit size of the given aligned auto array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to calculate bit size of a single element.
 * \param array Array to calculate the bit size for.
 * \param bitPosition Current bit position.
 *
 * \return Bit size of the array.
 */
template <typename ARRAY_TRAITS>
size_t bitSizeOfAlignedAuto(const ARRAY_TRAITS& arrayTraits,
        const std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition)
{
    const size_t lengthBitSizeOf = zserio::bitSizeOfVarSize(array.size());

    return lengthBitSizeOf + bitSizeOfAligned(arrayTraits, array, bitPosition + lengthBitSizeOf);
}

/**
 * Initializes indexed offsets in the given array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to initialize offsets of a single element.
 * \param array Array to initialize offsets for.
 * \param bitPosition Current bit position.
 *
 * \return Updated bit position which points to the first bit after the array.
 */
template <typename ARRAY_TRAITS>
size_t initializeOffsets(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
    {
        return bitPosition +
                (array.empty() ? 0 : array.size() * arrayTraits.bitSizeOf(bitPosition, array.at(0)));
    }

    size_t endBitPosition = bitPosition;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);

    return endBitPosition;
}

/**
 * Initializes indexed offsets in the given aligned array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to initialize offsets of a single element.
 * \param array Array to initialize offsets for.
 * \param bitPosition Current bit position.
 * \param offsetInitializer Initializer which initializes offsets for each element.
 *
 * \return Updated bit position which points to the first bit after the array.
 */
template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsetsAligned(const ARRAY_TRAITS& arrayTraits,
        std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition,
        const OFFSET_INITIALIZER& offsetInitializer)
{
    size_t endBitPosition = bitPosition;
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
    {
        endBitPosition = alignTo(8, endBitPosition);
        offsetInitializer.initializeOffset(index, bitsToBytes(endBitPosition));
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);
        index++;
    }

    return endBitPosition;
}

/**
 * Initializes indexed offsets in the given auto array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to initialize offsets of a single element.
 * \param array Array to initialize offsets for.
 * \param bitPosition Current bit position.
 *
 * \return Updated bit position which points to the first bit after the array.
 */
template <typename ARRAY_TRAITS>
size_t initializeOffsetsAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    return initializeOffsets(arrayTraits, array, bitPosition + zserio::bitSizeOfVarSize(array.size()));
}

/**
 * Initializes indexed offsets in the given aligned auto array using the given array traits.
 *
 * \param arrayTraits Array traits which know how to initialize offsets of a single element.
 * \param array Array to initialize offsets for.
 * \param bitPosition Current bit position.
 * \param offsetInitializer Initializer which initializes offsets for each element.
 *
 * \return Updated bit position which points to the first bit after the array.
 */
template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsetsAlignedAuto(const ARRAY_TRAITS& arrayTraits,
        std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition,
        const OFFSET_INITIALIZER& offsetInitializer)
{
    return initializeOffsetsAligned(arrayTraits, array, bitPosition + zserio::bitSizeOfVarSize(array.size()),
            offsetInitializer);
}

/**
 * Reads the array from the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to read a single element.
 * \param array Array to read to.
 * \param in Bit stream reader.
 * \param size Size of the array to read.
 */
template <typename ARRAY_TRAITS>
void read(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array, BitStreamReader& in,
        size_t size)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
        arrayTraits.read(array, in, index);
}

/**
 * Reads the aligned array from the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to read a single element.
 * \param array Array to read to.
 * \param in Bit stream reader.
 * \param size Size of the array to read.
 * \param offsetChecker Offset checker used to check offsets before reading.
 */
template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void readAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in, size_t size, const OFFSET_CHECKER& offsetChecker)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(in.getBitPosition()));
        arrayTraits.read(array, in, index);
    }
}

/**
 * Reads the auto array from the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to read a single element.
 * \param array Array to read to.
 * \param in Bit stream reader.
 */
template <typename ARRAY_TRAITS>
void readAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in)
{
    const uint32_t arraySize = in.readVarSize();
    read(arrayTraits, array, in, static_cast<size_t>(arraySize));
}

/**
 * Reads the aligned auto array from the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to read a single element.
 * \param array Array to read to.
 * \param in Bit stream reader.
 * \param offsetChecker Offset checker used to check offsets before reading.
 */
template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void readAlignedAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in, const OFFSET_CHECKER& offsetChecker)
{
    const uint32_t arraySize = in.readVarSize();
    readAligned(arrayTraits, array, in, static_cast<size_t>(arraySize), offsetChecker);
}

/**
 * Reads the implicit-length array from the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to read a single element.
 * \param array Array to read to.
 * \param in Bit stream reader.
 */
template <typename ARRAY_TRAITS>
void readImplicit(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in)
{
    static_assert(arrayTraits.IS_BITSIZEOF_CONSTANT, "Implicit array elements must have constant bit size!");
    const size_t remainingBits = in.getBufferBitSize() - in.getBitPosition();
    const size_t arraySize = remainingBits / arrayTraits.bitSizeOf();
    read(arrayTraits, array, in, arraySize);
}

/**
 * Writes the array to the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to write a single element.
 * \param array Array to write.
 * \param out Bit stream writer to use.
 */
template <typename ARRAY_TRAITS>
void write(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out)
{
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
        arrayTraits.write(out, element);
}

/**
 * Writes the aligned array to the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to write a single element.
 * \param array Array to write.
 * \param out Bit stream writer to use.
 * \param offsetChecker Offset checker used to check offsets before writing.
 */
template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void writeAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
    {
        out.alignTo(8);
        offsetChecker.checkOffset(index, bitsToBytes(out.getBitPosition()));
        arrayTraits.write(out, element);
        index++;
    }
}

/**
 * Writes the auto array to the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to write a single element.
 * \param array Array to write.
 * \param out Bit stream writer to use.
 */
template <typename ARRAY_TRAITS>
void writeAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out)
{
    out.writeVarSize(convertSizeToUInt32(array.size()));
    write(arrayTraits, array, out);
}

/**
 * Writes the aligned auto array to the bit stream using the given array traits.
 *
 * \param arrayTraits Array traits which know how to write a single element.
 * \param array Array to write.
 * \param out Bit stream writer to use.
 * \param offsetChecker Offset checker used to check offsets before writing.
 */
template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void writeAlignedAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    out.writeVarSize(convertSizeToUInt32(array.size()));
    writeAligned(arrayTraits, array, out, offsetChecker);
}

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
    void read(std::vector<type>& array, BitStreamReader& in, size_t) const
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
struct StringArrayTraits
{
    /** Type of the single array element. */
    typedef std::string type;

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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readString());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        out.writeString(value);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits for Zserio extern bit buffer type.
 */
struct BitBufferArrayTraits
{
    /** Type of the single array element. */
    typedef zserio::BitBuffer type;

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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.push_back(in.readBitBuffer());
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
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
    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer to use.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type value)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

    // Be aware that T can be varuint, so bitSizeOf cannot return constant value.
    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

/**
 * Array traits of Zserio structure, choice and union types.
 */
template <typename T, typename ELEMENT_FACTORY = void>
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
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     * \param index Index need in case of parameterized type which depends on the current index.
     */
    void read(std::vector<type>& array, BitStreamReader& in, size_t index) const
    {
        m_elementFactory.create(array, in, index);
    }

private:
    const ELEMENT_FACTORY& m_elementFactory;
};

/**
 * Array traits for Zserio structure, choice and union types.
 */
template <typename T>
class ObjectArrayTraits<T>
{
public:
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
};

} // namespace zserio

#endif // ZSERIO_ARRAYS_H_INC
