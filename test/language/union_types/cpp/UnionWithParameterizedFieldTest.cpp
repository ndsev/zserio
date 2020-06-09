#include "gtest/gtest.h"

#include "union_types/union_with_parameterized_field/TestUnion.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace union_types
{
namespace union_with_parameterized_field
{

TEST(UnionWithParameterizedFieldTest, emptyConstructor)
{
    TestUnion testUnion;
    ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

    testUnion.setArrayHolder(ArrayHolder());
    ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

    testUnion.initializeChildren();
    ASSERT_EQ(10, testUnion.getArrayHolder().getSize());
}

TEST(UnionWithParameterizedFieldTest, fieldConstructor)
{
    TestUnion testUnion(ArrayHolder{});
    ASSERT_THROW(testUnion.getArrayHolder().getSize(), zserio::CppRuntimeException);

    testUnion.initializeChildren();
    ASSERT_EQ(10, testUnion.getArrayHolder().getSize());
}

TEST(UnionWithParameterizedFieldTest, readerConstructor)
{
    TestUnion testUnion(ArrayHolder{std::vector<uint32_t>(10)});
    testUnion.initializeChildren();
    zserio::BitStreamWriter writer;
    testUnion.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    TestUnion readTestUnion(reader);
    ASSERT_EQ(10, readTestUnion.getArrayHolder().getSize());
}

TEST(UnionWithParameterizedFieldTest, copyConstructor)
{
    TestUnion testUnion(ArrayHolder{});
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

TEST(UnionWithParameterizedFieldTest, assignmentOperator)
{
    TestUnion testUnion(ArrayHolder{});
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

TEST(UnionWithParameterizedFieldTest, moveConstructor)
{
    {
        TestUnion testUnion(ArrayHolder{});
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_THROW(testUnionMoved.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion(ArrayHolder{});
        testUnion.initializeChildren();
        TestUnion testUnionMoved(std::move(testUnion));
        ASSERT_EQ(10, testUnionMoved.getArrayHolder().getSize());
    }
}

TEST(UnionWithParameterizedFieldTest, moveAssignmentOperator)
{
    {
        TestUnion testUnion(ArrayHolder{});
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_THROW(testUnionMoved.getArrayHolder().getSize(), zserio::CppRuntimeException);
    }

    {
        TestUnion testUnion(ArrayHolder{});
        testUnion.initializeChildren();
        TestUnion testUnionMoved;
        testUnionMoved = std::move(testUnion);
        ASSERT_EQ(10, testUnionMoved.getArrayHolder().getSize());
    }
}

} //namespace union_with_parameterized_field
} // namespace union_types
