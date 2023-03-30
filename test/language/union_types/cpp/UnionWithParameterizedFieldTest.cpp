#include "gtest/gtest.h"

#include "union_types/union_with_parameterized_field/TestUnion.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace union_types
{
namespace union_with_parameterized_field
{

using allocator_type = TestUnion::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class UnionWithParameterizedFieldTest : public ::testing::Test
{
protected:
    static constexpr size_t FIELD_BITSIZE = 8 + 32;
    static constexpr size_t ARRAY_HOLDER_BITSIZE = 8 + 10 * 32;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

constexpr size_t UnionWithParameterizedFieldTest::FIELD_BITSIZE;
constexpr size_t UnionWithParameterizedFieldTest::ARRAY_HOLDER_BITSIZE;

TEST_F(UnionWithParameterizedFieldTest, emptyConstructor)
{
    {
        TestUnion testUnion;
        ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

        testUnion.setArrayHolder(ArrayHolder());
        ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

        testUnion.initializeChildren();
        ASSERT_EQ(10, testUnion.getArrayHolder().getSize());
    }
    {
        TestUnion testUnion = {};
        ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

        testUnion.setArrayHolder(ArrayHolder());
        ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

        testUnion.initializeChildren();
        ASSERT_EQ(10, testUnion.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, readerConstructor)
{
    TestUnion testUnion;
    testUnion.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    testUnion.initializeChildren();
    zserio::BitStreamWriter writer(bitBuffer);
    testUnion.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    TestUnion readTestUnion(reader);
    ASSERT_EQ(10, readTestUnion.getArrayHolder().getSize());
}

TEST_F(UnionWithParameterizedFieldTest, copyConstructor)
{
    TestUnion testUnion;
    testUnion.setArrayHolder(ArrayHolder{});
    {
        TestUnion testUnionCopy(testUnion);
        ASSERT_THROW(testUnionCopy.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    testUnion.initializeChildren();
    {
        TestUnion testUnionCopy(testUnion);
        ASSERT_EQ(10, testUnionCopy.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, assignmentOperator)
{
    TestUnion testUnion;
    testUnion.setArrayHolder(ArrayHolder{});
    {
        TestUnion testUnionCopy;
        testUnionCopy = testUnion;
        ASSERT_THROW(testUnionCopy.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    testUnion.initializeChildren();
    {
        TestUnion testUnionCopy;
        testUnionCopy = testUnion;
        ASSERT_EQ(10, testUnionCopy.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, moveConstructor)
{
    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{});
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_THROW(testUnionMoved.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{});
        testUnion.initializeChildren();
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_EQ(10, testUnionMoved.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, moveAssignmentOperator)
{
    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{});
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_THROW(testUnionMoved.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{});
        testUnion.initializeChildren();
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_EQ(10, testUnionMoved.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, propagateAllocatorCopyConstructor)
{
    TestUnion testUnion;
    testUnion.setArrayHolder(ArrayHolder{});
    {
        TestUnion testUnionCopy(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
        ASSERT_THROW(testUnionCopy.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    testUnion.initializeChildren();
    {
        TestUnion testUnionCopy(zserio::PropagateAllocator, testUnion, TestUnion::allocator_type());
        ASSERT_EQ(10, testUnionCopy.getArrayHolder().getSize());
    }
}

TEST_F(UnionWithParameterizedFieldTest, choiceTag)
{
    TestUnion testUnion;
    ASSERT_EQ(TestUnion::UNDEFINED_CHOICE, testUnion.choiceTag());

    testUnion.setField(33);
    ASSERT_EQ(TestUnion::CHOICE_field, testUnion.choiceTag());

    testUnion.setArrayHolder(ArrayHolder{});
    ASSERT_EQ(TestUnion::CHOICE_arrayHolder, testUnion.choiceTag());
}

TEST_F(UnionWithParameterizedFieldTest, field)
{
    TestUnion testUnion;
    testUnion.setField(33);
    ASSERT_EQ(33, testUnion.getField());
}

TEST_F(UnionWithParameterizedFieldTest, arrayHolder)
{
    TestUnion testUnion;
    ArrayHolder arrayHolder{vector_type<uint32_t>(10)};
    arrayHolder.initialize(10);
    void* ptr = arrayHolder.getArray().data();
    testUnion.setArrayHolder(arrayHolder);
    ASSERT_NE(ptr, testUnion.getArrayHolder().getArray().data());
    ASSERT_EQ(arrayHolder, testUnion.getArrayHolder());

    testUnion.setArrayHolder(std::move(arrayHolder));
    ASSERT_EQ(ptr, testUnion.getArrayHolder().getArray().data());
}

TEST_F(UnionWithParameterizedFieldTest, bitSizeOf)
{
    TestUnion testUnion;

    testUnion.setField(33);
    ASSERT_EQ(FIELD_BITSIZE, testUnion.bitSizeOf());

    testUnion.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    testUnion.initializeChildren();
    ASSERT_EQ(ARRAY_HOLDER_BITSIZE, testUnion.bitSizeOf());
}

TEST_F(UnionWithParameterizedFieldTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    {
        TestUnion testUnion;
        testUnion.setField(33);
        ASSERT_EQ(bitPosition + FIELD_BITSIZE, testUnion.initializeOffsets(bitPosition));
    }

    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
        testUnion.initializeChildren();
        ASSERT_EQ(bitPosition + ARRAY_HOLDER_BITSIZE, testUnion.initializeOffsets(bitPosition));
    }
}

TEST_F(UnionWithParameterizedFieldTest, operatorEquality)
{
    TestUnion testUnion1;
    TestUnion testUnion2;
    ASSERT_TRUE(testUnion1 == testUnion2);
    testUnion1.setField(33);
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion2.setField(33);
    ASSERT_TRUE(testUnion1 == testUnion2);
    testUnion2.setField(32);
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion2.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    testUnion2.initializeChildren();
    ASSERT_FALSE(testUnion1 == testUnion2);
    testUnion1.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    ASSERT_THROW(testUnion1.operator==(testUnion2), zserio::CppRuntimeException);
    testUnion1.initializeChildren();
    ASSERT_TRUE(testUnion1 == testUnion2);
}

TEST_F(UnionWithParameterizedFieldTest, hashCode)
{
    TestUnion testUnion1;
    TestUnion testUnion2;
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion1.setField(33);
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.setField(33);
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.setField(32);
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());
    testUnion2.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    testUnion2.initializeChildren();
    ASSERT_NE(testUnion1.hashCode(), testUnion2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31520, testUnion1.hashCode());
    ASSERT_EQ(1174142900, testUnion2.hashCode());

    testUnion1.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
    ASSERT_THROW(testUnion1.hashCode(), zserio::CppRuntimeException);
    testUnion1.initializeChildren();
    ASSERT_EQ(testUnion1.hashCode(), testUnion2.hashCode());
}

TEST_F(UnionWithParameterizedFieldTest, write)
{
    {
        TestUnion testUnion;
        testUnion.setField(33);
        testUnion.initializeChildren();
        zserio::BitStreamWriter writer(bitBuffer);
        testUnion.write(writer);
        ASSERT_EQ(testUnion.bitSizeOf(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion readTestUnion(reader);
        ASSERT_EQ(testUnion, readTestUnion);
    }
    {
        TestUnion testUnion;
        testUnion.setArrayHolder(ArrayHolder{vector_type<uint32_t>(10)});
        testUnion.initializeChildren();
        zserio::BitStreamWriter writer(bitBuffer);
        testUnion.write(writer);
        ASSERT_EQ(testUnion.bitSizeOf(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestUnion readTestUnion(reader);
        ASSERT_EQ(testUnion, readTestUnion);
    }
}

} //namespace union_with_parameterized_field
} // namespace union_types
