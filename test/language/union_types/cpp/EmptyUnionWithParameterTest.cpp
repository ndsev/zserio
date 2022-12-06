#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "union_types/empty_union_with_parameter/EmptyUnionWithParameter.h"

namespace union_types
{
namespace empty_union_with_parameter
{

TEST(EmptyUnionWithParameterTest, emptyConstructor)
{
    {
        EmptyUnionWithParameter emptyUnionWithParameter;
        ASSERT_EQ(EmptyUnionWithParameter::UNDEFINED_CHOICE, emptyUnionWithParameter.choiceTag());
        ASSERT_EQ(0, emptyUnionWithParameter.bitSizeOf());

        ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);
    }
    {
        EmptyUnionWithParameter emptyUnionWithParameter = {};
        ASSERT_EQ(EmptyUnionWithParameter::UNDEFINED_CHOICE, emptyUnionWithParameter.choiceTag());
        ASSERT_EQ(0, emptyUnionWithParameter.bitSizeOf());

        ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);
    }
}

TEST(EmptyUnionWithParameterTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);
    EmptyUnionWithParameter emptyUnionWithParameter(reader, 1);
    ASSERT_EQ(1, emptyUnionWithParameter.getParam());
    ASSERT_EQ(EmptyUnionWithParameter::UNDEFINED_CHOICE, emptyUnionWithParameter.choiceTag());
    ASSERT_EQ(0, emptyUnionWithParameter.bitSizeOf());
}

TEST(EmptyUnionWithParameterTest, copyConstructor)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    EmptyUnionWithParameter emptyUnionCopy1WithParameter(emptyUnionWithParameter);

    ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyUnionCopy1WithParameter.getParam(), zserio::CppRuntimeException);

    emptyUnionWithParameter.initialize(1);
    EmptyUnionWithParameter emptyUnionCopy2WithParameter(emptyUnionWithParameter);
    ASSERT_EQ(emptyUnionWithParameter.getParam(), emptyUnionCopy2WithParameter.getParam());
}

TEST(EmptyUnionWithParameterTest, assignmentOperator)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    EmptyUnionWithParameter emptyUnionAssignWithParameter;
    emptyUnionAssignWithParameter = emptyUnionWithParameter;

    ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyUnionAssignWithParameter.getParam(), zserio::CppRuntimeException);

    emptyUnionWithParameter.initialize(1);
    emptyUnionAssignWithParameter = emptyUnionWithParameter;
    ASSERT_EQ(emptyUnionWithParameter.getParam(), emptyUnionAssignWithParameter.getParam());
}

TEST(EmptyUnionWithParameterTest, moveConstructor)
{
    {
        EmptyUnionWithParameter emptyUnionWithParameter;
        ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyUnionWithParameter emptyUnionMoveWithParameter(std::move(emptyUnionWithParameter));
        ASSERT_THROW(emptyUnionMoveWithParameter.getParam(), zserio::CppRuntimeException);
    }

    {
        EmptyUnionWithParameter emptyUnionWithParameter;
        emptyUnionWithParameter.initialize(1);
        ASSERT_EQ(1, emptyUnionWithParameter.getParam());

        EmptyUnionWithParameter emptyUnionMoveWithParameter(std::move(emptyUnionWithParameter));
        ASSERT_EQ(1, emptyUnionMoveWithParameter.getParam());
    }
}

TEST(EmptyUnionWithParameterTest, moveAssignmentOperator)
{
    {
        EmptyUnionWithParameter emptyUnionWithParameter;
        ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);

        EmptyUnionWithParameter emptyUnionAssignWithParameter;
        emptyUnionAssignWithParameter = std::move(emptyUnionWithParameter);
        ASSERT_THROW(emptyUnionAssignWithParameter.getParam(), zserio::CppRuntimeException);
    }

    {
        EmptyUnionWithParameter emptyUnionWithParameter;
        emptyUnionWithParameter.initialize(1);
        ASSERT_EQ(1, emptyUnionWithParameter.getParam());

        EmptyUnionWithParameter emptyUnionAssignWithParameter;
        emptyUnionAssignWithParameter = std::move(emptyUnionWithParameter);
        ASSERT_EQ(1, emptyUnionAssignWithParameter.getParam());
    }
}

TEST(EmptyUnionWithParameterTest, propagateAllocatorCopyConstructor)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    EmptyUnionWithParameter emptyUnionCopy1WithParameter(zserio::PropagateAllocator,
            emptyUnionWithParameter, EmptyUnionWithParameter::allocator_type());

    ASSERT_THROW(emptyUnionWithParameter.getParam(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyUnionCopy1WithParameter.getParam(), zserio::CppRuntimeException);

    emptyUnionWithParameter.initialize(1);
    EmptyUnionWithParameter emptyUnionCopy2WithParameter(zserio::PropagateAllocator,
            emptyUnionWithParameter, EmptyUnionWithParameter::allocator_type());
    ASSERT_EQ(emptyUnionWithParameter.getParam(), emptyUnionCopy2WithParameter.getParam());
}

TEST(EmptyUnionWithParameterTest, initialize)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    emptyUnionWithParameter.initialize(1);
    ASSERT_EQ(1, emptyUnionWithParameter.getParam());
}

TEST(EmptyUnionWithParameterTest, isInitialized)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    ASSERT_FALSE(emptyUnionWithParameter.isInitialized());
    emptyUnionWithParameter.initialize(1);
    ASSERT_TRUE(emptyUnionWithParameter.isInitialized());
}

TEST(EmptyUnionWithParameterTest, choiceTag)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    ASSERT_EQ(EmptyUnionWithParameter::UNDEFINED_CHOICE, emptyUnionWithParameter.choiceTag());
}

TEST(EmptyUnionWithParameterTest, bitSizeOf)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    const size_t bitPosition = 1;
    ASSERT_EQ(0, emptyUnionWithParameter.bitSizeOf(bitPosition));
}

TEST(EmptyUnionWithParameterTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    EmptyUnionWithParameter emptyUnionWithParameter;
    ASSERT_EQ(bitPosition, emptyUnionWithParameter.initializeOffsets(bitPosition));
}

TEST(EmptyUnionWithParameterTest, operatorEquality)
{
    EmptyUnionWithParameter emptyUnionWithParameter1;
    EmptyUnionWithParameter emptyUnionWithParameter2;
    ASSERT_THROW(emptyUnionWithParameter1.operator==(emptyUnionWithParameter2), zserio::CppRuntimeException);

    emptyUnionWithParameter1.initialize(1);
    ASSERT_THROW(emptyUnionWithParameter1.operator==(emptyUnionWithParameter2), zserio::CppRuntimeException);

    emptyUnionWithParameter2.initialize(1);
    ASSERT_TRUE(emptyUnionWithParameter1 == emptyUnionWithParameter2);

    emptyUnionWithParameter2.initialize(2);
    ASSERT_FALSE(emptyUnionWithParameter1 == emptyUnionWithParameter2);
}

TEST(EmptyUnionWithParameterTest, hashCode)
{
    EmptyUnionWithParameter emptyUnionWithParameter1;
    EmptyUnionWithParameter emptyUnionWithParameter2;
    ASSERT_THROW(emptyUnionWithParameter1.hashCode(), zserio::CppRuntimeException);
    ASSERT_THROW(emptyUnionWithParameter2.hashCode(), zserio::CppRuntimeException);

    emptyUnionWithParameter1.initialize(1);
    ASSERT_NO_THROW(emptyUnionWithParameter1.hashCode());

    emptyUnionWithParameter2.initialize(1);
    ASSERT_EQ(emptyUnionWithParameter1.hashCode(), emptyUnionWithParameter2.hashCode());

    emptyUnionWithParameter2.initialize(0);
    ASSERT_NE(emptyUnionWithParameter1.hashCode(), emptyUnionWithParameter2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31523, emptyUnionWithParameter1.hashCode());
    ASSERT_EQ(31486, emptyUnionWithParameter2.hashCode());
}

TEST(EmptyUnionWithParameterTest, write)
{
    EmptyUnionWithParameter emptyUnionWithParameter;
    emptyUnionWithParameter.initialize(1);

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    emptyUnionWithParameter.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EmptyUnionWithParameter readEmptyUnionWithParameter(reader, 1);
    ASSERT_TRUE(emptyUnionWithParameter == readEmptyUnionWithParameter);
}

} // namespace empty_union_with_parameter
} // namespace union_types
