#ifndef ZSERIO_ARRAYS_H_INC
#define ZSERIO_ARRAYS_H_INC

#include <type_traits>
#include <vector>

#include "BitStreamWriter.h"
#include "BitStreamReader.h"
#include "BitPositionUtil.h"
#include "VarUInt64Util.h"
#include "BitStreamException.h"
#include "PreWriteAction.h"
#include "BitSizeOfCalculator.h"
#include "Enums.h"

namespace zserio
{

template <typename T, typename ELEMENT_INITIALIZER>
void initializeElements(std::vector<T>& array, const ELEMENT_INITIALIZER& elementInitializer)
{
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
    {
        elementInitializer.initialize(element, index);
        index++;
    }
}

template <typename T>
T sum(const std::vector<T>& array)
{
    T summation = T();
    for (const T& element : array)
        summation += element;

    return summation;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOf(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
        return arrayTraits.bitSizeOf(bitPosition, typename ARRAY_TRAITS::type()) * array.size();

    size_t endBitPosition = bitPosition;
    for (const typename ARRAY_TRAITS::type& element : array)
        endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOfAligned(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    size_t endBitPosition = bitPosition;
    const size_t arraySize = array.size();
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT && arraySize > 0)
    {
        const size_t elementBitSize = arrayTraits.bitSizeOf(bitPosition, typename ARRAY_TRAITS::type());
        endBitPosition = alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        endBitPosition += (arraySize - 1) * alignTo(NUM_BITS_PER_BYTE, elementBitSize) + elementBitSize;
    }
    else
    {
        for (const typename ARRAY_TRAITS::type& element : array)
        {
            endBitPosition = alignTo(NUM_BITS_PER_BYTE, endBitPosition);
            endBitPosition += arrayTraits.bitSizeOf(endBitPosition, element);
        }
    }

    return endBitPosition - bitPosition;
}

template <typename ARRAY_TRAITS>
size_t bitSizeOfAuto(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    const size_t lengthBitSizeOf = zserio::bitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOf(arrayTraits, array, bitPosition + lengthBitSizeOf);
}

template <typename ARRAY_TRAITS>
size_t bitSizeOfAlignedAuto(const ARRAY_TRAITS& arrayTraits,
        const std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition)
{
    const size_t lengthBitSizeOf = zserio::bitSizeOfVarUInt64(array.size());

    return lengthBitSizeOf + bitSizeOfAligned(arrayTraits, array, bitPosition + lengthBitSizeOf);
}

template <typename ARRAY_TRAITS>
size_t initializeOffsets(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    if (ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT)
    {
        const size_t elementBitSize = arrayTraits.bitSizeOf(bitPosition, typename ARRAY_TRAITS::type());
        return bitPosition + elementBitSize * array.size();
    }

    size_t endBitPosition = bitPosition;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);

    return endBitPosition;
}

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
        endBitPosition = alignTo(NUM_BITS_PER_BYTE, endBitPosition);
        offsetInitializer.initializeOffset(index, bitsToBytes(endBitPosition));
        endBitPosition = arrayTraits.initializeOffsets(endBitPosition, element);
        index++;
    }

    return endBitPosition;
}

template <typename ARRAY_TRAITS>
size_t initializeOffsetsAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        size_t bitPosition)
{
    return initializeOffsets(arrayTraits, array, bitPosition + zserio::bitSizeOfVarUInt64(array.size()));
}

template <typename ARRAY_TRAITS, typename OFFSET_INITIALIZER>
size_t initializeOffsetsAlignedAuto(const ARRAY_TRAITS& arrayTraits,
        std::vector<typename ARRAY_TRAITS::type>& array, size_t bitPosition,
        const OFFSET_INITIALIZER& offsetInitializer)
{
    return initializeOffsetsAligned(arrayTraits, array, bitPosition + zserio::bitSizeOfVarUInt64(array.size()),
            offsetInitializer);
}

template <typename ARRAY_TRAITS>
void read(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array, BitStreamReader& in,
        size_t size)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
        arrayTraits.read(array, in, index);
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void readAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in, size_t size, const OFFSET_CHECKER& offsetChecker)
{
    array.clear();
    array.reserve(size);
    for (size_t index = 0; index < size; ++index)
    {
        in.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.checkOffset(index, bitsToBytes(in.getBitPosition()));
        arrayTraits.read(array, in, index);
    }
}

template <typename ARRAY_TRAITS>
void readAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in)
{
    const uint64_t arraySize = in.readVarUInt64();
    read<ARRAY_TRAITS>(arrayTraits, array, in, convertVarUInt64ToArraySize(arraySize));
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void readAlignedAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in, const OFFSET_CHECKER& offsetChecker)
{
    const uint64_t arraySize = in.readVarUInt64();
    readAligned(arrayTraits, array, in, convertVarUInt64ToArraySize(arraySize), offsetChecker);
}

template <typename ARRAY_TRAITS>
void readImplicit(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamReader& in)
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
            arrayTraits.read(array, in, index);
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
void write(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out)
{
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
        arrayTraits.write(out, element);
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void writeAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    size_t index = 0;
    // can't use 'typename ARRAY_TRAITS::type&' because std::vector<bool> returns rvalue
    for (auto&& element : array)
    {
        out.alignTo(NUM_BITS_PER_BYTE);
        offsetChecker.checkOffset(index, bitsToBytes(out.getBitPosition()));
        arrayTraits.write(out, element);
        index++;
    }
}

template <typename ARRAY_TRAITS>
void writeAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    write(arrayTraits, array, out);
}

template <typename ARRAY_TRAITS, typename OFFSET_CHECKER>
void writeAlignedAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
        BitStreamWriter& out, const OFFSET_CHECKER& offsetChecker)
{
    out.writeVarUInt64(static_cast<uint64_t>(array.size()));
    writeAligned(arrayTraits, array, out, offsetChecker);
}

namespace detail
{

template<typename T>
T read_bits(BitStreamReader& in, uint8_t numBits);

template<>
inline int8_t read_bits<int8_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<int8_t>(in.readSignedBits(numBits));
}

template<>
inline int16_t read_bits<int16_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<int16_t>(in.readSignedBits(numBits));
}

template<>
inline int32_t read_bits<int32_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readSignedBits(numBits);
}

template<>
inline int64_t read_bits<int64_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readSignedBits64(numBits);
}

template<>
inline uint8_t read_bits<uint8_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<uint8_t>(in.readBits(numBits));
}

template<>
inline uint16_t read_bits<uint16_t>(BitStreamReader& in, uint8_t numBits)
{
    return static_cast<uint16_t>(in.readBits(numBits));
}

template<>
inline uint32_t read_bits<uint32_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readBits(numBits);
}

template<>
inline uint64_t read_bits<uint64_t>(BitStreamReader& in, uint8_t numBits)
{
    return in.readBits64(numBits);
}

template<typename T>
void write_bits(BitStreamWriter& out, T value, uint8_t numBits);

template<>
inline void write_bits<int8_t>(BitStreamWriter& out, int8_t value, uint8_t numBits)
{
    out.writeSignedBits(static_cast<int32_t>(value), numBits);
}

template<>
inline void write_bits<int16_t>(BitStreamWriter& out, int16_t value, uint8_t numBits)
{
    out.writeSignedBits(static_cast<int32_t>(value), numBits);
}

template<>
inline void write_bits<int32_t>(BitStreamWriter& out, int32_t value, uint8_t numBits)
{
    out.writeSignedBits(value, numBits);
}

template<>
inline void write_bits<int64_t>(BitStreamWriter& out, int64_t value, uint8_t numBits)
{
    out.writeSignedBits64(value, numBits);
}

template<>
inline void write_bits<uint8_t>(BitStreamWriter& out, uint8_t value, uint8_t numBits)
{
    out.writeBits(static_cast<uint32_t>(value), numBits);
}

template<>
inline void write_bits<uint16_t>(BitStreamWriter& out, uint16_t value, uint8_t numBits)
{
    out.writeBits(static_cast<uint32_t>(value), numBits);
}

template<>
inline void write_bits<uint32_t>(BitStreamWriter& out, uint32_t value, uint8_t numBits)
{
    out.writeBits(value, numBits);
}

template<>
inline void write_bits<uint64_t>(BitStreamWriter& out, uint64_t value, uint8_t numBits)
{
    out.writeBits64(value, numBits);
}

} // namespace detail

template <typename T>
class BitFieldArrayTraits
{
public:
    typedef T type;

    explicit BitFieldArrayTraits(uint8_t numBits) : m_numBits(numBits)
    {
    }

    size_t bitSizeOf(size_t, type) const
    {
        return m_numBits;
    }

    size_t initializeOffsets(size_t bitPosition, type) const
    {
        return bitPosition + m_numBits;
    }

    void read(std::vector<type>& array, BitStreamReader& in, size_t) const
    {
        array.emplace_back(detail::read_bits<type>(in, m_numBits));
    }

    void write(BitStreamWriter& out, type value) const
    {
        detail::write_bits(out, value, m_numBits);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;

private:
    uint8_t m_numBits;
};

template <typename T>
struct StdIntArrayTraits
{
    typedef T type;

    static size_t bitSizeOf(size_t, type)
    {
        return NUM_BITS;
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return bitPosition + NUM_BITS;
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(detail::read_bits<type>(in, NUM_BITS));
    }

    static void write(BitStreamWriter& out, type value)
    {
        detail::write_bits(out, value, NUM_BITS);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;

private:
    static const uint8_t NUM_BITS = sizeof(type) * 8;
};

template <typename T>
struct VarIntNNArrayTraits;

template <>
struct VarIntNNArrayTraits<int16_t>
{
    typedef int16_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt16(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarInt16());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntNNArrayTraits<int32_t>
{
    typedef int32_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt32(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarInt32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntNNArrayTraits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarInt64(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarInt64());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntNNArrayTraits<uint16_t>
{
    typedef uint16_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt16(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarUInt16());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntNNArrayTraits<uint32_t>
{
    typedef uint32_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt32(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarUInt32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntNNArrayTraits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t, type value)
    {
        return zserio::bitSizeOfVarUInt64(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarUInt64());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <typename T>
struct VarIntArrayTraits;

template <>
struct VarIntArrayTraits<int64_t>
{
    typedef int64_t type;

    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::bitSizeOfVarInt(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarInt());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarInt(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <>
struct VarIntArrayTraits<uint64_t>
{
    typedef uint64_t type;

    static size_t bitSizeOf(size_t , type value)
    {
        return zserio::bitSizeOfVarUInt(value);
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readVarUInt());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeVarUInt(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

struct Float16ArrayTraits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type)
    {
        return 16;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readFloat16());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat16(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
};

struct Float32ArrayTraits
{
    typedef float type;

    static size_t bitSizeOf(size_t, type)
    {
        return 32;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readFloat32());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat32(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
};

struct Float64ArrayTraits
{
    typedef double type;

    static size_t bitSizeOf(size_t, type)
    {
        return 64;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readFloat64());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeFloat64(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
};

struct BoolArrayTraits
{
    typedef bool type;

    static size_t bitSizeOf(size_t, type)
    {
        return 1;
    }

    static size_t initializeOffsets(size_t bitPosition, type value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readBool());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeBool(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
};

struct StringArrayTraits
{
    typedef std::string type;

    static size_t bitSizeOf(size_t, const type& value)
    {
        return zserio::bitSizeOfString(value);
    }

    static size_t initializeOffsets(size_t bitPosition, const type& value)
    {
        return bitPosition + bitSizeOf(bitPosition, value);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in.readString());
    }

    static void write(BitStreamWriter& out, type value)
    {
        out.writeString(value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

template <typename T>
class EnumArrayTraits
{
public:
    typedef T type;

    static size_t bitSizeOf(size_t, type)
    {
        return zserio::bitSizeOf<type>();
    }

    static size_t initializeOffsets(size_t bitPosition, type)
    {
        return zserio::initializeOffsets<type>(bitPosition);
    }

    static void read(std::vector<type>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(zserio::read<type>(in));
    }

    static void write(BitStreamWriter& out, type& value)
    {
        zserio::write(out, value);
    }

    static const bool IS_BITSIZEOF_CONSTANT = true;
};

template <typename T, typename ELEMENT_FACTORY = void>
class ObjectArrayTraits
{
public:
    explicit ObjectArrayTraits(const ELEMENT_FACTORY& elementFactory) : m_elementFactory(elementFactory)
    {
    }

    typedef T type;

    void read(std::vector<type>& array, BitStreamReader& in, size_t index) const
    {
        m_elementFactory.create(array, in, index);
    }

private:
    const ELEMENT_FACTORY& m_elementFactory;
};

template <typename T>
class ObjectArrayTraits<T>
{
public:
    typedef T type;

    static size_t bitSizeOf(size_t bitPosition, const type& value)
    {
        return value.bitSizeOf(bitPosition);
    }

    static size_t initializeOffsets(size_t bitPosition, type& value)
    {
        return value.initializeOffsets(bitPosition);
    }

    static void write(BitStreamWriter& out, type& value)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

    static const bool IS_BITSIZEOF_CONSTANT = false;
};

} // namespace zserio

#endif // ZSERIO_ARRAYS_H_INC
