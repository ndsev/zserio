#include "gtest/gtest.h"

#include "union_types/union_with_array/TestUnion.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/CppRuntimeException.h"

namespace union_types
{
namespace union_with_array
{

using allocator_type = TestUnion::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class UnionWithArrayTest : public ::testing::Test
{
protected:
    static void writeArray8ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarSize(static_cast<uint32_t>(TestUnion::CHOICE_array8));
        writer.writeVarSize(ARRAY8_SIZE);
        for (size_t i = 0; i < ARRAY8_SIZE; ++i)
            writer.writeSignedBits(ARRAY8[i], 8);
    }

    static void writeArray16ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarSize(static_cast<uint32_t>(TestUnion::CHOICE_array16));
        writer.writeVarSize(ARRAY16_SIZE);
        for (size_t i = 0; i < ARRAY16_SIZE; ++i)
            writer.writeSignedBits(ARRAY16[i], 16);
    }

    template <size_t N>
    void checkArray(const int16_t(&array)[N], const vector_type<int16_t>& vector)
    {
        for (size_t i = 0; i < N; ++i)
        {
            ASSERT_EQ(array[i], vector.at(i));
        }
    }

    template <size_t N>
    void checkArray(const int8_t(&array)[N], const vector_type<Data8>& vector)
    {
        for (size_t i = 0; i < N; ++i)
        {
            ASSERT_EQ(array[i], vector.at(i).getData());
        }
    }

    vector_type<Data8> createArray8()
    {
        vector_type<Data8> array8(ARRAY8_SIZE);
        for (size_t i = 0; i < ARRAY8_SIZE; ++i)
            array8[i].setData(ARRAY8[i]);
        return array8;
    }

    vector_type<int16_t> createArray16()
    {
        return vector_type<int16_t>(ARRAY16, ARRAY16 + ARRAY16_SIZE);
    }

    static const std::string BLOB_NAME_BASE;

    static constexpr size_t ARRAY8_SIZE = 4;
    static const int8_t ARRAY8[ARRAY8_SIZE];
    static constexpr size_t ARRAY8_BITSIZE = 8 + 8 + ARRAY8_SIZE * 8;
    static constexpr size_t ARRAY16_SIZE = 5;
    static const int16_t ARRAY16[ARRAY16_SIZE];
    static constexpr size_t ARRAY16_BITSIZE = 8 + 8 + ARRAY16_SIZE * 16;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string UnionWithArrayTest::BLOB_NAME_BASE = "language/union_types/union_with_array_";

constexpr size_t UnionWithArrayTest::ARRAY8_SIZE;
constexpr size_t UnionWithArrayTest::ARRAY8_BITSIZE;
const int8_t UnionWithArrayTest::ARRAY8[ARRAY8_SIZE] = { -1, -2, -3, -4 };
constexpr size_t UnionWithArrayTest::ARRAY16_SIZE;
constexpr size_t UnionWithArrayTest::ARRAY16_BITSIZE;
const int16_t UnionWithArrayTest::ARRAY16[ARRAY16_SIZE] = { -10, -20, -30, -40, -50 };

TEST_F(UnionWithArrayTest, emptyConstructor)
{
    TestUnion test;
    ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, test.choiceTag());
    ASSERT_THROW(test.bitSizeOf(), zserio::CppRuntimeException);
}

TEST_F(UnionWithArrayTest, bitStreamReaderConstructor)
{
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writeArray8ToByteArray(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion testUnion(reader);
        ASSERT_THROW(testUnion.getArray16(), zserio::CppRuntimeException);
        const vector_type<Data8>& array8 = testUnion.getArray8();
        checkArray(ARRAY8, array8);
    }

    {
        zserio::BitStreamWriter writer(bitBuffer);
        writeArray16ToByteArray(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion testUnion(reader);
        ASSERT_THROW(testUnion.getArray8(), zserio::CppRuntimeException);
        const vector_type<int16_t>& array16 = testUnion.getArray16();
        checkArray(ARRAY16, array16);
    }
}

TEST_F(UnionWithArrayTest, copyConstructor)
{
    TestUnion testUnion;
    {
        TestUnion testUnionCopy(testUnion);
        ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnionCopy.choiceTag());
        ASSERT_THROW(testUnionCopy.bitSizeOf(), zserio::CppRuntimeException);
    }

    testUnion.setArray8(createArray8());
    {
        TestUnion testUnionCopy(testUnion);
        ASSERT_EQ(TestUnion::CHOICE_array8, testUnionCopy.choiceTag());
        ASSERT_EQ(testUnion.bitSizeOf(), testUnionCopy.bitSizeOf());
        checkArray(ARRAY8, testUnionCopy.getArray8());
    }
}

TEST_F(UnionWithArrayTest, assignmentOperator)
{
    TestUnion testUnion;
    {
        TestUnion testUnionCopy;
        testUnionCopy = testUnion;
        ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnionCopy.choiceTag());
        ASSERT_THROW(testUnionCopy.bitSizeOf(), zserio::CppRuntimeException);
    }

    testUnion.setArray16(createArray16());
    {
        TestUnion testUnionCopy;
        testUnionCopy = testUnion;
        ASSERT_EQ(TestUnion::CHOICE_array16, testUnionCopy.choiceTag());
        ASSERT_EQ(testUnion.bitSizeOf(), testUnionCopy.bitSizeOf());
        checkArray(ARRAY16, testUnionCopy.getArray16());
    }
}

TEST_F(UnionWithArrayTest, moveConstructor)
{
    {
        TestUnion testUnion;
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnionMoved.choiceTag());
        ASSERT_THROW(testUnionMoved.bitSizeOf(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.setArray8(createArray8());
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_EQ(TestUnion::CHOICE_array8, testUnionMoved.choiceTag());
        checkArray(ARRAY8, testUnionMoved.getArray8());
    }
}

TEST_F(UnionWithArrayTest, moveAssignmentOperator)
{
    {
        TestUnion testUnion;
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnionMoved.choiceTag());
        ASSERT_THROW(testUnionMoved.bitSizeOf(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.setArray16(createArray16());
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_EQ(TestUnion::CHOICE_array16, testUnionMoved.choiceTag());
        checkArray(ARRAY16, testUnionMoved.getArray16());
    }
}

TEST_F(UnionWithArrayTest, propagateAllocatorCopyConstructor)
{
    TestUnion testUnion;
    {
        TestUnion testUnionCopy(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
        ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnionCopy.choiceTag());
        ASSERT_THROW(testUnionCopy.bitSizeOf(), zserio::CppRuntimeException);
    }

    testUnion.setArray8(createArray8());
    {
        TestUnion testUnionCopy(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
        ASSERT_EQ(TestUnion::CHOICE_array8, testUnionCopy.choiceTag());
        ASSERT_EQ(testUnion.bitSizeOf(), testUnionCopy.bitSizeOf());
        checkArray(ARRAY8, testUnionCopy.getArray8());
    }
}

TEST_F(UnionWithArrayTest, choiceTag)
{
    TestUnion testUnion;
    ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnion.choiceTag());
    testUnion.setArray8(createArray8());
    ASSERT_EQ(TestUnion::CHOICE_array8, testUnion.choiceTag());
    testUnion.setArray16(createArray16());
    ASSERT_EQ(TestUnion::CHOICE_array16, testUnion.choiceTag());
}

TEST_F(UnionWithArrayTest, array8)
{
    TestUnion test;
    vector_type<Data8> data8(4);
    void* ptr = &data8[0];
    test.setArray8(data8);
    ASSERT_EQ(4, test.getArray8().size());
    ASSERT_NE(ptr, &test.getArray8()[0]);

    test.setArray8(std::move(data8));
    ASSERT_EQ(4, test.getArray8().size());
    ASSERT_EQ(ptr, &test.getArray8()[0]);
}

TEST_F(UnionWithArrayTest, array16)
{
    TestUnion test;
    vector_type<int16_t> data16(4);
    void* ptr = &data16[0];
    test.setArray16(data16);
    ASSERT_EQ(4, test.getArray16().size());
    ASSERT_NE(ptr, &test.getArray16()[0]);

    test.setArray16(std::move(data16));
    ASSERT_EQ(4, test.getArray16().size());
    ASSERT_EQ(ptr, &test.getArray16()[0]);
}

TEST_F(UnionWithArrayTest, bitSizeOf)
{
    TestUnion testUnion;
    testUnion.setArray8(createArray8());
    ASSERT_EQ(ARRAY8_BITSIZE, testUnion.bitSizeOf());

    testUnion.setArray16(createArray16());
    ASSERT_EQ(ARRAY16_BITSIZE, testUnion.bitSizeOf());
}

TEST_F(UnionWithArrayTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    {
        TestUnion testUnion;
        testUnion.setArray8(createArray8());
        ASSERT_EQ(bitPosition + ARRAY8_BITSIZE, testUnion.initializeOffsets(bitPosition));
    }
    {
        TestUnion testUnion;
        testUnion.setArray16(createArray16());
        ASSERT_EQ(bitPosition + ARRAY16_BITSIZE, testUnion.initializeOffsets(bitPosition));
    }
}

TEST_F(UnionWithArrayTest, operatorEquality)
{
    TestUnion testUnion1;
    TestUnion testUnion2;
    ASSERT_TRUE(testUnion1 == testUnion2);
    testUnion1.setArray8(createArray8());
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion2.setArray8(createArray8());
    ASSERT_TRUE(testUnion1 == testUnion2);
    testUnion2.getArray8()[0].setData(0);
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion2.setArray16(createArray16());
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion1.setArray16(createArray16());
    ASSERT_TRUE(testUnion1 == testUnion2);
}

TEST_F(UnionWithArrayTest, hashCode)
{
    TestUnion testUnion1;
    TestUnion testUnion2;
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion1.setArray8(createArray8());
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.setArray8(createArray8());
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.getArray8()[0].setData(0);
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.setArray16(createArray16());
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion1.setArray16(createArray16());
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
}

TEST_F(UnionWithArrayTest, writeRead)
{
    {
        TestUnion testUnion;
        testUnion.setArray8(createArray8());
        zserio::BitStreamWriter writer(bitBuffer);
        testUnion.write(writer);
        ASSERT_EQ(testUnion.bitSizeOf(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion readTestUnion(reader);
        ASSERT_EQ(testUnion, readTestUnion);
        checkArray(ARRAY8, readTestUnion.getArray8());
    }
    {
        TestUnion testUnion;
        testUnion.setArray16(createArray16());
        zserio::BitStreamWriter writer(bitBuffer);
        testUnion.write(writer);
        ASSERT_EQ(testUnion.bitSizeOf(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion readTestUnion(reader);
        ASSERT_EQ(testUnion, readTestUnion);
        checkArray(ARRAY16, readTestUnion.getArray16());
    }
}

TEST_F(UnionWithArrayTest, writeReadFile)
{
    {
        TestUnion testUnion;
        testUnion.setArray8(createArray8());
        const std::string fileName = BLOB_NAME_BASE + "array8.blob";
        zserio::serializeToFile(testUnion, fileName);

        TestUnion readTestUnion = zserio::deserializeFromFile<TestUnion>(fileName);
        ASSERT_EQ(testUnion, readTestUnion);
        checkArray(ARRAY8, readTestUnion.getArray8());
    }
    {
        TestUnion testUnion;
        testUnion.setArray16(createArray16());
        const std::string fileName = BLOB_NAME_BASE + "array16.blob";
        zserio::serializeToFile(testUnion, fileName);

        TestUnion readTestUnion = zserio::deserializeFromFile<TestUnion>(fileName);
        ASSERT_EQ(testUnion, readTestUnion);
        checkArray(ARRAY16, readTestUnion.getArray16());
    }
}

} // namespace union_with_array
} // namespace union_types
