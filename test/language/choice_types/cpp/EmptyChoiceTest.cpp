#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"
#include "zserio/CppRuntimeException.h"

#include "choice_types/empty_choice/EmptyChoice.h"

namespace choice_types
{
namespace empty_choice
{

struct EmptyChoiceTest : public ::testing::Test
{
    EmptyChoice::ParameterExpressions parameterExpressions0 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(0); } };
    EmptyChoice::ParameterExpressions parameterExpressions1 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint8_t>(1); } };
};

TEST_F(EmptyChoiceTest, emptyConstructor)
{
    {
        EmptyChoice emptyChoice;
        ASSERT_THROW(emptyChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        EmptyChoice emptyChoice = {};
        ASSERT_THROW(emptyChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(EmptyChoiceTest, bitStreamReaderConstructor)
{
    zserio::BitStreamReader reader(nullptr, 0);

    EmptyChoice emptyChoice(reader, parameterExpressions1);
    ASSERT_EQ(1, emptyChoice.getSelector());
    ASSERT_EQ(0, emptyChoice.bitSizeOf());
}

TEST_F(EmptyChoiceTest, copyConstructor)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    const EmptyChoice emptyChoiceCopy(emptyChoice);
    ASSERT_EQ(1, emptyChoiceCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceCopy.bitSizeOf());
}

TEST_F(EmptyChoiceTest, assignmentOperator)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    EmptyChoice emptyChoiceCopy;
    emptyChoiceCopy = emptyChoice;
    ASSERT_EQ(1, emptyChoiceCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceCopy.bitSizeOf());
}

TEST_F(EmptyChoiceTest, moveConstructor)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    const EmptyChoice emptyChoiceMoved(std::move(emptyChoice));
    ASSERT_EQ(1, emptyChoiceMoved.getSelector());
    ASSERT_EQ(0, emptyChoiceMoved.bitSizeOf());
}

TEST_F(EmptyChoiceTest, moveAssignmentOperator)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    // note that it doesn't ensure that move ctor was called
    EmptyChoice emptyChoiceMove;
    emptyChoiceMove = std::move(emptyChoice);
    ASSERT_EQ(1, emptyChoiceMove.getSelector());
    ASSERT_EQ(0, emptyChoiceMove.bitSizeOf());
}

TEST_F(EmptyChoiceTest, propagateAllocatorCopyConstructor)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    const EmptyChoice emptyChoiceCopy(zserio::PropagateAllocator, emptyChoice, EmptyChoice::allocator_type());
    ASSERT_EQ(1, emptyChoiceCopy.getSelector());
    ASSERT_EQ(0, emptyChoiceCopy.bitSizeOf());
}

TEST_F(EmptyChoiceTest, initialize)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoice.getSelector());
}

TEST_F(EmptyChoiceTest, getSelector)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    ASSERT_EQ(1, emptyChoice.getSelector());
}

TEST_F(EmptyChoiceTest, choiceTag)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions0);
    ASSERT_EQ(EmptyChoice::UNDEFINED_CHOICE, emptyChoice.choiceTag());
}

TEST_F(EmptyChoiceTest, bitSizeOf)
{
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    ASSERT_EQ(0, emptyChoice.bitSizeOf(1));
}

TEST_F(EmptyChoiceTest, initializeOffsets)
{
    const size_t bitPosition = 1;

    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    ASSERT_EQ(bitPosition, emptyChoice.initializeOffsets(bitPosition));
}

TEST_F(EmptyChoiceTest, operatorEquality)
{
    EmptyChoice emptyChoice1;
    emptyChoice1.initialize(parameterExpressions1);
    EmptyChoice emptyChoice2;
    emptyChoice2.initialize(parameterExpressions1);
    EmptyChoice emptyChoice3;
    emptyChoice3.initialize(parameterExpressions0);
    ASSERT_TRUE(emptyChoice1 == emptyChoice2);
    ASSERT_FALSE(emptyChoice1 == emptyChoice3);
}

TEST_F(EmptyChoiceTest, hashCode)
{
    EmptyChoice emptyChoice1;
    emptyChoice1.initialize(parameterExpressions1);
    EmptyChoice emptyChoice2;
    emptyChoice2.initialize(parameterExpressions1);
    EmptyChoice emptyChoice3;
    emptyChoice3.initialize(parameterExpressions0);
    ASSERT_EQ(emptyChoice1.hashCode(), emptyChoice2.hashCode());
    ASSERT_NE(emptyChoice1.hashCode(), emptyChoice3.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(852, emptyChoice1.hashCode());
    ASSERT_EQ(851, emptyChoice3.hashCode());
}

TEST_F(EmptyChoiceTest, writeRead)
{
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    EmptyChoice emptyChoice;
    emptyChoice.initialize(parameterExpressions1);
    emptyChoice.write(writer);
    ASSERT_EQ(0, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), 0);
    EmptyChoice readEmptyChoice(reader, parameterExpressions1);
    ASSERT_EQ(emptyChoice, readEmptyChoice);
}

TEST_F(EmptyChoiceTest, writeReadFile)
{
    EmptyChoice emptyChoice;
    const std::string fileName = "language/choice_types/empty_choice.blob";
    zserio::serializeToFile(emptyChoice, fileName, parameterExpressions1);

    EmptyChoice readEmptyChoice = zserio::deserializeFromFile<EmptyChoice>(fileName, parameterExpressions1);
    ASSERT_EQ(emptyChoice, readEmptyChoice);
}

} // namespace empty_choice
} // namespace choice_types
