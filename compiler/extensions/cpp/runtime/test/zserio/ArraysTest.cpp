#include <string>
#include <vector>

#include "zserio/Arrays.h"
#include "zserio/Enums.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"

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
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: " +
                convertToString(rawValue) + "!");
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

    DummyBitmask() : m_value(0) {};
    explicit DummyBitmask(::zserio::BitStreamReader& in) :
        m_value(readValue(in))
    {};

    explicit DummyBitmask(underlying_type value) :
        m_value(value)
    {}

    underlying_type getValue() const
    {
        return m_value;
    }

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

    void read(::zserio::BitStreamReader& in)
    {
        m_value = readValue(in);
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
    void testArray(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t elementBitSize)
    {
        const size_t arraySize = array.size();
        const size_t unalignedBitSize = elementBitSize * arraySize;
        const size_t alignedBitSize = (arraySize > 0) ? elementBitSize +
                alignTo(NUM_BITS_PER_BYTE, elementBitSize) * (arraySize - 1) : 0;
        testArray(arrayTraits, array, unalignedBitSize, alignedBitSize);
    }

    template <typename ARRAY_TRAITS>
    void testArray(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t unalignedBitSize, size_t alignedBitSize)
    {
        testArray(arrayTraits, arrayTraits, array, unalignedBitSize, alignedBitSize);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testArray(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array, size_t unalignedBitSize, size_t alignedBitSize)
    {
        testBitSizeOf(arrayTraits, array, unalignedBitSize);
        testBitSizeOfAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testBitSizeOfAligned(arrayTraits, array, alignedBitSize);
        testBitSizeOfAlignedAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testInitializeOffsets(arrayTraits, array, unalignedBitSize);
        testInitializeOffsetsAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testInitializeOffsetsAligned(arrayTraits, array, alignedBitSize);
        testInitializeOffsetsAlignedAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
        testRead(arrayTraits, arrayReadTraits, array);
        testReadAuto(arrayTraits, arrayReadTraits, array);
        testReadAligned(arrayTraits, arrayReadTraits, array);
        testReadAlignedAuto(arrayTraits, arrayReadTraits, array);
        testReadImplicit(arrayTraits, arrayReadTraits, array);
        testWrite(arrayTraits, array, unalignedBitSize);
        testWriteAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + unalignedBitSize);
        testWriteAligned(arrayTraits, array, alignedBitSize);
        testWriteAlignedAuto(arrayTraits, array, AUTO_LENGTH_BIT_SIZE + alignedBitSize);
    }

    template <typename ARRAY_TRAITS>
    void testArrayInitializeElements(const ARRAY_TRAITS&, std::vector<typename ARRAY_TRAITS::type>& array)
    {
        typedef typename ARRAY_TRAITS::type element_type;

        zserio::initializeElements(array, ArrayTestDummyObjectElementInitializer());
        const uint32_t expectedValue = ArrayTestDummyObjectElementInitializer::ELEMENT_VALUE;
        for (const element_type& element : array)
            EXPECT_EQ(expectedValue, element.getValue());
    }

private:
    template <typename ARRAY_TRAITS>
    void testBitSizeOf(const ARRAY_TRAITS& arrayTraits, const std::vector<typename ARRAY_TRAITS::type>& array,
            size_t unalignedBitSize)
    {
        EXPECT_EQ(unalignedBitSize, zserio::bitSizeOf(arrayTraits, array, 0));
        EXPECT_EQ(unalignedBitSize, zserio::bitSizeOf(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAligned(const ARRAY_TRAITS& arrayTraits,
            const std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedBitSize)
    {
        EXPECT_EQ(alignedBitSize, zserio::bitSizeOfAligned(arrayTraits, array, 0));
        EXPECT_EQ(alignedBitSize + 1, zserio::bitSizeOfAligned(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAuto(const ARRAY_TRAITS& arrayTraits,
            const std::vector<typename ARRAY_TRAITS::type>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(autoUnalignedBitSize, zserio::bitSizeOfAuto(arrayTraits, array, 0));
        EXPECT_EQ(autoUnalignedBitSize, zserio::bitSizeOfAuto(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testBitSizeOfAlignedAuto(const ARRAY_TRAITS& arrayTraits,
            const std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedAutoBitSize)
    {
        EXPECT_EQ(alignedAutoBitSize, zserio::bitSizeOfAlignedAuto(arrayTraits, array, 0));
        EXPECT_EQ(alignedAutoBitSize + 1, zserio::bitSizeOfAlignedAuto(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsets(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t unalignedBitSize)
    {
        EXPECT_EQ(0 + unalignedBitSize, zserio::initializeOffsets(arrayTraits, array, 0));
        EXPECT_EQ(7 + unalignedBitSize, zserio::initializeOffsets(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAuto(const ARRAY_TRAITS& arrayTraits,
            std::vector<typename ARRAY_TRAITS::type>& array, size_t autoUnalignedBitSize)
    {
        EXPECT_EQ(0 + autoUnalignedBitSize, zserio::initializeOffsetsAuto(arrayTraits, array, 0));
        EXPECT_EQ(7 + autoUnalignedBitSize, zserio::initializeOffsetsAuto(arrayTraits, array, 7));
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAligned(const ARRAY_TRAITS& arrayTraits,
            std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedBitSize)
    {
        const size_t alignedBitPosition0 = zserio::initializeOffsetsAligned(arrayTraits, array, 0,
                ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedBitSize, alignedBitPosition0);

        const size_t alignedBitPosition7 = zserio::initializeOffsetsAligned(arrayTraits, array, 7,
                ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + alignedBitSize + 1, alignedBitPosition7);
    }

    template <typename ARRAY_TRAITS>
    void testInitializeOffsetsAlignedAuto(const ARRAY_TRAITS& arrayTraits,
            std::vector<typename ARRAY_TRAITS::type>& array, size_t alignedAutoBitSize)
    {
        const size_t alignedAutoBitPosition0 = zserio::initializeOffsetsAlignedAuto(arrayTraits, array, 0,
                ArrayTestOffsetInitializer());
        EXPECT_EQ(0 + alignedAutoBitSize, alignedAutoBitPosition0);

        const size_t alignedAutoBitPosition7 = zserio::initializeOffsetsAlignedAuto(arrayTraits, array, 7,
                ArrayTestOffsetInitializer());
        EXPECT_EQ(7 + alignedAutoBitSize + 1, alignedAutoBitPosition7);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testRead(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::write(arrayTraits, array, writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> readArray;
        zserio::read(arrayReadTraits, readArray, reader, array.size());
        EXPECT_EQ(array, readArray);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testReadAuto(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAuto(arrayTraits, array, writer);
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> autoReadArray;
        zserio::readAuto(arrayReadTraits, autoReadArray, reader);
        EXPECT_EQ(array, autoReadArray);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testReadAligned(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAligned(arrayTraits, array, writer, ArrayTestOffsetChecker());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> alignedReadArray;
        zserio::readAligned(arrayReadTraits, alignedReadArray, reader, array.size(), ArrayTestOffsetChecker());
        EXPECT_EQ(array, alignedReadArray);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testReadAlignedAuto(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAlignedAuto(arrayTraits, array, writer, ArrayTestOffsetChecker());
        BitStreamReader reader(m_byteBuffer, BUFFER_SIZE);
        std::vector<typename ARRAY_TRAITS::type> alignedAutoReadArray;
        zserio::readAlignedAuto(arrayReadTraits, alignedAutoReadArray, reader, ArrayTestOffsetChecker());
        EXPECT_EQ(array, alignedAutoReadArray);
    }

    template <typename ARRAY_TRAITS, typename ARRAY_READ_TRAITS>
    void testReadImplicit(const ARRAY_TRAITS& arrayTraits, const ARRAY_READ_TRAITS& arrayReadTraits,
            std::vector<typename ARRAY_TRAITS::type>& array)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::write(arrayTraits, array, writer);
        const size_t implicitByteSize = writer.getBitPosition() / 8;
        BitStreamReader reader(m_byteBuffer, implicitByteSize);
        std::vector<typename ARRAY_TRAITS::type> implicitReadArray;
        zserio::readImplicit(arrayReadTraits, implicitReadArray, reader);
        for (size_t i = 0; i < implicitReadArray.size(); ++i)
            EXPECT_EQ(array[i], implicitReadArray[i]);
    }

    template <typename ARRAY_TRAITS>
    void testWrite(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t unalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::write(arrayTraits, array, writer);
        EXPECT_EQ(unalignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t autoUnalignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAuto(arrayTraits, array, writer);
        EXPECT_EQ(autoUnalignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAligned(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t alignedBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAligned(arrayTraits, array, writer, ArrayTestOffsetChecker());
        EXPECT_EQ(alignedBitSize, writer.getBitPosition());
    }

    template <typename ARRAY_TRAITS>
    void testWriteAlignedAuto(const ARRAY_TRAITS& arrayTraits, std::vector<typename ARRAY_TRAITS::type>& array,
            size_t alignedAutoBitSize)
    {
        BitStreamWriter writer(m_byteBuffer, BUFFER_SIZE);
        zserio::writeAlignedAuto(arrayTraits, array, writer, ArrayTestOffsetChecker());
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
    testArray(BitFieldArrayTraits<int8_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, intField12Array)
{
    const size_t numBits = 12;
    std::vector<int16_t> array = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray(BitFieldArrayTraits<int16_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, intField20Array)
{
    const size_t numBits = 20;
    std::vector<int32_t> array = {-(1 << (numBits - 1)), 7, (1 << (numBits - 1)) - 1};
    testArray(BitFieldArrayTraits<int32_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, intField36Array)
{
    const size_t numBits = 36;
    std::vector<int64_t> array = {-(INT64_C(1) << (numBits - 1)), 7, (INT64_C(1) << (numBits - 1)) - 1};
    testArray(BitFieldArrayTraits<int64_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, bitField4Array)
{
    const size_t numBits = 4;
    std::vector<uint8_t> array = {0, 7, (1 << numBits) - 1};
    testArray(BitFieldArrayTraits<uint8_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, bitField12Array)
{
    const size_t numBits = 12;
    std::vector<uint16_t> array = {0, 7, (1 << numBits) - 1};
    testArray(BitFieldArrayTraits<uint16_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, bitField20Array)
{
    const size_t numBits = 20;
    std::vector<uint32_t> array = {0, 7, (1 << numBits) - 1};
    testArray(BitFieldArrayTraits<uint32_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, bitField36Array)
{
    const size_t numBits = 36;
    std::vector<uint64_t> array = {0, 7, (UINT64_C(1) << numBits) - 1};
    testArray(BitFieldArrayTraits<uint64_t>(numBits), array, numBits);
}

TEST_F(ArraysTest, stdInt8Array)
{
    std::vector<int8_t> array = {INT8_MIN, 7, INT8_MAX};
    testArray(StdIntArrayTraits<int8_t>(), array, 8);
}

TEST_F(ArraysTest, stdInt16Array)
{
    std::vector<int16_t> array = {INT16_MIN, 7, INT16_MAX};
    testArray(StdIntArrayTraits<int16_t>(), array, 16);
}

TEST_F(ArraysTest, stdInt32Array)
{
    std::vector<int32_t> array = {INT32_MIN, 7, INT32_MAX};
    testArray(StdIntArrayTraits<int32_t>(), array, 32);
}

TEST_F(ArraysTest, stdInt64Array)
{
    std::vector<int64_t> array = {INT64_MIN, 7, INT64_MAX};
    testArray(StdIntArrayTraits<int64_t>(), array, 64);
}

TEST_F(ArraysTest, stdUInt8Array)
{
    std::vector<uint8_t> array = {0, 7, UINT8_MAX};
    testArray(StdIntArrayTraits<uint8_t>(), array, 8);
}

TEST_F(ArraysTest, stdUInt16Array)
{
    std::vector<uint16_t> array = {0, 7, UINT16_MAX};
    testArray(StdIntArrayTraits<uint16_t>(), array, 16);
}

TEST_F(ArraysTest, stdUInt32Array)
{
    std::vector<uint32_t> array = {0, 7, UINT32_MAX};
    testArray(StdIntArrayTraits<uint32_t>(), array, 32);
}

TEST_F(ArraysTest, stdUInt64Array)
{
    std::vector<uint64_t> array = {0, 7, UINT64_MAX};
    testArray(StdIntArrayTraits<uint64_t>(), array, 64);
}

TEST_F(ArraysTest, varInt16Array)
{
    std::vector<int16_t> array = {1 << 5, 1 << (5 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray(VarIntNNArrayTraits<int16_t>(), array, bitSize, bitSize);
}

TEST_F(ArraysTest, varInt32Array)
{
    std::vector<int32_t> array = {1 << 5, 1 << (5 + 7), 1 << (5 + 7 + 7), 1 << (5 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray(VarIntNNArrayTraits<int32_t>(), array, bitSize, bitSize);
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
    testArray(VarIntNNArrayTraits<int64_t>(), array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUInt16Array)
{
    std::vector<uint16_t> array = {1 << 6, 1 << (6 + 8)};
    const size_t bitSize = 8 * (1 + 2);
    testArray(VarIntNNArrayTraits<uint16_t>(), array, bitSize, bitSize);
}

TEST_F(ArraysTest, varUInt32Array)
{
    std::vector<uint32_t> array = {1 << 6, 1 << (6 + 7), 1 << (6 + 7 + 7), 1 << (6 + 7 + 7 + 8)};
    const size_t bitSize = 8 * (1 + 2 + 3 + 4);
    testArray(VarIntNNArrayTraits<uint32_t>(), array, bitSize, bitSize);
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
    testArray(VarIntNNArrayTraits<uint64_t>(), array, bitSize, bitSize);
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
    testArray(VarIntArrayTraits<int64_t>(), array, bitSize, bitSize);
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
    testArray(VarIntArrayTraits<uint64_t>(), array, bitSize, bitSize);
}

TEST_F(ArraysTest, float16Array)
{
    const size_t elementBitSize = 16;
    std::vector<float> array = {-9.0, 0.0,  10.0};
    testArray(Float16ArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, float32Array)
{
    const size_t elementBitSize = 32;
    std::vector<float> array = {-9.0, 0.0,  10.0};
    testArray(Float32ArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, float64Array)
{
    const size_t elementBitSize = 64;
    std::vector<double> array = {-9.0, 0.0, 10.0};
    testArray(Float64ArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, boolArray)
{
    const size_t elementBitSize = 1;
    std::vector<bool> array = {false, true};
    testArray(BoolArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, stringArray)
{
    const size_t stringLengthBitSize = 8;
    const size_t stringBitSize = (sizeof("StringX") - 1) * 8; // without terminating character
    const size_t elementBitSize = stringLengthBitSize + stringBitSize;
    std::vector<std::string> array = {"String0", "String1", "String2"};
    testArray(StringArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, bitBufferArray)
{
    const size_t bitBufferLengthBitSize = 8;
    const size_t bitBufferBitSize = 10;
    const size_t elementBitSize = bitBufferLengthBitSize + bitBufferBitSize;
    std::vector<BitBuffer> array = {BitBuffer(bitBufferBitSize), BitBuffer(bitBufferBitSize),
            BitBuffer(bitBufferBitSize)};
    testArray(BitBufferArrayTraits(), array, elementBitSize);
}

TEST_F(ArraysTest, enumArray)
{
    std::vector<DummyEnum> array = {DummyEnum::VALUE1, DummyEnum::VALUE2, DummyEnum::VALUE3};
    const size_t elementBitSize = 8;
    testArray(EnumArrayTraits<DummyEnum>(), array, elementBitSize);
}

TEST_F(ArraysTest, bitmaskArray)
{
    std::vector<DummyBitmask> array = {DummyBitmask::Values::READ, DummyBitmask::Values::WRITE,
            DummyBitmask::Values::CREATE};
    const size_t elementBitSize = 8;
    testArray(BitmaskArrayTraits<DummyBitmask>(), array, elementBitSize);
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
    testArrayInitializeElements(ObjectArrayTraits<DummyObject>(), array);
    testArray(ObjectArrayTraits<DummyObject>(),
            ObjectArrayTraits<DummyObject, ArrayTestDummyObjectElementFactory>(
                    ArrayTestDummyObjectElementFactory()), array, unalignedBitSize, alignedBitSize);
}

} // namespace zserio
