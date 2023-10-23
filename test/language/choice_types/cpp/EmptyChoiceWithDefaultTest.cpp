#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice_with_default/EmptyChoiceWithDefault.h"

namespace choice_types
{
namespace empty_choice_with_default
{

struct EmptyChoiceWithDefaultTest : public ::testing::Test
{
protected:
    EmptyChoiceWithDefault::ParameterExpressions parameterExpressions0 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(0); } };
    EmptyChoiceWithDefault::ParameterExpressions parameterExpressions1 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(1); } };
};

TEST_F(EmptyChoiceWithDefaultTest, emptyConstructor)
{
    {
        EmptyChoiceWithDefault emptyChoiceWithDefault;
        ASSERT_THROW(emptyChoiceWithDefault.getSelector(), zserio::CppRuntimeException);
    }
    {
        EmptyChoiceWithDefault emptyChoiceWithDefault = {};
        ASSERT_THROW(emptyChoiceWithDefault.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(EmptyChoiceWithDefaultTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyChoiceWithDefault emptyChoiceWithDefault(reader, parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithDefault.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefault.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, copyConstructor)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    const EmptyChoiceWithDefault emptyChoiceWithDefaultCopy(emptyChoiceWithDefault);
    ASSERT_EQ(1, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, assignmentOperator)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    EmptyChoiceWithDefault emptyChoiceWithDefaultCopy;
    emptyChoiceWithDefaultCopy = emptyChoiceWithDefault;
    ASSERT_EQ(1, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, moveConstructor)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoiceWithDefault emptyChoiceWithDefaultMoved(std::move(emptyChoiceWithDefault));
    ASSERT_EQ(1, emptyChoiceWithDefaultMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultMoved.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, moveAssignmentOperator)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    EmptyChoiceWithDefault emptyChoiceWithDefaultMoved;
    emptyChoiceWithDefaultMoved = std::move(emptyChoiceWithDefault);
    ASSERT_EQ(1, emptyChoiceWithDefaultMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultMoved.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, propagateAllocatorCopyConstructor)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    const EmptyChoiceWithDefault emptyChoiceWithDefaultCopy(zserio::PropagateAllocator, emptyChoiceWithDefault,
            EmptyChoiceWithDefault::allocator_type());
    ASSERT_EQ(1, emptyChoiceWithDefaultCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceWithDefaultCopy.bitSizeOf());
}

TEST_F(EmptyChoiceWithDefaultTest, initialize)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithDefault.getSelector());
}

TEST_F(EmptyChoiceWithDefaultTest, getSelector)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoiceWithDefault.getSelector());
}

TEST_F(EmptyChoiceWithDefaultTest, choiceTag)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions0);
    ASSERT_EQ(EmptyChoiceWithDefault::UNDEFINED_CHOICE, emptyChoiceWithDefault.choiceTag());

    emptyChoiceWithDefault.initialize(parameterExpressions1);
    ASSERT_EQ(EmptyChoiceWithDefault::UNDEFINED_CHOICE, emptyChoiceWithDefault.choiceTag());
}

TEST_F(EmptyChoiceWithDefaultTest, bitSizeOf)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    ASSERT_EQ(0, emptyChoiceWithDefault.bitSizeOf(1));
}

TEST_F(EmptyChoiceWithDefaultTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    ASSERT_EQ(bitPosition, emptyChoiceWithDefault.initializeOffsets(bitPosition));
}

TEST_F(EmptyChoiceWithDefaultTest, operatorEquality)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault1;
    emptyChoiceWithDefault1.initialize(parameterExpressions1);
    EmptyChoiceWithDefault emptyChoiceWithDefault2;
    emptyChoiceWithDefault2.initialize(parameterExpressions1);
    EmptyChoiceWithDefault emptyChoiceWithDefault3;
    emptyChoiceWithDefault3.initialize(parameterExpressions0);
    ASSERT_TRUE(emptyChoiceWithDefault1 == emptyChoiceWithDefault2);
    ASSERT_FALSE(emptyChoiceWithDefault1 == emptyChoiceWithDefault3);
}

TEST_F(EmptyChoiceWithDefaultTest, hashCode)
{
    EmptyChoiceWithDefault emptyChoiceWithDefault1;
    emptyChoiceWithDefault1.initialize(parameterExpressions1);
    EmptyChoiceWithDefault emptyChoiceWithDefault2;
    emptyChoiceWithDefault2.initialize(parameterExpressions1);
    EmptyChoiceWithDefault emptyChoiceWithDefault3;
    emptyChoiceWithDefault3.initialize(parameterExpressions0);
    ASSERT_EQ(emptyChoiceWithDefault1.hashCode(), emptyChoiceWithDefault2.hashCode());
    ASSERT_NE(emptyChoiceWithDefault1.hashCode(), emptyChoiceWithDefault3.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(852, emptyChoiceWithDefault1.hashCode());
    ASSERT_EQ(851, emptyChoiceWithDefault3.hashCode());
}

TEST_F(EmptyChoiceWithDefaultTest, write)
{
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    EmptyChoiceWithDefault emptyChoiceWithDefault;
    emptyChoiceWithDefault.initialize(parameterExpressions1);
    emptyChoiceWithDefault.write(writer);
    ASSERT_EQ(0, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), 0);
    EmptyChoiceWithDefault readEmptyChoiceWithDefault(reader, parameterExpressions1);
    ASSERT_EQ(emptyChoiceWithDefault, readEmptyChoiceWithDefault);
}

} // namespace empty_choice_with_default
} // namespace choice_types
