#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "structure_types/empty_structure_with_parameter/EmptyStructureWithParameter.h"

namespace structure_types
{
namespace empty_structure_with_parameter
{

class EmptyStructureWithParameterTest : public ::testing::Test
{
protected:
    EmptyStructureWithParameter::ParameterExpressions parameterExpressions0 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int32_t>(0); } };
    EmptyStructureWithParameter::ParameterExpressions parameterExpressions1 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int32_t>(1); } };
    EmptyStructureWithParameter::ParameterExpressions parameterExpressions2 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int32_t>(2); } };
    EmptyStructureWithParameter::ParameterExpressions parameterExpressions10 = {
            nullptr, 0, [](void*, size_t) { return static_cast<int32_t>(10); } };
};

TEST_F(EmptyStructureWithParameterTest, emptyConstructor)
{
    {
        EmptyStructureWithParameter emptyStructureWithParameter;
        ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
        ASSERT_THROW(emptyStructureWithParameter.getParam(), zserio::CppRuntimeException);
    }
    {
        EmptyStructureWithParameter emptyStructureWithParameter = {};
        ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
        ASSERT_THROW(emptyStructureWithParameter.getParam(), zserio::CppRuntimeException);
    }
    {
        EmptyStructureWithParameter emptyStructureWithParameter;
        emptyStructureWithParameter.initialize(parameterExpressions10);
        ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
        ASSERT_EQ(10, emptyStructureWithParameter.getParam());
    }
}

TEST_F(EmptyStructureWithParameterTest, copyConstructor)
{
    EmptyStructureWithParameter testStructureWithParameter;
    EmptyStructureWithParameter testStructureWithParameterCopy1(testStructureWithParameter);

    ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(testStructureWithParameterCopy1.getParam(), zserio::CppRuntimeException);

    testStructureWithParameter.initialize(parameterExpressions1);
    EmptyStructureWithParameter testStructureWithParameterCopy2(testStructureWithParameter);
    ASSERT_EQ(testStructureWithParameter.getParam(), testStructureWithParameterCopy2.getParam());
}

TEST_F(EmptyStructureWithParameterTest, assignmentOperator)
{
    EmptyStructureWithParameter testStructureWithParameter;
    EmptyStructureWithParameter testStructureWithParameterAssign;
    testStructureWithParameterAssign = testStructureWithParameter;

    ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(testStructureWithParameterAssign.getParam(), zserio::CppRuntimeException);

    testStructureWithParameter.initialize(parameterExpressions1);
    testStructureWithParameterAssign = testStructureWithParameter;
    ASSERT_EQ(testStructureWithParameter.getParam(), testStructureWithParameterAssign.getParam());
}

TEST_F(EmptyStructureWithParameterTest, moveConstructor)
{
    {
        EmptyStructureWithParameter testStructureWithParameter;
        ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyStructureWithParameter testStructureWithParameterMoved(std::move(testStructureWithParameter));
        ASSERT_THROW(testStructureWithParameterMoved.getParam(), zserio::CppRuntimeException);
    }

    {
        EmptyStructureWithParameter testStructureWithParameter;
        testStructureWithParameter.initialize(parameterExpressions1);
        ASSERT_EQ(1, testStructureWithParameter.getParam());

        EmptyStructureWithParameter testStructureWithParameterMoved(std::move(testStructureWithParameter));
        ASSERT_EQ(1, testStructureWithParameterMoved.getParam());
    }
}

TEST_F(EmptyStructureWithParameterTest, moveAssignmentOperator)
{
    {
        EmptyStructureWithParameter testStructureWithParameter;
        ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyStructureWithParameter testStructureWithParameterMoved;
        testStructureWithParameterMoved = std::move(testStructureWithParameter);
        ASSERT_THROW(testStructureWithParameterMoved.getParam(), zserio::CppRuntimeException);
    }

    {
        EmptyStructureWithParameter testStructureWithParameter;
        testStructureWithParameter.initialize(parameterExpressions1);
        ASSERT_EQ(1, testStructureWithParameter.getParam());

        EmptyStructureWithParameter testStructureWithParameterMoved;
        testStructureWithParameterMoved = std::move(testStructureWithParameter);
        ASSERT_EQ(1, testStructureWithParameterMoved.getParam());
    }
}

TEST_F(EmptyStructureWithParameterTest, propagateAllocatorCopyConstructor)
{
    EmptyStructureWithParameter testStructureWithParameter;
    EmptyStructureWithParameter testStructureWithParameterCopy1(zserio::PropagateAllocator,
            testStructureWithParameter, EmptyStructureWithParameter::allocator_type());

    ASSERT_THROW(testStructureWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(testStructureWithParameterCopy1.getParam(), zserio::CppRuntimeException);

    testStructureWithParameter.initialize(parameterExpressions1);
    EmptyStructureWithParameter testStructureWithParameterCopy2(zserio::PropagateAllocator,
            testStructureWithParameter, EmptyStructureWithParameter::allocator_type());
    ASSERT_EQ(testStructureWithParameter.getParam(), testStructureWithParameterCopy2.getParam());
}

TEST_F(EmptyStructureWithParameterTest, initialize)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    emptyStructureWithParameter.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyStructureWithParameter.getParam());
}

TEST_F(EmptyStructureWithParameterTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyStructureWithParameter emptyStructureWithParameter(reader, parameterExpressions1);
    ASSERT_EQ(1, emptyStructureWithParameter.getParam());
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf());
}

TEST_F(EmptyStructureWithParameterTest, bitSizeOf)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    const size_t bitPosition = 1;
    ASSERT_EQ(0, emptyStructureWithParameter.bitSizeOf(bitPosition));
}

TEST_F(EmptyStructureWithParameterTest, initializeOffsets)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition, emptyStructureWithParameter.initializeOffsets(bitPosition));
}

TEST_F(EmptyStructureWithParameterTest, operatorEquality)
{
    EmptyStructureWithParameter emptyStructureWithParameter1;
    EmptyStructureWithParameter emptyStructureWithParameter2;
    ASSERT_THROW(ASSERT_FALSE(emptyStructureWithParameter1 == emptyStructureWithParameter2),
            zserio::CppRuntimeException);

    emptyStructureWithParameter1.initialize(parameterExpressions1);
    ASSERT_THROW(ASSERT_FALSE(emptyStructureWithParameter1 == emptyStructureWithParameter2),
            zserio::CppRuntimeException);

    emptyStructureWithParameter2.initialize(parameterExpressions1);
    ASSERT_TRUE(emptyStructureWithParameter1 == emptyStructureWithParameter2);

    emptyStructureWithParameter2.initialize(parameterExpressions2);
    ASSERT_FALSE(emptyStructureWithParameter1 == emptyStructureWithParameter2);
}

TEST_F(EmptyStructureWithParameterTest, hashCode)
{
    EmptyStructureWithParameter emptyStructureWithParameter1;
    EmptyStructureWithParameter emptyStructureWithParameter2;
    ASSERT_THROW(emptyStructureWithParameter1.hashCode(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyStructureWithParameter2.hashCode(), zserio::CppRuntimeException);

    emptyStructureWithParameter1.initialize(parameterExpressions1);
    ASSERT_NO_THROW(emptyStructureWithParameter1.hashCode());

    emptyStructureWithParameter2.initialize(parameterExpressions1);
    ASSERT_EQ(emptyStructureWithParameter1.hashCode(), emptyStructureWithParameter2.hashCode());

    emptyStructureWithParameter2.initialize(parameterExpressions0);
    ASSERT_NE(emptyStructureWithParameter1.hashCode(), emptyStructureWithParameter2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(852, emptyStructureWithParameter1.hashCode());
    ASSERT_EQ(851, emptyStructureWithParameter2.hashCode());
}

TEST_F(EmptyStructureWithParameterTest, write)
{
    EmptyStructureWithParameter emptyStructureWithParameter;
    emptyStructureWithParameter.initialize(parameterExpressions1);

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    emptyStructureWithParameter.write(writer);

    ASSERT_EQ(0, writer.getBitPosition());
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EmptyStructureWithParameter readEmptyStructureWithParameter(reader, parameterExpressions1);
    ASSERT_TRUE(emptyStructureWithParameter == readEmptyStructureWithParameter);
}

} // namespace empty_structure_with_parameter
} // namespace structure_types
