#include <string>
#include <vector>
#include <limits>
#include <array>

#include "zserio/Array.h"
#include "zserio/ArrayTraits.h"
#include "zserio/Enums.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/BitBuffer.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace
{

class ArrayTestOwner
{};

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayBitSizeOf(const ARRAY& array, size_t bitPosition)
{
    return array.bitSizeOf(ArrayTestOwner(), bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayBitSizeOf(const ARRAY& array, size_t bitPosition)
{
    return array.bitSizeOf(bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayBitSizeOfPacked(const ARRAY& array, size_t bitPosition)
{
    return array.bitSizeOfPacked(ArrayTestOwner(), bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayBitSizeOfPacked(const ARRAY& array, size_t bitPosition)
{
    return array.bitSizeOfPacked(bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayInitializeOffsets(ARRAY& array, size_t bitPosition)
{
    ArrayTestOwner owner;
    return array.initializeOffsets(owner, bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayInitializeOffsets(ARRAY& array, size_t bitPosition)
{
    return array.initializeOffsets(bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayInitializeOffsetsPacked(ARRAY& array, size_t bitPosition)
{
    ArrayTestOwner owner;
    return array.initializeOffsetsPacked(owner, bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
size_t arrayInitializeOffsetsPacked(ARRAY& array, size_t bitPosition)
{
    return array.initializeOffsetsPacked(bitPosition);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayRead(ARRAY& array, BitStreamReader& in, size_t arrayLength = 0)
{
    ArrayTestOwner owner;
    array.read(owner, in, arrayLength);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayRead(ARRAY& array, BitStreamReader& in, size_t arrayLength = 0)
{
    array.read(in, arrayLength);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayReadPacked(ARRAY& array, BitStreamReader& in, size_t arrayLength = 0)
{
    ArrayTestOwner owner;
    array.readPacked(owner, in, arrayLength);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayReadPacked(ARRAY& array, BitStreamReader& in, size_t arrayLength = 0)
{
    array.readPacked(in, arrayLength);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayWrite(ARRAY& array, BitStreamWriter& out)
{
    array.write(ArrayTestOwner(), out);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayWrite(ARRAY& array, BitStreamWriter& out)
{
    array.write(out);
}

template <typename ARRAY,
        typename std::enable_if<std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayWritePacked(ARRAY& array, BitStreamWriter& out)
{
    array.writePacked(ArrayTestOwner(), out);
}

template <typename ARRAY,
        typename std::enable_if<!std::is_same<ArrayTestOwner, typename ARRAY::OwnerType>::value ,int>::type = 0>
void arrayWritePacked(ARRAY& array, BitStreamWriter& out)
{
    array.writePacked(out);
}

enum class DummyEnum : uint8_t
{
    VALUE1 = UINT8_C(0),
    VALUE2 = UINT8_C(1),
    VALUE3 = UINT8_C(2)
};

class DummyBitmask
{
public:
    using underlying_type = uint8_t;

    enum class Values : underlying_type
    {
        CREATE = UINT8_C(1),
        READ = UINT8_C(2),
        WRITE = UINT8_C(8)
    };

    explicit DummyBitmask(BitStreamReader& in) :
            m_value(readValue(in))
    {}

    DummyBitmask(PackingContextNode& contextNode, BitStreamReader& in) :
            m_value(readValue(contextNode, in))
    {}

    constexpr DummyBitmask(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

    static void createPackingContext(PackingContextNode& contextNode)
    {
        contextNode.createContext();
    }

    void initPackingContext(PackingContextNode& contextNode) const
    {
        contextNode.getContext().init<StdIntArrayTraits<underlying_type>>(m_value);
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return UINT8_C(8);
    }

    size_t bitSizeOf(PackingContextNode& contextNode, size_t) const
    {
        return contextNode.getContext().bitSizeOf<StdIntArrayTraits<underlying_type>>(m_value);
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    size_t initializeOffsets(PackingContextNode& contextNode, size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(contextNode, bitPosition);
    }

    bool operator==(const DummyBitmask& other) const
    {
        return m_value == other.m_value;
    }

    void write(BitStreamWriter& out) const
    {
        out.writeBits(m_value, UINT8_C(8));
    }

    void write(PackingContextNode& contextNode, BitStreamWriter& out) const
    {
        contextNode.getContext().write<StdIntArrayTraits<underlying_type>>(out, m_value);
    }

private:
    static underlying_type readValue(BitStreamReader& in)
    {
        return static_cast<underlying_type>(in.readBits(UINT8_C(8)));
    }

    static underlying_type readValue(PackingContextNode& contextNode,
            BitStreamReader& in)
    {
        return contextNode.getContext().read<StdIntArrayTraits<underlying_type>>(in);
    }

    underlying_type m_value;
};

template <uint8_t BIT_SIZE>
class ElementBitSize
{
public:
    using OwnerType = ArrayTestOwner;

    static uint8_t get(const ArrayTestOwner&)
    {
        return BIT_SIZE;
    }
};

class DummyObject
{
public:
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyObject(uint32_t value, const allocator_type& = allocator_type()) :
            m_value(value)
    {}
    explicit DummyObject(BitStreamReader& in, const allocator_type& = allocator_type()) :
            m_value(in.readBits(31))
    {}

    DummyObject(PackingContextNode& contextNode, BitStreamReader& in, const allocator_type& = allocator_type())
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        m_value = context.read<BitFieldArrayTraits<uint32_t, 31>>(in);
    }

    static void createPackingContext(PackingContextNode& contextNode)
    {
        auto& child = contextNode.createChild();
        child.createContext();
    }

    void initPackingContext(PackingContextNode& contextNode) const
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        context.init<BitFieldArrayTraits<uint32_t, 31>>(m_value);
    }

    void initialize(uint32_t value)
    {
        m_value = value;
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return 31; // to make an unaligned type
    }

    size_t bitSizeOf(PackingContextNode& contextNode, size_t bitPosition = 0) const
    {
        size_t endBitPosition = bitPosition;

        auto& context = contextNode.getChildren().at(0).getContext();
        endBitPosition += context.bitSizeOf<BitFieldArrayTraits<uint32_t, 31>>(m_value);

        return endBitPosition - bitPosition;
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    size_t initializeOffsets(PackingContextNode& contextNode, size_t bitPosition = 0) const
    {
        size_t endBitPosition = bitPosition;

        auto& context = contextNode.getChildren().at(0).getContext();
        endBitPosition += context.bitSizeOf<BitFieldArrayTraits<uint32_t, 31>>(m_value);

        return endBitPosition;
    }

    bool operator==(const DummyObject& other) const
    {
        return m_value == other.m_value;
    }

    uint32_t getValue() const
    {
        return m_value;
    }

    void write(BitStreamWriter& out) const
    {
        out.writeBits(m_value, static_cast<uint8_t>(bitSizeOf()));
    }

    void write(PackingContextNode& contextNode, BitStreamWriter& out) const
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        context.write<BitFieldArrayTraits<uint32_t, 31>>(out, m_value);
    }

private:
    uint32_t m_value;
};

class ArrayTestArrayExpressions
{
public:
    using OwnerType = ArrayTestOwner;

    static void initializeOffset(ArrayTestOwner&, size_t, size_t)
    {
    }

    static void checkOffset(const ArrayTestOwner&, size_t, size_t)
    {
    }

    static void initializeElement(ArrayTestOwner&, DummyObject& element, size_t)
    {
        element.initialize(ELEMENT_VALUE);
    }

    static const uint32_t ELEMENT_VALUE = 0x12;
};

class DummyObjectElementFactory
{
public:
    using OwnerType = ArrayTestOwner;
    using allocator_type = std::allocator<uint8_t>;

    static DummyObject create(OwnerType&, BitStreamReader& in,
            const allocator_type& allocator, size_t)
    {
        return DummyObject(in, allocator);
    }

    static DummyObject create(OwnerType&, PackingContextNode& contextNode, BitStreamReader& in,
            const allocator_type& allocator, size_t)
    {
        return DummyObject(contextNode, in, allocator);
    }
};

} // namespace

template <>
inline DummyEnum valueToEnum(typename std::underlying_type<DummyEnum>::type rawValue)
{
    switch (rawValue)
    {
    case UINT8_C(0):
    case UINT8_C(1):
    case UINT8_C(2):
        return static_cast<DummyEnum>(rawValue);
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") << rawValue << "!";
    }
}

template <>
void initPackingContext<PackingContextNode, DummyEnum>(PackingContextNode& contextNode, DummyEnum value)
{
    return contextNode.getContext().init<StdIntArrayTraits<uint8_t>>(enumToValue(value));
}

template <>
inline size_t bitSizeOf<DummyEnum>(DummyEnum)
{
    return UINT8_C(8);
}

template <>
inline size_t bitSizeOf<PackingContextNode, DummyEnum>(PackingContextNode& contextNode, DummyEnum value)
{
    return contextNode.getContext().bitSizeOf<StdIntArrayTraits<uint8_t>>(enumToValue(value));
}

template <>
inline size_t initializeOffsets<DummyEnum>(size_t bitPosition, DummyEnum value)
{
    return bitPosition + bitSizeOf(value);
}

template <>
inline size_t initializeOffsets<PackingContextNode, DummyEnum>(
        PackingContextNode& contextNode, size_t bitPosition, DummyEnum value)
{
    return bitPosition + bitSizeOf(contextNode, value);
}

template <>
inline DummyEnum read<DummyEnum>(BitStreamReader& in)
{
    return valueToEnum<DummyEnum>(
            static_cast<typename std::underlying_type<DummyEnum>::type>(in.readBits(UINT8_C(8))));
}

template <>
inline DummyEnum read(PackingContextNode& contextNode, BitStreamReader& in)
{
    return valueToEnum<DummyEnum>(contextNode.getContext().read<StdIntArrayTraits<uint8_t>>(in));
}

template <>
inline void write<DummyEnum>(BitStreamWriter& out, DummyEnum value)
{
    out.writeBits(enumToValue(value), UINT8_C(8));
}

template <>
inline void write(PackingContextNode& contextNode, BitStreamWriter& out, DummyEnum value)
{
    contextNode.getContext().write<StdIntArrayTraits<uint8_t>>(out, enumToValue(value));
}

class ArrayTest : public ::testing::Test
{
public:
    ArrayTest() :
            m_byteBuffer()
    {}

protected:
    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArray(const RAW_ARRAY& rawArray, size_t elementBitSize)
    {
        const size_t arraySize = rawArray.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0)
                ? alignTo(8, elementBitSize) * (arraySize - 1) + elementBitSize
                : 0;
        testArray<ARRAY_TRAITS>(rawArray, unalignedBitSize, alignedBitSize);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize)
    {
        testArrayNormal<ARRAY_TRAITS>(rawArray, unalignedBitSize);
        testArrayAuto<ARRAY_TRAITS>(rawArray, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testArrayAligned<ARRAY_TRAITS>(rawArray, alignedBitSize);
        testArrayAlignedAuto<ARRAY_TRAITS>(rawArray, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testArrayImplicit<ARRAY_TRAITS>(rawArray, unalignedBitSize);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayInitializeElements(const RAW_ARRAY& rawArray)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ArrayTestArrayExpressions>;

        ArrayT array(rawArray);
        ArrayTestOwner owner;
        array.initializeElements(owner);
        const uint32_t expectedValue = ArrayTestArrayExpressions::ELEMENT_VALUE;
        for (const auto& element : array.getRawArray())
            ASSERT_EQ(expectedValue, element.getValue());
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArray(const RAW_ARRAY& rawArray)
    {
        testPackedArray<ARRAY_TRAITS>(rawArray, UNKNOWN_BIT_SIZE, UNKNOWN_BIT_SIZE);
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArray(const RAW_ARRAY& rawArray, size_t unalignedBitSize, size_t alignedBitSize)
    {
        testPackedArrayNormal<ARRAY_TRAITS>(rawArray, unalignedBitSize);
        testPackedArrayAuto<ARRAY_TRAITS>(rawArray, (unalignedBitSize != UNKNOWN_BIT_SIZE) ?
                AUTO_LENGTH_BIT_SIZE + unalignedBitSize : UNKNOWN_BIT_SIZE);
        testPackedArrayAligned<ARRAY_TRAITS>(rawArray, alignedBitSize);
        testPackedArrayAlignedAuto<ARRAY_TRAITS>(rawArray, (alignedBitSize != UNKNOWN_BIT_SIZE) ?
                AUTO_LENGTH_BIT_SIZE + alignedBitSize : UNKNOWN_BIT_SIZE);
    }

    size_t calcPackedBitSize(size_t elementBitSize, size_t arraySize, size_t maxDeltaBitSize)
    {
        return PACKING_DESCRIPTOR_BITSIZE + elementBitSize + (arraySize - 1) * (maxDeltaBitSize + 1);
    }

    size_t calcAlignedPackedBitSize(size_t elementBitSize, size_t arraySize, size_t maxDeltaBitSize)
    {
        const size_t firstElementWithDescriptorBitSize = PACKING_DESCRIPTOR_BITSIZE + elementBitSize;
        const size_t alignedFirstElementWithDescriptorBitSize = (firstElementWithDescriptorBitSize + 7) / 8 * 8;
        const size_t alignedMaxDeltaBitSize = (maxDeltaBitSize + 1 + 7) / 8 * 8;

        return alignedFirstElementWithDescriptorBitSize +
                (arraySize - 2) * alignedMaxDeltaBitSize + (maxDeltaBitSize + 1);
    }

    static const size_t PACKING_DESCRIPTOR_BITSIZE = 1 + 6;

private:
    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayNormal(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, reader);
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayAligned(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED, ArrayTestArrayExpressions>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, i);
            ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testArrayAlignedAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO, ArrayTestArrayExpressions>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, i);
            ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, reader);
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT>;

        if (detail::arrayTraitsConstBitSizeOf<ARRAY_TRAITS>(ArrayTestOwner()) % 8 != 0)

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOf(array, i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, arrayInitializeOffsets(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWrite(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayRead(readArray, reader);
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY,
            typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY&, size_t)
    {
        // implicit array not allowed for types with non-constant bitsize, so skip the test
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArrayNormal(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL, ArrayTestOwner>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArrayAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, reader);
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArrayAligned(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED, ArrayTestArrayExpressions>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, reader, rawArray.size());
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    template <typename ARRAY_TRAITS, typename RAW_ARRAY>
    void testPackedArrayAlignedAuto(const RAW_ARRAY& rawArray, size_t expectedBitSize)
    {
        using ArrayT = Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO, ArrayTestArrayExpressions>;

        for (uint8_t i = 0; i < 8; ++i)
        {
            ArrayT array(rawArray);

            const size_t bitSize = arrayBitSizeOfPacked(array, i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, arrayInitializeOffsetsPacked(array, i));

            BitStreamWriter writer(m_byteBuffer.data(), m_byteBuffer.size());
            writer.writeBits(0, i);
            arrayWritePacked(array, writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer.data(), writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            ArrayT readArray;
            arrayReadPacked(readArray, reader);
            ASSERT_EQ(array, readArray);

            ArrayT arrayCopy(array);
            ASSERT_EQ(array, arrayCopy);
        }
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t UNKNOWN_BIT_SIZE = std::numeric_limits<size_t>::max();

    std::array<uint8_t, 256> m_byteBuffer;
};

TEST_F(ArrayTest, intField4Array)
{
    const size_t NUM_BITS = 4;
    std::vector<int8_t> rawArray = {
            -static_cast<int8_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int8_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int8_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, intField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<int16_t> rawArray = {
            -static_cast<int16_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int16_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int16_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, intField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<int32_t> rawArray = {
            -static_cast<int32_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int32_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int32_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, intField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<int64_t> rawArray = {
            -static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)),
            7,
            static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)) - 1};
    testArray<BitFieldArrayTraits<int64_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint8_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint16_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint32_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, bitField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << NUM_BITS) - 1};
    testArray<BitFieldArrayTraits<uint64_t, NUM_BITS>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<int8_t> rawArray = {
            -static_cast<int8_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int8_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int8_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<int16_t> rawArray = {
            -static_cast<int16_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int16_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int16_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<int32_t> rawArray = {
            -static_cast<int32_t>(1U << (NUM_BITS - 1)),
            7,
            static_cast<int32_t>(1U << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int32_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicIntField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<int64_t> rawArray = {
            -static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)),
            7,
            static_cast<int64_t>(UINT64_C(1) << (NUM_BITS - 1)) - 1};
    testArray<DynamicBitFieldArrayTraits<int64_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField4Array)
{
    constexpr size_t NUM_BITS = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint8_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField12Array)
{
    constexpr size_t NUM_BITS = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint16_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField20Array)
{
    constexpr size_t NUM_BITS = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1U << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint32_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, dynamicBitField36Array)
{
    constexpr size_t NUM_BITS = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << NUM_BITS) - 1};
    testArray<DynamicBitFieldArrayTraits<uint64_t, ElementBitSize<NUM_BITS>>>(rawArray, NUM_BITS);
}

TEST_F(ArrayTest, stdInt8Array)
{
    std::vector<int8_t> rawArray = {INT8_MIN, 7, INT8_MAX};
    testArray<StdIntArrayTraits<int8_t>>(rawArray, 8);
}

TEST_F(ArrayTest, stdInt16Array)
{
    std::vector<int16_t> rawArray = {INT16_MIN, 7, INT16_MAX};
    testArray<StdIntArrayTraits<int16_t>>(rawArray, 16);
}

TEST_F(ArrayTest, stdInt32Array)
{
    std::vector<int32_t> rawArray = {INT32_MIN, 7, INT32_MAX};
    testArray<StdIntArrayTraits<int32_t>>(rawArray, 32);
}

TEST_F(ArrayTest, stdInt64Array)
{
    std::vector<int64_t> rawArray = {INT64_MIN, 7, INT64_MAX};
    testArray<StdIntArrayTraits<int64_t>>(rawArray, 64);
}

TEST_F(ArrayTest, stdUInt8Array)
{
    std::vector<uint8_t> rawArray = {0, 7, UINT8_MAX};
    testArray<StdIntArrayTraits<uint8_t>>(rawArray, 8);
}

TEST_F(ArrayTest, stdUInt16Array)
{
    std::vector<uint16_t> rawArray = {0, 7, UINT16_MAX};
    testArray<StdIntArrayTraits<uint16_t>>(rawArray, 16);
}

TEST_F(ArrayTest, stdUInt32Array)
{
    std::vector<uint32_t> rawArray = {0, 7, UINT32_MAX};
    testArray<StdIntArrayTraits<uint32_t>>(rawArray, 32);
}

TEST_F(ArrayTest, stdUInt64Array)
{
    std::vector<uint64_t> rawArray = {0, 7, UINT64_MAX};
    testArray<StdIntArrayTraits<uint64_t>>(rawArray, 64);
}

TEST_F(ArrayTest, varInt16Array)
{
    std::vector<int16_t> rawArray = {static_cast<int16_t>(1U << 5U), static_cast<int16_t>(1U << (5U + 8))};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<int16_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varInt32Array)
{
    std::vector<int32_t> rawArray = {
            static_cast<int32_t>(1U << 5U),
            static_cast<int32_t>(1U << (5U + 7)),
            static_cast<int32_t>(1U << (5U + 7 + 7)),
            static_cast<int32_t>(1U << (5U + 7 + 7 + 8))};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<int32_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varInt64Array)
{
    std::vector<int64_t> rawArray = {
            static_cast<int64_t>(UINT64_C(1) << 5U),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7 + 7)),
            static_cast<int64_t>(UINT64_C(1) << (5U + 7 + 7 + 7 + 7 + 7 + 7 + 8))};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<int64_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt16Array)
{
    std::vector<uint16_t> rawArray = {1U << 6U, 1U << (6U + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<uint16_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt32Array)
{
    std::vector<uint32_t> rawArray = {1U << 6U, 1U << (6U + 7), 1U << (6U + 7 + 7), 1U << (6U + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<uint32_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt64Array)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6U,
            UINT64_C(1) << (6U + 7),
            UINT64_C(1) << (6U + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<uint64_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varIntArray)
{
    std::vector<int64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(-1);
    rawArray.push_back(1);
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 6U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 6U) - 1);
    // 2 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 13U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 13U) - 1);
    // 3 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 20U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 20U) - 1);
    // 4 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 27U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 27U) - 1);
    // 5 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 34U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 34U) - 1);
    // 6 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 41U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 41U) - 1);
    // 7 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 48U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 48U) - 1);
    // 8 bytes
    rawArray.push_back(-static_cast<int64_t>(UINT64_C(1) << 55U) + 1);
    rawArray.push_back(static_cast<int64_t>(UINT64_C(1) << 55U) - 1);
    // 9 bytes
    rawArray.push_back(INT64_MIN + 1);
    rawArray.push_back(INT64_MAX);
    // 1 byte - special case, INT64_MIN stored as -0
    rawArray.push_back(INT64_MIN);
    const size_t bitSize = 8 * (3 + 2 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9) + 1);
    testArray<VarIntArrayTraits<int64_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varUIntArray)
{
    std::vector<uint64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(1);
    rawArray.push_back((UINT64_C(1) << 7U) - 1);
    // 2 bytes
    rawArray.push_back((UINT64_C(1) << 14U) - 1);
    // 3 bytes
    rawArray.push_back((UINT64_C(1) << 21U) - 1);
    // 4 bytes
    rawArray.push_back((UINT64_C(1) << 28U) - 1);
    // 5 bytes
    rawArray.push_back((UINT64_C(1) << 35U) - 1);
    // 6 bytes
    rawArray.push_back((UINT64_C(1) << 42U) - 1);
    // 7 bytes
    rawArray.push_back((UINT64_C(1) << 49U) - 1);
    // 8 bytes
    rawArray.push_back((UINT64_C(1) << 56U) - 1);
    // 9 bytes
    rawArray.push_back(UINT64_MAX);
    const size_t bitSize = 8 * (2 + (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9));
    testArray<VarIntArrayTraits<uint64_t>>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, varSizeArray)
{
    std::vector<uint32_t> rawArray = {
            UINT32_C(1) << 6U,
            UINT32_C(1) << (6U + 7),
            UINT32_C(1) << (6U + 7 + 7),
            UINT32_C(1) << (6U + 7 + 7 + 7),
            UINT32_C(1) << (1U + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5);
    testArray<VarSizeArrayTraits>(rawArray, bitSize, bitSize);
}

TEST_F(ArrayTest, float16Array)
{
    const size_t elementBitSize = 16;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray<Float16ArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, float32Array)
{
    const size_t elementBitSize = 32;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray<Float32ArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, float64Array)
{
    const size_t elementBitSize = 64;
    std::vector<double> rawArray = {-9.0, 0.0, 10.0};
    testArray<Float64ArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, boolArray)
{
    const size_t elementBitSize = 1;
    std::vector<bool> rawArray = {false, true};
    testArray<BoolArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, bytesArray)
{
    const size_t bytesLengthBitSize = 8;
    const size_t bytesBitSize = 2 * 8;
    const size_t elementBitSize = bytesLengthBitSize + bytesBitSize;
    std::vector<std::vector<uint8_t>> rawArray = {{ {{ 1, 255 }}, {{ 127, 128 }} }};
    testArray<BytesArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, stringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    std::vector<std::string> rawArray = {"String0", "String1", "String2"};
    testArray<StringArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, bitBufferArray)
{
    const size_t bitBufferLengthBitSize = 8;
    const size_t bitBufferBitSize = 10;
    const size_t elementBitSize = bitBufferLengthBitSize + bitBufferBitSize;
    std::vector<BitBuffer> rawArray = {BitBuffer(bitBufferBitSize), BitBuffer(bitBufferBitSize),
            BitBuffer(bitBufferBitSize)};
    testArray<BitBufferArrayTraits>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, enumArray)
{
    std::vector<DummyEnum> rawArray = {DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3};
    const size_t elementBitSize = 8;
    testArray<EnumArrayTraits<DummyEnum>>(rawArray, elementBitSize);

    std::vector<DummyEnum> invalidRawArray = {static_cast<DummyEnum>(10)};
    ASSERT_THROW(testArray<EnumArrayTraits<DummyEnum>>(invalidRawArray, elementBitSize), CppRuntimeException);
}

TEST_F(ArrayTest, bitmaskArray)
{
    std::vector<DummyBitmask> rawArray = {DummyBitmask::Values::READ, DummyBitmask::Values::WRITE,
            DummyBitmask::Values::CREATE};
    const size_t elementBitSize = 8;
    testArray<BitmaskArrayTraits<DummyBitmask>>(rawArray, elementBitSize);
}

TEST_F(ArrayTest, objectArray)
{
    std::vector<DummyObject> rawArray = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    testArrayInitializeElements<ObjectArrayTraits<DummyObject, DummyObjectElementFactory>>(rawArray);
    testArray<ObjectArrayTraits<DummyObject, DummyObjectElementFactory>>(rawArray, 31);
}

TEST_F(ArrayTest, stdInt8PackedArray)
{
    std::vector<int8_t> rawArray = { -4, -3, -1, 0, 2, 4, 6, 8, 10, 10, 11 };
    testPackedArray<StdIntArrayTraits<int8_t>>(rawArray);
}

TEST_F(ArrayTest, stdInt64PackedArray)
{
    // will not be packed
    std::vector<int64_t> rawArray = { INT64_MIN, 1, -1, INT64_MAX };
    testPackedArray<StdIntArrayTraits<int64_t>>(rawArray);
}

TEST_F(ArrayTest, stdUInt64PackedArray)
{
    // will have maxBitNumber 62 bits
    std::vector<uint64_t> rawArray = { 0, INT64_MAX / 2, 100, 200, 300, 400, 500, 600, 700 };
    testPackedArray<StdIntArrayTraits<uint64_t>>(rawArray);
}

TEST_F(ArrayTest, bitField64PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<uint64_t, 64>;

    // none-zero delta
    std::vector<uint64_t> rawArray1 = {10, 11, 12};
    const size_t array1MaxDeltaBitSize = 1;
    const size_t array1BitSizeOf = calcPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    const size_t array1AlignedBitSizeOf = calcAlignedPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    testPackedArray<ArrayTraits>(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf);

    // zero delta
    std::vector<uint64_t> rawArray2 = {10, 10, 10};
    const size_t array2BitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64;
    const size_t array2AlignedBitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64 +
            /* alignment before element 2 */ (8 - PACKING_DESCRIPTOR_BITSIZE);
    testPackedArray<ArrayTraits>(rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);

    // one-element array
    std::vector<uint64_t> rawArray3 = {10};
    const size_t array3BitSizeOf = 1 + 64;
    const size_t array3AlignedBitSizeOf = 1 + 64; // no alignment before first element, no more elements
    testPackedArray<ArrayTraits>(rawArray3, array3BitSizeOf, array3AlignedBitSizeOf);

    // empty array
    std::vector<uint64_t> rawArray4 = {};
    const size_t array4BitSizeOf = 0;
    const size_t array4AlignedBitSizeOf = 0;
    testPackedArray<ArrayTraits>(rawArray4, array4BitSizeOf, array4AlignedBitSizeOf);

    // packing not enabled, delta is too big
    testPackedArray<ArrayTraits>(std::vector<uint64_t>{0, UINT64_MAX});
    testPackedArray<ArrayTraits>(std::vector<uint64_t>{UINT64_MAX, UINT64_MAX / 2, 0});

    // will have maxBitNumber 62 bits
    testPackedArray<ArrayTraits>(
            std::vector<uint64_t>{0, static_cast<uint64_t>(INT64_MAX / 2), 100, 200, 300, 400, 500, 600, 700});
}

TEST_F(ArrayTest, bitField8PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<uint8_t, 8>;

    // will not be packed because unpacked 8bit values will be more efficient
    std::vector<uint8_t> rawArray1 = {UINT8_MAX, 0, 10, 20, 30, 40}; // max_bit_number 8, delta needs 9 bits
    const size_t array1BitSizeOf = 1 + 6 * 8;
    const size_t array1AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf);

    // will not be packed because unpacked 8bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<uint8_t> rawArray2 =
            {UINT8_MAX, UINT8_MAX / 2 + 1, 10, 20, 30, 40}; // max_bit_number 7, delta needs 8 bits
    const size_t array2BitSizeOf = 1 + 6 * 8;
    const size_t array2AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);
}

TEST_F(ArrayTest, intField64PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<int64_t, 64>;

    testPackedArray<ArrayTraits>(std::vector<int64_t>{-10, 11, -12});
    testPackedArray<ArrayTraits>(std::vector<int64_t>{-10, -10, -10}); // zero delta

    testPackedArray<ArrayTraits>(std::vector<int64_t>{}); // empty
    testPackedArray<ArrayTraits>(std::vector<int64_t>{10}); // single element

    // packing not enabled, delta is too big
    testPackedArray<ArrayTraits>(std::vector<int64_t>{INT64_MIN, INT64_MAX});
    testPackedArray<ArrayTraits>(std::vector<int64_t>{INT64_MIN, 0, INT64_MAX});
}

TEST_F(ArrayTest, intField16PackedArray)
{
    using ArrayTraits = BitFieldArrayTraits<int16_t, 16>;

    // will not be packed because unpacked 16bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<int16_t> rawArray = {INT16_MIN, -1, 10, 20, 30, 40}; // max_bit_number 15, delta needs 16 bits
    const size_t unpackedBitSizeOf = 1 + 6 * 16;
    const size_t unpackedAlignedBitSizeOf = 1 + 16 + /* alignment */ 7 + 5 * 16;
    testPackedArray<ArrayTraits>(rawArray, unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, dynamicBitField8PackedArray)
{
    using ArrayTraits = DynamicBitFieldArrayTraits<uint8_t, ElementBitSize<8>>;

    // will not be packed because unpacked 8bit values will be more efficient
    std::vector<uint8_t> rawArray1 = {UINT8_MAX, 0, 10, 20, 30, 40}; // max_bit_number 8, delta needs 9 bits
    const size_t array1BitSizeOf = 1 + 6 * 8;
    const size_t array1AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf);

    // will not be packed because unpacked 8bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<uint8_t> rawArray2 =
            {UINT8_MAX, UINT8_MAX / 2 + 1, 10, 20, 30, 40}; // max_bit_number 7, delta needs 8 bits
    const size_t array2BitSizeOf = 1 + 6 * 8;
    const size_t array2AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray<ArrayTraits>(rawArray2, array2BitSizeOf, array2AlignedBitSizeOf);
}

TEST_F(ArrayTest, varUInt64PackedArray)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6U,
            UINT64_C(1) << (6U + 7),
            UINT64_C(1) << (6U + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6U + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    testPackedArray<VarIntNNArrayTraits<uint64_t>>(rawArray);

    std::vector<uint64_t> unpackedRawArray = {UINT64_C(5000000), 0, 0, 0, 0, 0, 0};
    const size_t unpackedBitSizeOf = 1 + 32 + 6 * 8;
    const size_t unpackedAlignedBitSizeOf = 1 + 32 + /* alignment */ 7 + 6 * 8;
    testPackedArray<VarIntNNArrayTraits<uint64_t>>(unpackedRawArray,
            unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, enumPackedArray)
{
    std::vector<DummyEnum> rawArray = {DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3};
    testPackedArray<EnumArrayTraits<DummyEnum>>(rawArray);
}

TEST_F(ArrayTest, bitmaskPackedArray)
{
    std::vector<DummyBitmask> rawArray = {DummyBitmask::Values::READ, DummyBitmask::Values::WRITE,
            DummyBitmask::Values::CREATE};
    testPackedArray<BitmaskArrayTraits<DummyBitmask>>(rawArray);
}

TEST_F(ArrayTest, objectPackedArray)
{
    std::vector<DummyObject> rawArray = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    testPackedArray<ObjectArrayTraits<DummyObject, DummyObjectElementFactory>>(rawArray);
}

} // namespace zserio
