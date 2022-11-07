#include <string>
#include <vector>
#include <limits>

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

class ArrayTestOffsetInitializer
{
public:
    void initializeOffset(size_t, size_t) const
    {
    }
};

class ArrayTestOffsetChecker
{
public:
    void checkOffset(size_t, size_t) const
    {
    }
};

enum class DummyEnum : uint8_t
{
    VALUE1 = UINT8_C(0),
    VALUE2 = UINT8_C(1),
    VALUE3 = UINT8_C(2)
};

class DummyBitmask
{
public:
    typedef uint8_t underlying_type;

    struct Values
    {
        static const DummyBitmask CREATE;
        static const DummyBitmask READ;
        static const DummyBitmask WRITE;
    };

    explicit DummyBitmask(BitStreamReader& in) :
        m_value(readValue(in))
    {}

    explicit DummyBitmask(underlying_type value) :
        m_value(value)
    {}

    DummyBitmask(PackingContextNode& contextNode, BitStreamReader& in) :
        m_value(readValue(contextNode, in))
    {}

    static void createPackingContext(PackingContextNode& contextNode)
    {
        contextNode.createContext();
    }

    void initPackingContext(PackingContextNode& contextNode) const
    {
        contextNode.getContext().init(StdIntArrayTraits<underlying_type>(), m_value);
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return UINT8_C(8);
    }

    size_t bitSizeOf(PackingContextNode& contextNode, size_t) const
    {
        return contextNode.getContext().bitSizeOf(StdIntArrayTraits<underlying_type>(), m_value);
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
        contextNode.getContext().write(StdIntArrayTraits<underlying_type>(), out, m_value);
    }

private:
    static underlying_type readValue(BitStreamReader& in)
    {
        return static_cast<underlying_type>(in.readBits(UINT8_C(8)));
    }

    static underlying_type readValue(PackingContextNode& contextNode,
            BitStreamReader& in)
    {
        return contextNode.getContext().read(StdIntArrayTraits<underlying_type>(), in);
    }

    underlying_type m_value;
};

const DummyBitmask DummyBitmask::Values::CREATE = DummyBitmask(UINT8_C(1));
const DummyBitmask DummyBitmask::Values::READ = DummyBitmask(UINT8_C(2));
const DummyBitmask DummyBitmask::Values::WRITE = DummyBitmask(UINT8_C(8));

class ElementBitSize
{
public:
    explicit ElementBitSize(uint8_t bitSize) : m_bitSize(bitSize) {}

    uint8_t get() const
    {
        return m_bitSize;
    }

private:
    uint8_t m_bitSize;
};

class DummyObject
{
public:
    explicit DummyObject(uint32_t value) : m_value(value) {}
    explicit DummyObject(BitStreamReader& in) : m_value(in.readBits(31)) {}
    DummyObject(PackingContextNode& contextNode, BitStreamReader& in)
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        m_value = context.read(BitFieldArrayTraits<uint32_t>(31), in);
    }

    static void createPackingContext(PackingContextNode& contextNode)
    {
        auto& child = contextNode.createChild();
        child.createContext();
    }

    void initPackingContext(PackingContextNode& contextNode) const
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        context.init(BitFieldArrayTraits<uint32_t>(31), m_value);
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
        endBitPosition += context.bitSizeOf(BitFieldArrayTraits<uint32_t>(31),
                m_value);

        return endBitPosition - bitPosition;
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    size_t initializeOffsets(PackingContextNode& contextNode, size_t bitPosition = 0)
    {
        size_t endBitPosition = bitPosition;

        auto& context = contextNode.getChildren().at(0).getContext();
        endBitPosition += context.bitSizeOf(BitFieldArrayTraits<uint32_t>(31), m_value);

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
        context.write(BitFieldArrayTraits<uint32_t>(31), out, m_value);
    }

private:
    uint32_t m_value;
};

class DummyObjectElementInitializer
{
public:
    void initialize(DummyObject& element, size_t _index) const
    {
        (void)_index;
        element.initialize(ELEMENT_VALUE);
    }

    static const uint32_t ELEMENT_VALUE = 0x12;
};

class DummyObjectElementFactory
{
public:
    static void create(std::vector<DummyObject>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in);
    }

    static void create(PackingContextNode& contextNode,
            std::vector<DummyObject>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(contextNode, in);
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
        return DummyEnum(rawValue);
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") << rawValue << "!";
    }
}

template <>
void initPackingContext<PackingContextNode, DummyEnum>(PackingContextNode& contextNode, DummyEnum value)
{
    return contextNode.getContext().init(StdIntArrayTraits<uint8_t>(), enumToValue(value));
}

template <>
inline size_t bitSizeOf<DummyEnum>(DummyEnum)
{
    return UINT8_C(8);
}

template <>
inline size_t bitSizeOf<PackingContextNode, DummyEnum>(PackingContextNode& contextNode, DummyEnum value)
{
    return contextNode.getContext().bitSizeOf(StdIntArrayTraits<uint8_t>(), enumToValue(value));
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
    return valueToEnum<DummyEnum>(contextNode.getContext().read(StdIntArrayTraits<uint8_t>(), in));
}

template <>
inline void write<DummyEnum>(BitStreamWriter& out, DummyEnum value)
{
    out.writeBits(enumToValue(value), UINT8_C(8));
}

template <>
inline void write(PackingContextNode& contextNode, BitStreamWriter& out, DummyEnum value)
{
    contextNode.getContext().write(StdIntArrayTraits<uint8_t>(), out, enumToValue(value));
}

class ArrayTest : public ::testing::Test
{
public:
    ArrayTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t elementBitSize,
            const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        const size_t arraySize = rawArray.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0)
                ? alignTo(8, elementBitSize) * (arraySize - 1) + elementBitSize
                : 0;
        testArray(rawArray, arrayTraits, unalignedBitSize, alignedBitSize, elementFactory);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t unalignedBitSize, size_t alignedBitSize,
            const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        testArrayNormal(rawArray, arrayTraits, unalignedBitSize, elementFactory);
        testArrayAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + unalignedBitSize, elementFactory);
        testArrayAligned(rawArray, arrayTraits, alignedBitSize, elementFactory);
        testArrayAlignedAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + alignedBitSize, elementFactory);
        testArrayImplicit(rawArray, arrayTraits, unalignedBitSize);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testArrayInitializeElements(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        array.initializeElements(DummyObjectElementInitializer());
        const uint32_t expectedValue = DummyObjectElementInitializer::ELEMENT_VALUE;
        for (const auto& element : array.getRawArray())
            ASSERT_EQ(expectedValue, element.getValue());
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testPackedArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        testPackedArray(rawArray, arrayTraits, UNKNOWN_BIT_SIZE, UNKNOWN_BIT_SIZE, elementFactory);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testPackedArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t unalignedBitSize,
            size_t alignedBitSize, const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        testPackedArrayNormal(rawArray, arrayTraits, unalignedBitSize, elementFactory);
        testPackedArrayAuto(rawArray, arrayTraits, (unalignedBitSize != UNKNOWN_BIT_SIZE) ?
                AUTO_LENGTH_BIT_SIZE + unalignedBitSize : UNKNOWN_BIT_SIZE, elementFactory);
        testPackedArrayAligned(rawArray, arrayTraits, alignedBitSize, elementFactory);
        testPackedArrayAlignedAuto(rawArray, arrayTraits, (alignedBitSize != UNKNOWN_BIT_SIZE) ?
                AUTO_LENGTH_BIT_SIZE + alignedBitSize : UNKNOWN_BIT_SIZE, elementFactory);
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
    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testArrayNormal(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t expectedBitSize,
            const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOf(i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, array.initializeOffsets(i));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.write(writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> readArray{arrayTraits};
            readArray.read(reader, rawArray.size(), elementFactory);
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testArrayAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t expectedBitSize,
            const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOf(i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, array.initializeOffsets(i));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.write(writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> readArray{arrayTraits};
            readArray.read(reader, elementFactory);
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testArrayAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t expectedBitSize,
            const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOf(i);
            ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, array.initializeOffsets(i, ArrayTestOffsetInitializer()));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.write(writer, ArrayTestOffsetChecker());
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
            readArray.read(reader, rawArray.size(), elementFactory, ArrayTestOffsetChecker());
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testArrayAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t expectedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOf(i);
            ASSERT_EQ(alignTo(8, i) - i + expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, array.initializeOffsets(i, ArrayTestOffsetInitializer()));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.write(writer, ArrayTestOffsetChecker());
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
            readArray.read(reader, elementFactory, ArrayTestOffsetChecker());
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS,
            typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t expectedBitSize)
    {
        if (arrayTraits.bitSizeOf() % 8 != 0)
            return; // implicit array allowed for types with constant bitsize rounded to bytes

        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOf(i);
            ASSERT_EQ(expectedBitSize, bitSize);
            ASSERT_EQ(i + bitSize, array.initializeOffsets(i));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.write(writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT> readArray{arrayTraits};
            readArray.read(reader);
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS,
            typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testArrayImplicit(const RAW_ARRAY&, const ARRAY_TRAITS&, size_t)
    {
        // implicit array not allowed for types with non-constant bitsize, so skip the test
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testPackedArrayNormal(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t expectedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOfPacked(i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, array.initializeOffsetsPacked(i));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.writePacked(writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> readArray{arrayTraits};
            readArray.readPacked(reader, rawArray.size(), elementFactory);
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testPackedArrayAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t expectedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOfPacked(i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, array.initializeOffsetsPacked(i));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.writePacked(writer);
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> readArray{arrayTraits};
            readArray.readPacked(reader, elementFactory);
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testPackedArrayAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t expectedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOfPacked(i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, array.initializeOffsetsPacked(i, ArrayTestOffsetInitializer()));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.writePacked(writer, ArrayTestOffsetChecker());
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
            readArray.readPacked(reader, rawArray.size(), elementFactory, ArrayTestOffsetChecker());
            ASSERT_EQ(array, readArray);
        }
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testPackedArrayAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t expectedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        for (uint8_t i = 0; i < 8; ++i)
        {
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

            const size_t bitSize = array.bitSizeOfPacked(i);
            if (expectedBitSize != UNKNOWN_BIT_SIZE && i == 0)
            {
                ASSERT_EQ(expectedBitSize, bitSize);
            }
            ASSERT_EQ(i + bitSize, array.initializeOffsetsPacked(i, ArrayTestOffsetInitializer()));

            BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
            writer.writeBits(0, i);
            array.writePacked(writer, ArrayTestOffsetChecker());
            ASSERT_EQ(i + bitSize, writer.getBitPosition());

            BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
            ASSERT_EQ(0, reader.readBits(i));
            Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                    ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
            readArray.readPacked(reader, elementFactory, ArrayTestOffsetChecker());
            ASSERT_EQ(array, readArray);
        }
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;
    static const size_t UNKNOWN_BIT_SIZE = std::numeric_limits<size_t>::max();

    uint8_t m_byteBuffer[BUFFER_SIZE];
};

TEST_F(ArrayTest, intField4Array)
{
    const size_t numBits = 4;
    std::vector<int8_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray(rawArray, BitFieldArrayTraits<int8_t>(numBits), numBits);
}

TEST_F(ArrayTest, intField12Array)
{
    const size_t numBits = 12;
    std::vector<int16_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray(rawArray, BitFieldArrayTraits<int16_t>(numBits), numBits);
}

TEST_F(ArrayTest, intField20Array)
{
    const size_t numBits = 20;
    std::vector<int32_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray(rawArray, BitFieldArrayTraits<int32_t>(numBits), numBits);
}

TEST_F(ArrayTest, intField36Array)
{
    const size_t numBits = 36;
    std::vector<int64_t> rawArray = {-(INT64_C(1) << (numBits - 1)), 7, (INT64_C(1) << (numBits - 1)) - 1};
    testArray(rawArray, BitFieldArrayTraits<int64_t>(numBits), numBits);
}

TEST_F(ArrayTest, bitField4Array)
{
    const size_t numBits = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1 << numBits) - 1};
    testArray(rawArray, BitFieldArrayTraits<uint8_t>(numBits), numBits);
}

TEST_F(ArrayTest, bitField12Array)
{
    const size_t numBits = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1 << numBits) - 1};
    testArray(rawArray, BitFieldArrayTraits<uint16_t>(numBits), numBits);
}

TEST_F(ArrayTest, bitField20Array)
{
    const size_t numBits = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1 << numBits) - 1};
    testArray(rawArray, BitFieldArrayTraits<uint32_t>(numBits), numBits);
}

TEST_F(ArrayTest, bitField36Array)
{
    const size_t numBits = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << numBits) - 1};
    testArray(rawArray, BitFieldArrayTraits<uint64_t>(numBits), numBits);
}

TEST_F(ArrayTest, dynamicIntField4Array)
{
    const size_t numBits = 4;
    std::vector<int8_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<int8_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicIntField12Array)
{
    const size_t numBits = 12;
    std::vector<int16_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<int16_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicIntField20Array)
{
    const size_t numBits = 20;
    std::vector<int32_t> rawArray = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<int32_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicIntField36Array)
{
    const size_t numBits = 36;
    std::vector<int64_t> rawArray = {-(INT64_C(1) << (numBits - 1)), 7, (INT64_C(1) << (numBits - 1)) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<int64_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicBitField4Array)
{
    const size_t numBits = 4;
    std::vector<uint8_t> rawArray = {0, 7, (1 << numBits) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<uint8_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicBitField12Array)
{
    const size_t numBits = 12;
    std::vector<uint16_t> rawArray = {0, 7, (1 << numBits) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<uint16_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicBitField20Array)
{
    const size_t numBits = 20;
    std::vector<uint32_t> rawArray = {0, 7, (1 << numBits) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<uint32_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, dynamicBitField36Array)
{
    const size_t numBits = 36;
    std::vector<uint64_t> rawArray = {0, 7, (UINT64_C(1) << numBits) - 1};
    const ElementBitSize elementBitSize(numBits);
    testArray(rawArray, DynamicBitFieldArrayTraits<uint64_t, ElementBitSize>(elementBitSize), numBits);
}

TEST_F(ArrayTest, stdInt8Array)
{
    std::vector<int8_t> rawArray = {INT8_MIN, 7, INT8_MAX};
    testArray(rawArray, StdIntArrayTraits<int8_t>(), 8);
}

TEST_F(ArrayTest, stdInt16Array)
{
    std::vector<int16_t> rawArray = {INT16_MIN, 7, INT16_MAX};
    testArray(rawArray, StdIntArrayTraits<int16_t>(), 16);
}

TEST_F(ArrayTest, stdInt32Array)
{
    std::vector<int32_t> rawArray = {INT32_MIN, 7, INT32_MAX};
    testArray(rawArray, StdIntArrayTraits<int32_t>(), 32);
}

TEST_F(ArrayTest, stdInt64Array)
{
    std::vector<int64_t> rawArray = {INT64_MIN, 7, INT64_MAX};
    testArray(rawArray, StdIntArrayTraits<int64_t>(), 64);
}

TEST_F(ArrayTest, stdUInt8Array)
{
    std::vector<uint8_t> rawArray = {0, 7, UINT8_MAX};
    testArray(rawArray, StdIntArrayTraits<uint8_t>(), 8);
}

TEST_F(ArrayTest, stdUInt16Array)
{
    std::vector<uint16_t> rawArray = {0, 7, UINT16_MAX};
    testArray(rawArray, StdIntArrayTraits<uint16_t>(), 16);
}

TEST_F(ArrayTest, stdUInt32Array)
{
    std::vector<uint32_t> rawArray = {0, 7, UINT32_MAX};
    testArray(rawArray, StdIntArrayTraits<uint32_t>(), 32);
}

TEST_F(ArrayTest, stdUInt64Array)
{
    std::vector<uint64_t> rawArray = {0, 7, UINT64_MAX};
    testArray(rawArray, StdIntArrayTraits<uint64_t>(), 64);
}

TEST_F(ArrayTest, varInt16Array)
{
    std::vector<int16_t> rawArray = {1 << 5, 1 << (5 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray(rawArray, VarIntNNArrayTraits<int16_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varInt32Array)
{
    std::vector<int32_t> rawArray = {1 << 5, 1 << (5 + 7), 1 << (5 + 7 + 7), 1 << (5 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray(rawArray, VarIntNNArrayTraits<int32_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varInt64Array)
{
    std::vector<int64_t> rawArray = {
            INT64_C(1) << 5,
            INT64_C(1) << (5 + 7),
            INT64_C(1) << (5 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray(rawArray, VarIntNNArrayTraits<int64_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt16Array)
{
    std::vector<uint16_t> rawArray = {1 << 6, 1 << (6 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray(rawArray, VarIntNNArrayTraits<uint16_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt32Array)
{
    std::vector<uint32_t> rawArray = {1 << 6, 1 << (6 + 7), 1 << (6 + 7 + 7), 1 << (6 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray(rawArray, VarIntNNArrayTraits<uint32_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varUInt64Array)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6,
            UINT64_C(1) << (6 + 7),
            UINT64_C(1) << (6 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray(rawArray, VarIntNNArrayTraits<uint64_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varIntArray)
{
    std::vector<int64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(-1);
    rawArray.push_back(1);
    rawArray.push_back(-(INT64_C(1) << 6) + 1);
    rawArray.push_back((INT64_C(1) << 6) - 1);
    // 2 bytes
    rawArray.push_back(-(INT64_C(1) << 13) + 1);
    rawArray.push_back((INT64_C(1) << 13) - 1);
    // 3 bytes
    rawArray.push_back(-(INT64_C(1) << 20) + 1);
    rawArray.push_back((INT64_C(1) << 20) - 1);
    // 4 bytes
    rawArray.push_back(-(INT64_C(1) << 27) + 1);
    rawArray.push_back((INT64_C(1) << 27) - 1);
    // 5 bytes
    rawArray.push_back(-(INT64_C(1) << 34) + 1);
    rawArray.push_back((INT64_C(1) << 34) - 1);
    // 6 bytes
    rawArray.push_back(-(INT64_C(1) << 41) + 1);
    rawArray.push_back((INT64_C(1) << 41) - 1);
    // 7 bytes
    rawArray.push_back(-(INT64_C(1) << 48) + 1);
    rawArray.push_back((INT64_C(1) << 48) - 1);
    // 8 bytes
    rawArray.push_back(-(INT64_C(1) << 55) + 1);
    rawArray.push_back((INT64_C(1) << 55) - 1);
    // 9 bytes
    rawArray.push_back(INT64_MIN + 1);
    rawArray.push_back(INT64_MAX);
    // 1 byte - special case, INT64_MIN stored as -0
    rawArray.push_back(INT64_MIN);
    const size_t bitSize = 8 * (3 + 2 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9) + 1);
    testArray(rawArray, VarIntArrayTraits<int64_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varUIntArray)
{
    std::vector<uint64_t> rawArray;
    // 1 byte
    rawArray.push_back(0);
    rawArray.push_back(1);
    rawArray.push_back((UINT64_C(1) << 7) - 1);
    // 2 bytes
    rawArray.push_back((UINT64_C(1) << 14) - 1);
    // 3 bytes
    rawArray.push_back((UINT64_C(1) << 21) - 1);
    // 4 bytes
    rawArray.push_back((UINT64_C(1) << 28) - 1);
    // 5 bytes
    rawArray.push_back((UINT64_C(1) << 35) - 1);
    // 6 bytes
    rawArray.push_back((UINT64_C(1) << 42) - 1);
    // 7 bytes
    rawArray.push_back((UINT64_C(1) << 49) - 1);
    // 8 bytes
    rawArray.push_back((UINT64_C(1) << 56) - 1);
    // 9 bytes
    rawArray.push_back(UINT64_MAX);
    const size_t bitSize = 8 * (2 + (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9));
    testArray(rawArray, VarIntArrayTraits<uint64_t>(), bitSize, bitSize);
}

TEST_F(ArrayTest, varSizeArray)
{
    std::vector<uint32_t> rawArray = {
            UINT32_C(1) << 6,
            UINT32_C(1) << (6 + 7),
            UINT32_C(1) << (6 + 7 + 7),
            UINT32_C(1) << (6 + 7 + 7 + 7),
            UINT32_C(1) << (1 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5);
    testArray(rawArray, VarSizeArrayTraits(), bitSize, bitSize);
}

TEST_F(ArrayTest, float16Array)
{
    const size_t elementBitSize = 16;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray(rawArray, Float16ArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, float32Array)
{
    const size_t elementBitSize = 32;
    std::vector<float> rawArray = {-9.0, 0.0,  10.0};
    testArray(rawArray, Float32ArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, float64Array)
{
    const size_t elementBitSize = 64;
    std::vector<double> rawArray = {-9.0, 0.0, 10.0};
    testArray(rawArray, Float64ArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, boolArray)
{
    const size_t elementBitSize = 1;
    std::vector<bool> rawArray = {false, true};
    testArray(rawArray, BoolArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, bytesArray)
{
    const size_t bytesLengthBitSize = 8;
    const size_t bytesBitSize = 2 * 8;
    const size_t elementBitSize = bytesLengthBitSize + bytesBitSize;
    std::vector<std::vector<uint8_t>> rawArray = {{ {{ 1, 255 }}, {{ 127, 128 }} }};
    testArray(rawArray, BytesArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, stringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    std::vector<std::string> rawArray = {"String0", "String1", "String2"};
    testArray(rawArray, StringArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, bitBufferArray)
{
    const size_t bitBufferLengthBitSize = 8;
    const size_t bitBufferBitSize = 10;
    const size_t elementBitSize = bitBufferLengthBitSize + bitBufferBitSize;
    std::vector<BitBuffer> rawArray = {BitBuffer(bitBufferBitSize), BitBuffer(bitBufferBitSize),
            BitBuffer(bitBufferBitSize)};
    testArray(rawArray, BitBufferArrayTraits(), elementBitSize);
}

TEST_F(ArrayTest, enumArray)
{
    std::vector<DummyEnum> rawArray = {DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3};
    const size_t elementBitSize = 8;
    testArray(rawArray, EnumArrayTraits<DummyEnum>(), elementBitSize);

    std::vector<DummyEnum> invalidRawArray = {static_cast<DummyEnum>(10)};
    ASSERT_THROW(testArray(invalidRawArray, EnumArrayTraits<DummyEnum>(), elementBitSize), CppRuntimeException);
}

TEST_F(ArrayTest, bitmaskArray)
{
    std::vector<DummyBitmask> rawArray = {DummyBitmask::Values::READ, DummyBitmask::Values::WRITE,
            DummyBitmask::Values::CREATE};
    const size_t elementBitSize = 8;
    testArray(rawArray, BitmaskArrayTraits<DummyBitmask>(), elementBitSize);
}

TEST_F(ArrayTest, objectArray)
{
    std::vector<DummyObject> rawArray = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    testArrayInitializeElements(rawArray, ObjectArrayTraits<DummyObject, DummyObjectElementFactory>());
    testArray(rawArray, ObjectArrayTraits<DummyObject, DummyObjectElementFactory>(), 31,
            DummyObjectElementFactory());
}

TEST_F(ArrayTest, stdInt8PackedArray)
{
    std::vector<int8_t> rawArray = { -4, -3, -1, 0, 2, 4, 6, 8, 10, 10, 11 };
    testPackedArray(rawArray, StdIntArrayTraits<int8_t>());
}

TEST_F(ArrayTest, stdInt64PackedArray)
{
    // will not be packed
    std::vector<int64_t> rawArray = { INT64_MIN, 1, -1, INT64_MAX };
    testPackedArray(rawArray, StdIntArrayTraits<int64_t>());
}

TEST_F(ArrayTest, stdUInt64PackedArray)
{
    // will have maxBitNumber 62 bits
    std::vector<uint64_t> rawArray = { 0, INT64_MAX / 2, 100, 200, 300, 400, 500, 600, 700 };
    testPackedArray(rawArray, StdIntArrayTraits<uint64_t>());
}

TEST_F(ArrayTest, bitField64PackedArray)
{
    auto arrayTraits = BitFieldArrayTraits<uint64_t>(64);

    // none-zero delta
    std::vector<uint64_t> rawArray1 = {10, 11, 12};
    const size_t array1MaxDeltaBitSize = 1;
    const size_t array1BitSizeOf = calcPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    const size_t array1AlignedBitSizeOf = calcAlignedPackedBitSize(64, rawArray1.size(), array1MaxDeltaBitSize);
    testPackedArray(rawArray1, arrayTraits, array1BitSizeOf, array1AlignedBitSizeOf);

    // zero delta
    std::vector<uint64_t> rawArray2 = {10, 10, 10};
    const size_t array2BitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64;
    const size_t array2AlignedBitSizeOf = PACKING_DESCRIPTOR_BITSIZE + 64 +
            /* alignment before element 2 */ (8 - PACKING_DESCRIPTOR_BITSIZE);
    testPackedArray(rawArray2, arrayTraits, array2BitSizeOf, array2AlignedBitSizeOf);

    // one-element array
    std::vector<uint64_t> rawArray3 = {10};
    const size_t array3BitSizeOf = 1 + 64;
    const size_t array3AlignedBitSizeOf = 1 + 64; // no alignment before first element, no more elements
    testPackedArray(rawArray3, arrayTraits, array3BitSizeOf, array3AlignedBitSizeOf);

    // empty array
    std::vector<uint64_t> rawArray4 = {};
    const size_t array4BitSizeOf = 0;
    const size_t array4AlignedBitSizeOf = 0;
    testPackedArray(rawArray4, arrayTraits, array4BitSizeOf, array4AlignedBitSizeOf);

    // packing not enabled, delta is too big
    testPackedArray(std::vector<uint64_t>{0, UINT64_MAX}, arrayTraits);
    testPackedArray(std::vector<uint64_t>{UINT64_MAX, UINT64_MAX / 2, 0}, arrayTraits);

    // will have maxBitNumber 62 bits
    testPackedArray(
            std::vector<uint64_t>{0, static_cast<uint64_t>(INT64_MAX / 2), 100, 200, 300, 400, 500, 600, 700},
            arrayTraits);
}

TEST_F(ArrayTest, bitField8PackedArray)
{
    auto arrayTraits = BitFieldArrayTraits<uint8_t>(8);

    // will not be packed because unpacked 8bit values will be more efficient
    std::vector<uint8_t> rawArray1 = {UINT8_MAX, 0, 10, 20, 30, 40}; // max_bit_number 8, delta needs 9 bits
    const size_t array1BitSizeOf = 1 + 6 * 8;
    const size_t array1AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray(rawArray1, arrayTraits, array1BitSizeOf, array1AlignedBitSizeOf);

    // will not be packed because unpacked 8bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<uint8_t> rawArray2 =
            {UINT8_MAX, UINT8_MAX / 2 + 1, 10, 20, 30, 40}; // max_bit_number 7, delta needs 8 bits
    const size_t array2BitSizeOf = 1 + 6 * 8;
    const size_t array2AlignedBitSizeOf = 1 + 8 + /* alignment */ 7 + 5 * 8;
    testPackedArray(rawArray2, arrayTraits, array2BitSizeOf, array2AlignedBitSizeOf);
}

TEST_F(ArrayTest, intField64PackedArray)
{
    auto arrayTraits = BitFieldArrayTraits<int64_t>(64);

    testPackedArray(std::vector<int64_t>{-10, 11, -12}, arrayTraits);
    testPackedArray(std::vector<int64_t>{-10, -10, -10}, arrayTraits); // zero delta

    testPackedArray(std::vector<int64_t>{}, arrayTraits); // empty
    testPackedArray(std::vector<int64_t>{10}, arrayTraits); // single element

    // packing not enabled, delta is too big
    testPackedArray(std::vector<int64_t>{INT64_MIN, INT64_MAX}, arrayTraits);
    testPackedArray(std::vector<int64_t>{INT64_MIN, 0, INT64_MAX}, arrayTraits);
}

TEST_F(ArrayTest, intField16PackedArray)
{
    auto arrayTraits = BitFieldArrayTraits<int16_t>(16);

    // will not be packed because unpacked 16bit values will be more efficient
    // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
    std::vector<int16_t> rawArray = {INT16_MIN, -1, 10, 20, 30, 40}; // max_bit_number 15, delta needs 16 bits
    const size_t unpackedBitSizeOf = 1 + 6 * 16;
    const size_t unpackedAlignedBitSizeOf = 1 + 16 + /* alignment */ 7 + 5 * 16;
    testPackedArray(rawArray, arrayTraits, unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, varUInt64PackedArray)
{
    std::vector<uint64_t> rawArray = {
            UINT64_C(1) << 6,
            UINT64_C(1) << (6 + 7),
            UINT64_C(1) << (6 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    testPackedArray(rawArray, VarIntNNArrayTraits<uint64_t>());

    std::vector<uint64_t> unpackedRawArray = {UINT64_C(5000000), 0, 0, 0, 0, 0, 0};
    const size_t unpackedBitSizeOf = 1 + 32 + 6 * 8;
    const size_t unpackedAlignedBitSizeOf = 1 + 32 + /* alignment */ 7 + 6 * 8;
    testPackedArray(unpackedRawArray, VarIntNNArrayTraits<uint64_t>(),
            unpackedBitSizeOf, unpackedAlignedBitSizeOf);
}

TEST_F(ArrayTest, enumPackedArray)
{
    std::vector<DummyEnum> rawArray = {DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3};
    testPackedArray(rawArray, EnumArrayTraits<DummyEnum>());
}

TEST_F(ArrayTest, bitmaskPackedArray)
{
    std::vector<DummyBitmask> rawArray = {DummyBitmask::Values::READ, DummyBitmask::Values::WRITE,
            DummyBitmask::Values::CREATE};
    testPackedArray(rawArray, BitmaskArrayTraits<DummyBitmask>());
}

TEST_F(ArrayTest, objectPackedArray)
{
    std::vector<DummyObject> rawArray = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    testPackedArray(rawArray, ObjectArrayTraits<DummyObject, DummyObjectElementFactory>(),
            DummyObjectElementFactory());
}

} // namespace zserio
