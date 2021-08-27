#include <string>
#include <vector>

#include "zserio/Array.h"
#include "zserio/ArrayTraits.h"
#include "zserio/Enums.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/BitBuffer.h"

#include "gtest/gtest.h"

namespace zserio
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

template <>
DummyEnum valueToEnum(typename std::underlying_type<DummyEnum>::type rawValue)
{
    switch (rawValue)
    {
    case UINT8_C(0):
    case UINT8_C(1):
    case UINT8_C(2):
        return DummyEnum(rawValue);
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") + rawValue + "!";
    }
}

template <>
inline size_t bitSizeOf<DummyEnum>(DummyEnum)
{
    return UINT8_C(8);
}

template <>
inline size_t initializeOffsets<DummyEnum>(size_t bitPosition, DummyEnum value)
{
    return bitPosition + bitSizeOf(value);
}

template <>
inline DummyEnum read<DummyEnum>(zserio::BitStreamReader& in)
{
    return valueToEnum<DummyEnum>(
            static_cast<typename std::underlying_type<DummyEnum>::type>(in.readBits(UINT8_C(8))));
}

template <>
inline void write<DummyEnum>(BitStreamWriter& out, DummyEnum value)
{
    out.writeBits(enumToValue(value), UINT8_C(8));
}

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

    explicit DummyBitmask(::zserio::BitStreamReader& in) :
        m_value(readValue(in))
    {}

    explicit DummyBitmask(underlying_type value) :
        m_value(value)
    {}

    size_t bitSizeOf(size_t = 0) const
    {
        return UINT8_C(8);
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    bool operator==(const DummyBitmask& other) const
    {
        return m_value == other.m_value;
    }

    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS) const
    {
        out.writeBits(m_value, UINT8_C(8));
    }

private:
    static underlying_type readValue(::zserio::BitStreamReader& in)
    {
        return static_cast<underlying_type>(in.readBits(UINT8_C(8)));
    }

    underlying_type m_value;
};

const DummyBitmask DummyBitmask::Values::CREATE = DummyBitmask(UINT8_C(1));
const DummyBitmask DummyBitmask::Values::READ = DummyBitmask(UINT8_C(2));
const DummyBitmask DummyBitmask::Values::WRITE = DummyBitmask(UINT8_C(8));

class DummyObject
{
public:
    DummyObject() : m_value(0) {}
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
        context.init(m_value);
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
        endBitPosition += context.bitSizeOf(BitFieldArrayTraits<uint32_t>(31), endBitPosition, m_value);

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
        endBitPosition += context.bitSizeOf(BitFieldArrayTraits<uint32_t>(31), endBitPosition, m_value);

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

    void write(BitStreamWriter& out, PreWriteAction)
    {
        out.writeBits(m_value, static_cast<uint8_t>(bitSizeOf()));
    }

    void write(PackingContextNode& contextNode, BitStreamWriter& out)
    {
        auto& context = contextNode.getChildren().at(0).getContext();
        context.write(BitFieldArrayTraits<uint32_t>(31), out, m_value);
    }

private:
    uint32_t m_value;
};

class ArrayTestDummyObjectElementInitializer
{
public:
    void initialize(DummyObject& element, size_t _index) const
    {
        (void)_index;
        element.initialize(ELEMENT_VALUE);
    }

    static const uint32_t ELEMENT_VALUE = 0x12;
};

class ArrayTestDummyObjectElementFactory
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

class ArrayTest : public ::testing::Test
{
public:
    ArrayTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t elementBitSize)
    {
        const size_t arraySize = rawArray.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0) ? elementBitSize +
                alignTo(8, elementBitSize) * (arraySize - 1) : 0;
        testArray(rawArray, arrayTraits, unalignedBitSize, alignedBitSize);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t unalignedBitSize, size_t alignedBitSize,
            const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        testBitSizeOf(rawArray, arrayTraits, unalignedBitSize);
        testBitSizeOfAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testBitSizeOfAligned(rawArray, arrayTraits, alignedBitSize);
        testBitSizeOfAlignedAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testInitializeOffsets(rawArray, arrayTraits, unalignedBitSize);
        testInitializeOffsetsAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testInitializeOffsetsAligned(rawArray, arrayTraits, alignedBitSize);
        testInitializeOffsetsAlignedAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testRead(rawArray, arrayTraits, elementFactory);
        testReadAuto(rawArray, arrayTraits, elementFactory);
        testReadAligned(rawArray, arrayTraits, elementFactory);
        testReadAlignedAuto(rawArray, arrayTraits, elementFactory);
        testReadImplicit(rawArray, arrayTraits);
        testWrite(rawArray, arrayTraits, unalignedBitSize);
        testWriteAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testWriteAligned(rawArray, arrayTraits, alignedBitSize);
        testWriteAlignedAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testArrayInitializeElements(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        array.initializeElements(ArrayTestDummyObjectElementInitializer());
        const uint32_t expectedValue = ArrayTestDummyObjectElementInitializer::ELEMENT_VALUE;
        for (const auto& element : array.getRawArray())
            EXPECT_EQ(expectedValue, element.getValue());
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY = detail::DummyElementFactory>
    void testPackedArray(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t elementBitSize,
            const ELEMENT_FACTORY& elementFactory = ELEMENT_FACTORY())
    {
        const size_t arraySize = rawArray.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        testWritePackedAuto(rawArray, arrayTraits, AUTO_LENGTH_BIT_SIZE + unalignedBitSize, elementFactory);
    }

private:
    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testBitSizeOf(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t unalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(0));
        EXPECT_EQ(unalignedBitSize, array.bitSizeOf(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testBitSizeOfAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t autoUnalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(0));
        EXPECT_EQ(autoUnalignedBitSize, array.bitSizeOf(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testBitSizeOfAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t alignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        EXPECT_EQ(0 + alignedBitSize, array.bitSizeOf(0));
        EXPECT_EQ(7 + alignedBitSize, array.bitSizeOf(1));
        EXPECT_EQ(5 + alignedBitSize, array.bitSizeOf(3));
        EXPECT_EQ(3 + alignedBitSize, array.bitSizeOf(5));
        EXPECT_EQ(1 + alignedBitSize, array.bitSizeOf(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testBitSizeOfAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t alignedAutoBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        EXPECT_EQ(alignedAutoBitSize, array.bitSizeOf(0));
        EXPECT_EQ(alignedAutoBitSize + 1, array.bitSizeOf(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testInitializeOffsets(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t unalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        EXPECT_EQ(0 + unalignedBitSize, array.initializeOffsets(0));
        EXPECT_EQ(7 + unalignedBitSize, array.initializeOffsets(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testInitializeOffsetsAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t autoUnalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};
        EXPECT_EQ(0 + autoUnalignedBitSize, array.initializeOffsets(0));
        EXPECT_EQ(7 + autoUnalignedBitSize, array.initializeOffsets(7));
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testInitializeOffsetsAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t alignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

        const size_t alignedBitPosition0 = array.initializeOffsets(0, ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedBitSize, alignedBitPosition0);

        const size_t alignedBitPosition1 = array.initializeOffsets(1, ArrayTestOffsetInitializer());
        EXPECT_EQ(1 + 7 + alignedBitSize, alignedBitPosition1);

        const size_t alignedBitPosition3 = array.initializeOffsets(3, ArrayTestOffsetInitializer());
        EXPECT_EQ(3 + 5 + alignedBitSize, alignedBitPosition3);

        const size_t alignedBitPosition5 = array.initializeOffsets(5, ArrayTestOffsetInitializer());
        EXPECT_EQ(5 + 3 + alignedBitSize, alignedBitPosition5);

        const size_t alignedBitPosition7 = array.initializeOffsets(7, ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + 1 + alignedBitSize, alignedBitPosition7);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testInitializeOffsetsAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t alignedAutoBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};

        const size_t alignedAutoBitPosition0 = array.initializeOffsets(0, ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedAutoBitSize, alignedAutoBitPosition0);

        const size_t alignedAutoBitPosition7 = array.initializeOffsets(7, ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + alignedAutoBitSize + 1, alignedAutoBitPosition7);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testRead(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            const ELEMENT_FACTORY& elementFactory)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> readArray{arrayTraits};
        readArray.read(reader, rawArray.size(), elementFactory);
        EXPECT_EQ(array, readArray);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            const ELEMENT_FACTORY& elementFactory)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> readArray{arrayTraits};
        readArray.read(reader, elementFactory);
        EXPECT_EQ(array, readArray);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            const ELEMENT_FACTORY& elementFactory)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetChecker());

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
        readArray.read(reader, rawArray.size(), elementFactory, ArrayTestOffsetChecker());
        EXPECT_EQ(array, readArray);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            const ELEMENT_FACTORY& elementFactory)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetChecker());

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> readArray{arrayTraits};
        readArray.read(reader, elementFactory, ArrayTestOffsetChecker());
        EXPECT_EQ(array, readArray);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS,
            typename std::enable_if<ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testReadImplicit(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits)
    {
        if (arrayTraits.bitSizeOf() % 8 != 0)
            return; // implicit array allowed for types with constant bitsize rounded to bytes

        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::IMPLICIT> readArray{arrayTraits};
        readArray.read(reader);
        EXPECT_EQ(array, readArray);
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS,
            typename std::enable_if<!ARRAY_TRAITS::IS_BITSIZEOF_CONSTANT, int>::type = 0>
    void testReadImplicit(const RAW_ARRAY&, const ARRAY_TRAITS&)
    {
        // implicit array not allowed for types with non-constant bitsize, so skip the test
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testWrite(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t unalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::NORMAL> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        EXPECT_EQ(unalignedBitSize, writer.getBitPosition());
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testWriteAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t autoUnalignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer);
        EXPECT_EQ(autoUnalignedBitSize, writer.getBitPosition());
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS>
    void testWriteAligned(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits, size_t alignedBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        writer.writeBool(false);
        array.write(writer, ArrayTestOffsetChecker());
        EXPECT_EQ(1 + 7 + alignedBitSize, writer.getBitPosition());
    }

    template <typename RAW_ARRAY,typename ARRAY_TRAITS>
    void testWriteAlignedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t alignedAutoBitSize)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::ALIGNED_AUTO,
                ArrayTestOffsetChecker, ArrayTestOffsetInitializer> array{rawArray, arrayTraits};
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.write(writer, ArrayTestOffsetChecker());
        EXPECT_EQ(alignedAutoBitSize, writer.getBitPosition());
    }

    template <typename RAW_ARRAY, typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testWritePackedAuto(const RAW_ARRAY& rawArray, const ARRAY_TRAITS& arrayTraits,
            size_t unpackedBitSize, const ELEMENT_FACTORY& elementFactory)
    {
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> array{rawArray, arrayTraits};

        const size_t bitSizeOfResult = array.bitSizeOfPacked(0);
        const size_t initializeOffsetsResult = array.initializeOffsetsPacked(0);
        ASSERT_EQ(bitSizeOfResult, initializeOffsetsResult);

        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        array.writePacked(writer);
        ASSERT_GT(unpackedBitSize, writer.getBitPosition());
        ASSERT_EQ(initializeOffsetsResult, writer.getBitPosition());

        BitStreamReader reader(m_byteBuffer, writer.getBitPosition(), BitsTag());
        Array<RAW_ARRAY, ARRAY_TRAITS, ArrayType::AUTO> readArray{arrayTraits};
        readArray.readPacked(reader, elementFactory);

        ASSERT_EQ(array, readArray);
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;

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
    ASSERT_THROW(testArray(invalidRawArray, EnumArrayTraits<DummyEnum>(), elementBitSize),
            zserio::CppRuntimeException);
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
    size_t unalignedBitSize = 0;
    size_t alignedBitSize = 0;
    for (size_t i = 0; i < rawArray.size(); ++i)
    {
        const size_t bitSize = rawArray[i].bitSizeOf();
        unalignedBitSize += bitSize;
        alignedBitSize += (i == 0) ? bitSize : alignTo(8, bitSize);
    }
    testArrayInitializeElements(rawArray, ObjectArrayTraits<DummyObject, ArrayTestDummyObjectElementFactory>());
    testArray(rawArray, ObjectArrayTraits<DummyObject, ArrayTestDummyObjectElementFactory>(),
            unalignedBitSize, alignedBitSize, ArrayTestDummyObjectElementFactory());
}

TEST_F(ArrayTest, stdInt8PackedArray)
{
    std::vector<int8_t> rawArray = { 0, 2, 4, 6, 8, 10, 10, 11 };
    testPackedArray(rawArray, StdIntArrayTraits<int8_t>(), 8);
}

TEST_F(ArrayTest, objectPackedArray)
{
    std::vector<DummyObject> rawArray = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    testPackedArray(rawArray, ObjectArrayTraits<DummyObject, ArrayTestDummyObjectElementFactory>(), 31,
            ArrayTestDummyObjectElementFactory());
}

} // namespace zserio
