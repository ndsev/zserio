#ifndef ZSERIO_ARRAYS_H_INC
#define ZSERIO_ARRAYS_H_INC

#include <type_traits>
#include <vector>

#include "HashCodeUtil.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitPositionUtil.h"
#include "VarUInt64Util.h"
#include "BitStreamException.h"
#include "PreWriteAction.h"
#include "BitSizeOfCalculator.h"

// This should be implemented in runtime library header.
namespace zserio
{

namespace arrays
{

struct ImplicitLength
{
};

struct Aligned
{
};

struct AutoLength
{
};

template <typename T>
T sum(const std::vector<T>& array)
{
    T summation = T();
    for (auto element : array)
        summation += element;

    return summation;
}

template <typename T>
int hashCode(const std::vector<T>& array)
{
    int result = HASH_SEED;
    for (auto element : array)
        result = calcHashCode(result, element);

    return result;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOf(const std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition = 0)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return ARRAY_TRAITS::BIT_SIZE * array.size();

    size_t endBitPosition = bitPosition;
    for (auto element : array)
        endBitPosition += ARRAY_TRAITS::bitSizeOf(endBitPosition, element);

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOf(const std::vector<typename ARRAY_TRAITS::type>& array, Aligned, size_t bitPosition = 0)
{
    const size_t arraySize = array.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && arraySize > 0)
        return (arraySize - 1) * zserio::alignTo(NUM_BITS_PER_BYTE, ARRAY_TRAITS::BIT_SIZE) +
                ARRAY_TRAITS::BIT_SIZE;

    size_t endBitPosition = bitPosition;
    for (auto element : array)
    {
        endBitPosition = zserio::alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        endBitPosition += ARRAY_TRAITS::bitSizeOf(endBitPosition, element);
    }

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOf(const std::vector<typename ARRAY_TRAITS::type>& array, AutoLength, size_t bitPosition = 0)
{
    const size_t lengthBitSizeOf = getBitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOf(array, bitPosition + lengthBitSizeOf);
}

template <typename ARRAY_TRAITS>
size_t bitSizeOf(const std::vector<typename ARRAY_TRAITS::type>& array, AutoLength, Aligned aligned,
        size_t bitPosition = 0)
{
    const size_t lengthBitSizeOf = getBitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOf(array, aligned, bitPosition + lengthBitSizeOf);
}

template <typename ARRAY_TRAITS>
size_t initializeOffsets(std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return bitPosition + ARRAY_TRAITS::BIT_SIZE * array.size();

    size_t endBitPosition = bitPosition;
    for (auto element : array)
        endBitPosition = ARRAY_TRAITS::initializeOffsets(endBitPosition, element);

    return endBitPosition;
}

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsets(std::vector<typename ARRAY_TRAITS::type>& array, OFFSET_INITIALIZER offsetInitializer,
        size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    size_t index = 0;
    for (auto element : array)
    {
        endBitPosition = zserio::alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        offsetInitializer.setOffset(index, zserio::bitsToBytes(endBitPosition));
        endBitPosition = ARRAY_TRAITS::initializeOffsets(endBitPosition, element);
        index++;
    }

    return endBitPosition;
}

template <typename ARRAY_TRAITS>
size_t initializeOffsets(std::vector<typename ARRAY_TRAITS::type>& array, AutoLength, size_t bitPosition = 0)
{
    return initializeOffsets(array, bitPosition + getBitSizeOfVarUInt64(array.size()));
}

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsets(std::vector<typename ARRAY_TRAITS::type>& array, AutoLength,
        OFFSET_INITIALIZER offsetInitializer, size_t bitPosition = 0)
{
    return initializeOffsets(array, offsetInitializer, bitPosition + getBitSizeOfVarUInt64(array.size()));
}

template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
void read(std::vector<typename ARRAY_TRAITS::type>& array, zserio::BitStreamReader& in, size_t size,
        ELEMENT_FACTORY elementFactory)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
        ARRAY_TRAITS::read(array, in, index, elementFactory);
}

template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
void read(std::vector<typename ARRAY_TRAITS::type>& array, zserio::BitStreamReader& in, size_t size,
        ELEMENT_FACTORY elementFactory, OFFSET_CHECKER offsetChecker)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.check(index, zserio::bitsToBytes(in.getBitPosition()));
        ARRAY_TRAITS::read(array, in, index, elementFactory);
    }
}

template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
void read(std::vector<typename ARRAY_TRAITS::type>& array, zserio::BitStreamReader& in, size_t size,
        ELEMENT_FACTORY elementFactory, AutoLength)
{
    const uint64_t arraySize = in.readVarUInt64();
    read(array, in, zserio::convertVarUInt64ToArraySize(arraySize), elementFactory);
}

template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
void read(std::vector<typename ARRAY_TRAITS::type>& array, zserio::BitStreamReader& in, size_t size,
        ELEMENT_FACTORY elementFactory, AutoLength, OFFSET_CHECKER offsetChecker)
{
    const uint64_t arraySize = in.readVarUInt64();
    read(array, in, zserio::convertVarUInt64ToArraySize(arraySize), elementFactory, offsetChecker);
}

template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
void read(std::vector<typename ARRAY_TRAITS::type>& array, BitStreamReader& in, ImplicitLength,
        ELEMENT_FACTORY elementFactory)
{
    array.clear();
    BitStreamReader::BitPosType bitPosition;
    // we must read until end of the stream because we don't know element sizes
    while (true)
    {
        bitPosition = in.getBitPosition();
        const size_t index = array.size();
        try
        {
            ARRAY_TRAITS::read(array, in, index, elementFactory);
        }
        catch (BitStreamException&)
        {
            // set correct end bit position in the stream avoiding padding at the end
            in.setBitPosition(bitPosition);
            break;
        }
    }
}

template <typename ARRAY_TRAITS>
void write(const std::vector<typename ARRAY_TRAITS::type>& array, BitStreamWriter& out)
{
    for (auto element : array)
        ARRAY_TRAITS::write(out, element);
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void write(const std::vector<typename ARRAY_TRAITS::type>& array, BitStreamWriter& out,
        OFFSET_CHECKER offsetChecker)
{
    size_t index = 0;
    for (auto element : array)
    {
        out.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.check(index, zserio::bitsToBytes(out.getBitPosition()));
        ARRAY_TRAITS::write(out, element);
        index++;
    }
}

template <typename ARRAY_TRAITS>
void write(const std::vector<typename ARRAY_TRAITS::type>& array, BitStreamWriter& out, AutoLength)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    write(array, out);
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void write(const std::vector<typename ARRAY_TRAITS::type>& array, BitStreamWriter& out, AutoLength,
        OFFSET_CHECKER offsetChecker)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    write(array, out, offsetChecker);
}

namespace detail
{

template <size_t NUM_BITS, typename T, typename ENABLED = void>
struct bit_field_array_traits;

template <size_t NUM_BITS, typename T>
struct bit_field_array_traits<NUM_BITS, T, typename std::enable_if<std::is_same<T, int8_t>::value ||
                                                                   std::is_same<T, int16_t>::value ||
                                                                   std::is_same<T, int32_t>::value>::type>
{
    typedef T type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(static_cast<type>(in.readSignedBits(BIT_SIZE)));
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeSignedBits(value, BIT_SIZE);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = NUM_BITS;
};

template <size_t NUM_BITS>
struct bit_field_array_traits<NUM_BITS, int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readSignedBits64(BIT_SIZE));
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeSignedBits64(value, BIT_SIZE);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = NUM_BITS;
};

template <size_t NUM_BITS, typename T>
struct bit_field_array_traits<NUM_BITS, T, typename std::enable_if<std::is_same<T, uint8_t>::value ||
                                                                   std::is_same<T, uint16_t>::value ||
                                                                   std::is_same<T, uint32_t>::value>::type>
{
    typedef T type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(static_cast<type>(in.readBits(BIT_SIZE)));
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeBits(value, BIT_SIZE);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = NUM_BITS;
};

template <size_t NUM_BITS>
struct bit_field_array_traits<NUM_BITS, uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readBits64(BIT_SIZE));
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeBits64(value, BIT_SIZE);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = NUM_BITS;
};

template <typename T>
struct std_int_array_traits : public bit_field_array_traits<sizeof(T) * 8, T>
{
};

template <typename T>
struct var_int_nn_array_traits;

template <>
struct var_int_nn_array_traits<int16_t>
{
    typedef int16_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarInt16(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarInt16());
    }

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarInt16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<int32_t>
{
    typedef int32_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarInt32(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarInt32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarInt64(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarInt64());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint16_t>
{
    typedef uint16_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarUInt16(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarUInt16());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint32_t>
{
    typedef uint32_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarUInt32(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarUInt32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_nn_array_traits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::getBitSizeOfVarUInt64(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY, uint8_t)
    {
        array.emplace_back(in.readVarUInt64());
    }

    static void write(BitStreamWriter& out, type value, uint8_t)
    {
        out.writeVarUInt64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <typename T>
struct var_int_array_traits;

template <>
struct var_int_array_traits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::getBitSizeOfVarInt(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarInt());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <>
struct var_int_array_traits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::getBitSizeOfVarUInt(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readVarUInt());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

struct float16_array_traits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readFloat16());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 16;
};

struct float32_array_traits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readFloat32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 32;
};

struct float64_array_traits
{
    typedef double type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readFloat64());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 64;
};

struct bool_array_traits
{
    typedef bool type;

    static size_t bitSizeOf(size_t, type)
    {
        return BIT_SIZE;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + BIT_SIZE;
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readBool());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeBool(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
    static const size_t BIT_SIZE = 1;
};

struct string_array_traits
{
    typedef std::string type;

    static size_t bitSizeOf(size_t, const type& value)
    {
        return zserio::getBitSizeOfString(value);
    }

    static size_t initializeOffsets(size_t bitPosition, const type& value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t, ELEMENT_FACTORY)
    {
        array.emplace_back(in.readString());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeString(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

template <typename T>
struct object_array_traits
{
    typedef T type;

    static size_t bitSizeOf(size_t bitPosition, const type& value)
    {
        return value.bitSizeOf(bitPosition);
    }

    static size_t initializeOffsets(size_t bitPosition, type& value)
    {
        return value.initializeOffsets(bitPosition);
    }

    template <typename ELEMENT_FACTORY>
    static void read(std::vector<type>& array, BitStreamReader& in, size_t index,
            ELEMENT_FACTORY elementFactory)
    {
        elementFactory.create(array, in, index);
    }

    static void write(BitStreamWriter& out, type& value)
    {
        value.write(out, zserio::NO_PRE_WRITE_ACTION);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
    static const size_t BIT_SIZE = 0;
};

} // namespace detail

} // namespace arrays

} // namespace zserio

#endif // ZSERIO_ARRAYS_H_INC
