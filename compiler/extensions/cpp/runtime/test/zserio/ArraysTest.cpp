#include <string>
#include <vector>

#include "zserio/Arrays.h"

#include "gtest/gtest.h"

namespace zserio
{

class ArrayTestOffsetInitializer
{
public:
    void initializeOffset(size_t, size_t)
    {
    }
};

class ArrayTestOffsetChecker
{
public:
    void checkOffset(size_t, size_t)
    {
    }
};

class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    explicit DummyObject(uint32_t value) : m_value(value) {}
    explicit DummyObject(BitStreamReader& in) { read(in); }

    void initialize(uint32_t value)
    {
        m_value = value;
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return sizeof(uint32_t) * 8 - 1; // to make an unaligned type
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    bool operator==(const DummyObject& other) const
    {
        return m_value == other.m_value;
    }

    DummyObject operator+(const DummyObject& other) const
    {
        return DummyObject(m_value + other.m_value);
    }

    DummyObject& operator+=(const DummyObject& other)
    {
        m_value += other.m_value;
        return *this;
    }

    uint32_t getValue() const
    {
        return m_value;
    }

    void write(BitStreamWriter& out, PreWriteAction)
    {
        out.writeBits(m_value, static_cast<uint8_t>(bitSizeOf()));
    }

    void read(BitStreamReader& in)
    {
        m_value = in.readBits(static_cast<uint8_t>(bitSizeOf()));
    }

private:
    uint32_t    m_value;
};

class ArrayTestDummyObjectElementFactory
{
public:
    void create(std::vector<DummyObject>& array, BitStreamReader& in, size_t)
    {
        array.emplace_back(in);
    }
};

class ArraysTest : public ::testing::Test
{
public:
    ArraysTest()
    {
        memset(m_byteBuffer, 0, sizeof(m_byteBuffer) / sizeof(m_byteBuffer[0]));
    }

protected:
    template <typename ARRAY_TRAITS>
    void testArray(std::vector<typename ARRAY_TRAITS::type>& array, size_t elementBitSize)
    {
        const size_t arraySize = array.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0) ? elementBitSize +
                alignTo(NUM_BITS_PER_BYTE, elementBitSize) * (arraySize - 1) : 0;
        testArray<ARRAY_TRAITS>(array, unalignedBitSize, alignedBitSize);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY = nullptr_t>
    void testArray(std::vector<typename ARRAY_TRAITS::type>& array, size_t unalignedBitSize,
            size_t alignedBitSize, ELEMENT_FACTORY elementFactory = nullptr)
    {
        testSum<ARRAY_TRAITS>(array);
        testBitSizeOf<ARRAY_TRAITS>(array, unalignedBitSize);
        testBitSizeOfAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testBitSizeOfAligned<ARRAY_TRAITS>(array, alignedBitSize);
        testBitSizeOfAlignedAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testInitializeOffsets<ARRAY_TRAITS>(array, unalignedBitSize);
        testInitializeOffsetsAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testInitializeOffsetsAligned<ARRAY_TRAITS>(array, alignedBitSize);
        testInitializeOffsetsAlignedAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testRead<ARRAY_TRAITS, ELEMENT_FACTORY>(array, elementFactory);
        testReadAuto<ARRAY_TRAITS, ELEMENT_FACTORY>(array, elementFactory);
        testReadAligned<ARRAY_TRAITS, ELEMENT_FACTORY>(array, elementFactory);
        testReadAlignedAuto<ARRAY_TRAITS, ELEMENT_FACTORY>(array, elementFactory);
        testReadImplicit<ARRAY_TRAITS, ELEMENT_FACTORY>(array, elementFactory);
        testWrite<ARRAY_TRAITS>(array, unalignedBitSize);
        testWriteAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testWriteAligned<ARRAY_TRAITS>(array, alignedBitSize);
        testWriteAlignedAuto<ARRAY_TRAITS>(array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
    }

private:
    template <typename ARRAY_TRAITS>
    void testSum(const std::vector<typename ARRAY_TRAITS::type>& array)
    {
        typedef typename ARRAY_TRAITS::type element_type;

        element_type calculatedSum = element_type();
        for (const element_type& element : array)
            calculatedSum += element;

        EXPECT_EQ(calculatedSum, zserio::sum(array));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOf(const std::vector<typename ARRAY_TRAITS::type>& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(unalignedBitSize, zserio::bitSizeOf<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(unalignedBitSize, zserio::bitSizeOf<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAligned(const std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedBitSize)
    {
        EXPECT_EQ(alignedBitSize, zserio::bitSizeOfAligned<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(alignedBitSize + 1, zserio::bitSizeOfAligned<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAuto(const std::vector<typename ARRAY_TRAITS::type>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(autoUnalignedBitSize, zserio::bitSizeOfAuto<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(autoUnalignedBitSize, zserio::bitSizeOfAuto<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAlignedAuto(const std::vector<typename ARRAY_TRAITS::type>& array,
            size_t alignedAutoBitSize)
    {
        EXPECT_EQ(alignedAutoBitSize, zserio::bitSizeOfAlignedAuto<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(alignedAutoBitSize + 1, zserio::bitSizeOfAlignedAuto<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsets(std::vector<typename ARRAY_TRAITS::type>& array, size_t unalignedBitSize)
    {
        EXPECT_EQ(0 + unalignedBitSize, zserio::initializeOffsets<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(7 + unalignedBitSize, zserio::initializeOffsets<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAuto(std::vector<typename ARRAY_TRAITS::type>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(0 + autoUnalignedBitSize, zserio::initializeOffsetsAuto<ARRAY_TRAITS>(array, 0));
        EXPECT_EQ(7 + autoUnalignedBitSize, zserio::initializeOffsetsAuto<ARRAY_TRAITS>(array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAligned(std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedBitSize)
    {
        const size_t alignedBitPosition0 =
                zserio::initializeOffsetsAligned<ARRAY_TRAITS, ArrayTestOffsetInitializer>(array, 0,
                        ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedBitSize, alignedBitPosition0);

        const size_t alignedBitPosition7 =
                zserio::initializeOffsetsAligned<ARRAY_TRAITS, ArrayTestOffsetInitializer>(array, 7,
                        ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + alignedBitSize + 1, alignedBitPosition7);
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAlignedAuto(std::vector<typename ARRAY_TRAITS::type>& array,
    size_t alignedAutoBitSize)
    {
        const size_t alignedAutoBitPosition0 =
                zserio::initializeOffsetsAlignedAuto<ARRAY_TRAITS, ArrayTestOffsetInitializer>(array, 0,
                        ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedAutoBitSize, alignedAutoBitPosition0);

        const size_t alignedAutoBitPosition7 =
                zserio::initializeOffsetsAlignedAuto<ARRAY_TRAITS, ArrayTestOffsetInitializer>(array, 7,
                        ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + alignedAutoBitSize + 1, alignedAutoBitPosition7);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testRead(std::vector<typename ARRAY_TRAITS::type>& array, ELEMENT_FACTORY elementFactory)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        write<ARRAY_TRAITS>(array, writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> readArray;
        zserio::read<ARRAY_TRAITS, ELEMENT_FACTORY>(readArray, reader, array.size(), elementFactory);
        EXPECT_EQ(array, readArray);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAuto(std::vector<typename ARRAY_TRAITS::type>& array, ELEMENT_FACTORY elementFactory)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAuto<ARRAY_TRAITS>(array, writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> autoReadArray;
        zserio::readAuto<ARRAY_TRAITS, ELEMENT_FACTORY>(autoReadArray, reader, elementFactory);
        EXPECT_EQ(array, autoReadArray);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAligned(std::vector<typename ARRAY_TRAITS::type>& array, ELEMENT_FACTORY elementFactory)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAligned<ARRAY_TRAITS>(array, writer, ArrayTestOffsetChecker());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> alignedReadArray;
        zserio::readAligned<ARRAY_TRAITS, ArrayTestOffsetChecker, ELEMENT_FACTORY>(alignedReadArray, reader,
                array.size(), ArrayTestOffsetChecker(), elementFactory);
        EXPECT_EQ(array, alignedReadArray);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadAlignedAuto(std::vector<typename ARRAY_TRAITS::type>& array, ELEMENT_FACTORY elementFactory)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAlignedAuto<ARRAY_TRAITS>(array, writer, ArrayTestOffsetChecker());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> alignedAutoReadArray;
        zserio::readAlignedAuto<ARRAY_TRAITS, ArrayTestOffsetChecker, ELEMENT_FACTORY>(alignedAutoReadArray,
                reader, ArrayTestOffsetChecker(), elementFactory);
        EXPECT_EQ(array, alignedAutoReadArray);
    }

    template <typename ARRAY_TRAITS, typename ELEMENT_FACTORY>
    void testReadImplicit(std::vector<typename ARRAY_TRAITS::type>& array, ELEMENT_FACTORY elementFactory)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::write<ARRAY_TRAITS>(array, writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        std::vector<typename ARRAY_TRAITS::type> implicitReadArray;
        zserio::readImplicit<ARRAY_TRAITS, ELEMENT_FACTORY>(implicitReadArray, reader, elementFactory);
        for (size_t i = 0; i < implicitReadArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadArray[i]);
    }

    template <typename ARRAY_TRAITS>
    void testWrite(std::vector<typename ARRAY_TRAITS::type>& array, size_t unalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::write<ARRAY_TRAITS>(array, writer);
        EXPECT_EQ(unalignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAuto(std::vector<typename ARRAY_TRAITS::type>& array, size_t autoUnalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAuto<ARRAY_TRAITS>(array, writer);
        EXPECT_EQ(autoUnalignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAligned(std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAligned<ARRAY_TRAITS, ArrayTestOffsetChecker>(array, writer, ArrayTestOffsetChecker());
        EXPECT_EQ(alignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAlignedAuto(std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedAutoBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAlignedAuto<ARRAY_TRAITS, ArrayTestOffsetChecker>(array, writer, ArrayTestOffsetChecker());
        EXPECT_EQ(alignedAutoBitSize, writer.getBitPosition());
    }

    static const size_t AUTO_LENGTH_BIT_SIZE = 8;
    static const size_t BUFFER_SIZE = 256;

    uint8_t m_byteBuffer[BUFFER_SIZE];
};

TEST_F(ArraysTest, intField4Array)
{
    const size_t numBits = 4;
    std::vector<int8_t> array = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray<BitFieldArrayTraits<numBits, int8_t>>(array, numBits);
}

TEST_F(ArraysTest, intField12Array)
{
    const size_t numBits = 12;
    std::vector<int16_t> array = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray<BitFieldArrayTraits<numBits, int16_t>>(array, numBits);
}

TEST_F(ArraysTest, intField20Array)
{
    const size_t numBits = 20;
    std::vector<int32_t> array = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray<BitFieldArrayTraits<numBits, int32_t>>(array, numBits);
}

TEST_F(ArraysTest, intField36Array)
{
    const size_t numBits = 36;
    std::vector<int64_t> array = {-(INT64_C(1) << (numBits - 1)), 7, (INT64_C(1) << (numBits - 1)) - 1};
    testArray<BitFieldArrayTraits<numBits, int64_t>>(array, numBits);
}

TEST_F(ArraysTest, bitField4Array)
{
    const size_t numBits = 4;
    std::vector<uint8_t> array = {0, 7, (1 << numBits) - 1};
    testArray<BitFieldArrayTraits<numBits, uint8_t>>(array, numBits);
}

TEST_F(ArraysTest, bitField12Array)
{
    const size_t numBits = 12;
    std::vector<uint16_t> array = {0, 7, (1 << numBits) - 1};
    testArray<BitFieldArrayTraits<numBits, uint16_t>>(array, numBits);
}

TEST_F(ArraysTest, bitField20Array)
{
    const size_t numBits = 20;
    std::vector<uint32_t> array = {0, 7, (1 << numBits) - 1};
    testArray<BitFieldArrayTraits<numBits, uint32_t>>(array, numBits);
}

TEST_F(ArraysTest, bitField36Array)
{
    const size_t numBits = 36;
    std::vector<uint64_t> array = {0, 7, (UINT64_C(1) << numBits) - 1};
    testArray<BitFieldArrayTraits<numBits, uint64_t>>(array, numBits);
}

TEST_F(ArraysTest, stdInt8Array)
{
    std::vector<int8_t> array = {INT8_MIN, 7, INT8_MAX};
    testArray<StdIntArrayTraits<int8_t>>(array, 8);
}

TEST_F(ArraysTest, stdInt16Array)
{
    std::vector<int16_t> array = {INT16_MIN, 7, INT16_MAX};
    testArray<StdIntArrayTraits<int16_t>>(array, 16);
}

TEST_F(ArraysTest, stdInt32Array)
{
    std::vector<int32_t> array = {INT32_MIN, 7, INT32_MAX};
    testArray<StdIntArrayTraits<int32_t>>(array, 32);
}

TEST_F(ArraysTest, stdInt64Array)
{
    std::vector<int64_t> array = {INT64_MIN, 7, INT64_MAX};
    testArray<StdIntArrayTraits<int64_t>>(array, 64);
}

TEST_F(ArraysTest, stdUInt8Array)
{
    std::vector<uint8_t> array = {0, 7, UINT8_MAX};
    testArray<StdIntArrayTraits<uint8_t>>(array, 8);
}

TEST_F(ArraysTest, stdUInt16Array)
{
    std::vector<uint16_t> array = {0, 7, UINT16_MAX};
    testArray<StdIntArrayTraits<uint16_t>>(array, 16);
}

TEST_F(ArraysTest, stdUInt32Array)
{
    std::vector<uint32_t> array = {0, 7, UINT32_MAX};
    testArray<StdIntArrayTraits<uint32_t>>(array, 32);
}

TEST_F(ArraysTest, stdUInt64Array)
{
    std::vector<uint64_t> array = {0, 7, UINT64_MAX};
    testArray<StdIntArrayTraits<uint64_t>>(array, 64);
}

TEST_F(ArraysTest, varInt16Array)
{
    std::vector<int16_t> array = {1 << 5, 1 << (5 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<int16_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varInt32Array)
{
    std::vector<int32_t> array = {1 << 5, 1 << (5 + 7), 1 << (5 + 7 + 7), 1 << (5 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<int32_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varInt64Array)
{
    std::vector<int64_t> array = {
            INT64_C(1) << 5,
            INT64_C(1) << (5 + 7),
            INT64_C(1) << (5 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7 + 7),
            INT64_C(1) << (5 + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<int64_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUInt16Array)
{
    std::vector<uint16_t> array = {1 << 6, 1 << (6 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray<VarIntNNArrayTraits<uint16_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUInt32Array)
{
    std::vector<uint32_t> array = {1 << 6, 1 << (6 + 7), 1 << (6 + 7 + 7), 1 << (6 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray<VarIntNNArrayTraits<uint32_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUInt64Array)
{
    std::vector<uint64_t> array = {
            UINT64_C(1) << 6,
            UINT64_C(1) << (6 + 7),
            UINT64_C(1) << (6 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7),
            UINT64_C(1) << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);
    testArray<VarIntNNArrayTraits<uint64_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varIntArray)
{
    std::vector<int64_t> array;
    // 1 byte
    array.push_back(0);
    array.push_back(-1);
    array.push_back(1);
    array.push_back(-(INT64_C(1) << 6) + 1);
    array.push_back((INT64_C(1) << 6) - 1);
    // 2 bytes
    array.push_back(-(INT64_C(1) << 13) + 1);
    array.push_back((INT64_C(1) << 13) - 1);
    // 3 bytes
    array.push_back(-(INT64_C(1) << 20) + 1);
    array.push_back((INT64_C(1) << 20) - 1);
    // 4 bytes
    array.push_back(-(INT64_C(1) << 27) + 1);
    array.push_back((INT64_C(1) << 27) - 1);
    // 5 bytes
    array.push_back(-(INT64_C(1) << 34) + 1);
    array.push_back((INT64_C(1) << 34) - 1);
    // 6 bytes
    array.push_back(-(INT64_C(1) << 41) + 1);
    array.push_back((INT64_C(1) << 41) - 1);
    // 7 bytes
    array.push_back(-(INT64_C(1) << 48) + 1);
    array.push_back((INT64_C(1) << 48) - 1);
    // 8 bytes
    array.push_back(-(INT64_C(1) << 55) + 1);
    array.push_back((INT64_C(1) << 55) - 1);
    // 9 bytes
    array.push_back(INT64_MIN + 1);
    array.push_back(INT64_MAX);
    // 1 byte - special case, INT64_MIN stored as -0
    array.push_back(INT64_MIN);
    const size_t bitSize = 8 * (3 + 2 * (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9) + 1);
    testArray<VarIntArrayTraits<int64_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUIntArray)
{
    std::vector<uint64_t> array;
    // 1 byte
    array.push_back(0);
    array.push_back(1);
    array.push_back((UINT64_C(1) << 7) - 1);
    // 2 bytes
    array.push_back((UINT64_C(1) << 14) - 1);
    // 3 bytes
    array.push_back((UINT64_C(1) << 21) - 1);
    // 4 bytes
    array.push_back((UINT64_C(1) << 28) - 1);
    // 5 bytes
    array.push_back((UINT64_C(1) << 35) - 1);
    // 6 bytes
    array.push_back((UINT64_C(1) << 42) - 1);
    // 7 bytes
    array.push_back((UINT64_C(1) << 49) - 1);
    // 8 bytes
    array.push_back((UINT64_C(1) << 56) - 1);
    // 9 bytes
    array.push_back(UINT64_MAX);
    const size_t bitSize = 8 * (2 + (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9));
    testArray<VarIntArrayTraits<uint64_t>>(array, bitSize, bitSize);
}

TEST_F(ArraysTest, float16Array)
{
    const size_t elementBitSize = 16;
    std::vector<float> array = {-9.0, 0.0,  10.0};
    testArray<Float16ArrayTraits>(array, elementBitSize);
}

TEST_F(ArraysTest, float32Array)
{
    const size_t elementBitSize = 32;
    std::vector<float> array = {-9.0, 0.0,  10.0};
    testArray<Float32ArrayTraits>(array, elementBitSize);
}

TEST_F(ArraysTest, float64Array)
{
    const size_t elementBitSize = 64;
    std::vector<double> array = {-9.0, 0.0, 10.0};
    testArray<Float64ArrayTraits>(array, elementBitSize);
}

TEST_F(ArraysTest, boolArray)
{
    const size_t elementBitSize = 1;
    std::vector<bool> array = {false, true};
    testArray<BoolArrayTraits>(array, elementBitSize);
}

TEST_F(ArraysTest, stringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    std::vector<std::string> array = {"String0", "String1", "String2"};
    testArray<StringArrayTraits>(array, elementBitSize);
}

TEST_F(ArraysTest, objectArray)
{
    std::vector<DummyObject> array = {DummyObject(0xAB), DummyObject(0xCD), DummyObject(0xEF)};
    size_t unalignedBitSize = 0;
    size_t alignedBitSize = 0;
    for (size_t i = 0; i < array.size(); ++i)
    {
        const size_t bitSize = array[i].bitSizeOf();
        unalignedBitSize += bitSize;
        alignedBitSize += (i == 0) ? bitSize : alignTo(NUM_BITS_PER_BYTE, bitSize);
    }
    testArray<ObjectArrayTraits<DummyObject>, ArrayTestDummyObjectElementFactory>(array, unalignedBitSize,
            alignedBitSize, ArrayTestDummyObjectElementFactory());
}

} // namespace zserio
