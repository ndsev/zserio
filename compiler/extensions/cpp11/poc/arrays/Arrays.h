#ifndef ZSERIO_ARRAYS_H_INC
#define ZSERIO_ARRAYS_H_INC

#include <numeric>
#include <vector>

#include "HashCodeUtil.h"
#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitPositionUtil.h"
#include "VarUInt64Util.h"
#include "BitStreamException.h"
#include "PreWriteAction.h"

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
    return std::accumulate(array.begin(), array.end(), T());
}

template <typename T>
int hashCode(const std::vector<T>& array)
{
    int result = HASH_SEED;
    for (auto it = array.begin(); it != array.end(); ++it)
        result = calcHashCode(result, *it);

    return result;
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t bitSizeOf(const std::vector<T>& array, size_t bitPosition = 0)
{
    if (ARRAY_TRAITS<NUM_BITS, T>::IS_BITSIZEOF_CONSTANT)
        return ARRAY_TRAITS<NUM_BITS, T>::BIT_SIZE * array.size();

    size_t endBitPosition = bitPosition;
    for (auto it = array.begin(); it != array.end(); ++it)
        endBitPosition += ARRAY_TRAITS<NUM_BITS, T>::bitSizeOf(endBitPosition, *it);

    return endBitPosition - bitPosition;
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t bitSizeOf(const std::vector<T>& array, Aligned, size_t bitPosition = 0)
{
    const size_t arraySize = array.size();
    if (ARRAY_TRAITS<NUM_BITS, T>::IS_BITSIZEOF_CONSTANT && arraySize > 0)
        return (arraySize - 1) * zserio::alignTo(NUM_BITS_PER_BYTE, ARRAY_TRAITS<NUM_BITS, T>::BIT_SIZE) +
                ARRAY_TRAITS<NUM_BITS, T>::BIT_SIZE;

    size_t endBitPosition = bitPosition;
    for (auto it = array.begin(); it != array.end(); ++it)
    {
        endBitPosition = zserio::alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        endBitPosition += ARRAY_TRAITS<NUM_BITS, T>::bitSizeOf(endBitPosition, *it);
    }

    return endBitPosition - bitPosition;
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t bitSizeOf(const std::vector<T>& array, AutoLength, size_t bitPosition = 0)
{
    const size_t lengthBitSizeOf = getBitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOf(array, bitPosition + lengthBitSizeOf);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t bitSizeOf(const std::vector<T>& array, AutoLength, Aligned aligned, size_t bitPosition = 0)
{
    const size_t lengthBitSizeOf = getBitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOf(array, aligned, bitPosition + lengthBitSizeOf);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t initializeOffsets(std::vector<T>& array, size_t bitPosition)
{
    if (ARRAY_TRAITS<NUM_BITS, T>::IS_BITSIZEOF_CONSTANT)
        return bitPosition + ARRAY_TRAITS<NUM_BITS, T>::BIT_SIZE * array.size();

    size_t endBitPosition = bitPosition;
    for (auto it = array.begin(); it != array.end(); ++it)
        endBitPosition = ARRAY_TRAITS<NUM_BITS, T>::initializeOffsets(endBitPosition, *it);

    return endBitPosition;
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename OFFSET_INITIALIZER>
size_t initializeOffsets(std::vector<T>& array, OFFSET_INITIALIZER offsetInitializer, size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    size_t index = 0;
    for (auto it = array.begin(); it != array.end(); ++it)
    {
        endBitPosition = zserio::alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        offsetInitializer.setOffset(index, zserio::bitsToBytes(endBitPosition));
        endBitPosition = ARRAY_TRAITS<NUM_BITS, T>::initializeOffsets(endBitPosition, *it);
        index++;
    }

    return endBitPosition;
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
size_t initializeOffsets(std::vector<T>& array, AutoLength, size_t bitPosition = 0)
{
    return initializeOffsets(array, bitPosition + getBitSizeOfVarUInt64(array.size()));
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename OFFSET_INITIALIZER>
size_t initializeOffsets(std::vector<T>& array, AutoLength, OFFSET_INITIALIZER offsetInitializer,
        size_t bitPosition = 0)
{
    return initializeOffsets(array, offsetInitializer, bitPosition + getBitSizeOfVarUInt64(array.size()));
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename ELEMENT_FACTORY>
void read(std::vector<T>& array, zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
        ARRAY_TRAITS<NUM_BITS, T>::read(array, in, index, elementFactory);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
void read(std::vector<T>& array, zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
        OFFSET_CHECKER offsetChecker)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.check(index, zserio::bitsToBytes(in.getBitPosition()));
        ARRAY_TRAITS<NUM_BITS, T>::read(array, in, index, elementFactory);
    }
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename ELEMENT_FACTORY>
void read(std::vector<T>& array, zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
        AutoLength)
{
    const uint64_t arraySize = in.readVarUInt64();
    read(array, in, zserio::convertVarUInt64ToArraySize(arraySize), elementFactory);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename ELEMENT_FACTORY, typename OFFSET_CHECKER>
void read(std::vector<T>& array, zserio::BitStreamReader& in, size_t size, ELEMENT_FACTORY elementFactory,
        AutoLength, OFFSET_CHECKER offsetChecker)
{
    const uint64_t arraySize = in.readVarUInt64();
    read(array, in, zserio::convertVarUInt64ToArraySize(arraySize), elementFactory, offsetChecker);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename ELEMENT_FACTORY>
void read(std::vector<T>& array, BitStreamReader& in, ImplicitLength, ELEMENT_FACTORY elementFactory)
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
            ARRAY_TRAITS<NUM_BITS, T>::read(array, in, index, elementFactory);
        }
        catch (BitStreamException&)
        {
            // set correct end bit position in the stream avoiding padding at the end
            in.setBitPosition(bitPosition);
            break;
        }
    }
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
void write(const std::vector<T>& array, BitStreamWriter& out)
{
    for (auto it = array.begin(); it != array.end(); ++it)
        ARRAY_TRAITS<NUM_BITS, T>::write(out, *it);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename OFFSET_CHECKER>
void write(const std::vector<T>& array, BitStreamWriter& out, OFFSET_CHECKER offsetChecker)
{
    size_t index = 0;
    for (auto it = array.begin(); it != array.end(); ++it)
    {
        out.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.check(index, zserio::bitsToBytes(out.getBitPosition()));
        ARRAY_TRAITS<NUM_BITS, T>::write(out, *it);
        index++;
    }
}

template<template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T>
void write(const std::vector<T>& array, BitStreamWriter& out, AutoLength)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    write(array, out);
}

template <template<size_t, typename> typename ARRAY_TRAITS, size_t NUM_BITS = 0, typename T,
        typename OFFSET_CHECKER>
void write(const std::vector<T>& array, BitStreamWriter& out, AutoLength, OFFSET_CHECKER offsetChecker)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    write(array, out, offsetChecker);
}

namespace detail
{

template <size_t NUM_BITS, typename T>
struct bit_field_array_traits;

template <size_t NUM_BITS>
struct bit_field_array_traits<NUM_BITS, int8_t>
{
    typedef int8_t type;

    static size_t bitSizeOf(size_t, type) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type) { return bitPosition + BIT_SIZE; }

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

template <size_t NUM_BITS, typename T>
struct std_integer_array_traits;

template <>
struct std_integer_array_traits<0, int8_t>
{
    typedef int8_t type;

    static size_t bitSizeOf(size_t, type) { return BIT_SIZE; }

    static size_t initializeOffsets(size_t bitPosition, type) { return bitPosition + BIT_SIZE; }

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
    static const size_t BIT_SIZE = 8;
};

template <size_t NUM_BITS, typename T>
struct object_array_traits;

template <typename T>
struct object_array_traits<0, T>
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
