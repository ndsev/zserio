#include "gtest/gtest.h"

#include "choice_types/bool_param_choice/BoolParamChoice.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace choice_types
{
namespace bool_param_choice
{

class BoolParamChoiceTest : public ::testing::Test
{
protected:
    void writeBoolParamChoiceToByteArray(zserio::BitStreamWriter& writer, bool selector, int32_t value)
    {
        if (selector)
            writer.writeSignedBits(value, 8);
        else
            writer.writeSignedBits(value, 16);
    }

    BoolParamChoice::ParameterExpressions parameterExpressionsTrue = {
            nullptr, 0, [](void*, size_t) { return true; } };
    BoolParamChoice::ParameterExpressions parameterExpressionsFalse = {
            nullptr, 0, [](void*, size_t) { return false; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(BoolParamChoiceTest, emptyConstructor)
{
    {
        BoolParamChoice boolParamChoice;
        ASSERT_THROW(boolParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        BoolParamChoice boolParamChoice = {};
        ASSERT_THROW(boolParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(BoolParamChoiceTest, bitStreamReaderConstructor)
{
    const int8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBoolParamChoiceToByteArray(writer, true, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BoolParamChoice boolParamChoice(reader, parameterExpressionsTrue);
    ASSERT_EQ(true, boolParamChoice.getSelector());
    ASSERT_EQ(value, boolParamChoice.getBlack());
}

TEST_F(BoolParamChoiceTest, copyConstructor)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsTrue);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);

    BoolParamChoice boolParamChoiceCopy(boolParamChoice);
    ASSERT_EQ(true, boolParamChoiceCopy.getSelector());
    ASSERT_EQ(value, boolParamChoiceCopy.getBlack());
}

TEST_F(BoolParamChoiceTest, assignmentOperator)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsFalse);
    const int16_t value = 234;
    boolParamChoice.setGrey(value);

    BoolParamChoice boolParamChoiceCopy;
    boolParamChoiceCopy = boolParamChoice;
    ASSERT_EQ(false, boolParamChoiceCopy.getSelector());
    ASSERT_EQ(value, boolParamChoiceCopy.getGrey());
}

TEST_F(BoolParamChoiceTest, moveConstructor)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsTrue);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const BoolParamChoice boolParamChoiceMoved(std::move(boolParamChoice));
    ASSERT_EQ(true, boolParamChoiceMoved.getSelector());
    ASSERT_EQ(value, boolParamChoiceMoved.getBlack());
}

TEST_F(BoolParamChoiceTest, moveAssignmentOperator)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsFalse);
    const int16_t value = 234;
    boolParamChoice.setGrey(value);

    // note that it doesn't ensure that move ctor was called
    BoolParamChoice boolParamChoiceMoved;
    boolParamChoiceMoved = std::move(boolParamChoice);
    ASSERT_EQ(false, boolParamChoiceMoved.getSelector());
    ASSERT_EQ(value, boolParamChoiceMoved.getGrey());
}

TEST_F(BoolParamChoiceTest, propagateAllocatorCopyConstructor)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsTrue);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);

    BoolParamChoice boolParamChoiceCopy(zserio::PropagateAllocator, boolParamChoice,
            BoolParamChoice::allocator_type());
    ASSERT_EQ(true, boolParamChoiceCopy.getSelector());
    ASSERT_EQ(value, boolParamChoiceCopy.getBlack());
}

TEST_F(BoolParamChoiceTest, initialize)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsFalse);
    ASSERT_EQ(false, boolParamChoice.getSelector());
}

TEST_F(BoolParamChoiceTest, getSelector)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsTrue);
    ASSERT_EQ(true, boolParamChoice.getSelector());
}

TEST_F(BoolParamChoiceTest, getSetBlack)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsTrue);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);
    ASSERT_EQ(value, boolParamChoice.getBlack());
}

TEST_F(BoolParamChoiceTest, getSetGrey)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(parameterExpressionsFalse);
    const int16_t value = 234;
    boolParamChoice.setGrey(value);
    ASSERT_EQ(value, boolParamChoice.getGrey());
}

TEST_F(BoolParamChoiceTest, choiceTag)
{
    BoolParamChoice boolParamChoiceBlack;
    boolParamChoiceBlack.initialize(parameterExpressionsTrue);
    ASSERT_EQ(BoolParamChoice::CHOICE_black, boolParamChoiceBlack.choiceTag());

    BoolParamChoice boolParamChoiceGrey;
    boolParamChoiceGrey.initialize(parameterExpressionsFalse);
    ASSERT_EQ(BoolParamChoice::CHOICE_grey, boolParamChoiceGrey.choiceTag());
}

TEST_F(BoolParamChoiceTest, bitSizeOf)
{
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(parameterExpressionsTrue);
    ASSERT_EQ(8, boolParamChoiceB.bitSizeOf());

    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(parameterExpressionsFalse);
    ASSERT_EQ(16, boolParamChoiceG.bitSizeOf());
}

TEST_F(BoolParamChoiceTest, initializeOffsets)
{
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(parameterExpressionsTrue);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, boolParamChoiceB.initializeOffsets(bitPosition));

    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(parameterExpressionsFalse);
    ASSERT_EQ(17, boolParamChoiceG.initializeOffsets(bitPosition));
}

TEST_F(BoolParamChoiceTest, operatorEquality)
{
    BoolParamChoice boolParamChoice1;
    boolParamChoice1.initialize(parameterExpressionsTrue);
    BoolParamChoice boolParamChoice2;
    boolParamChoice2.initialize(parameterExpressionsTrue);
    ASSERT_TRUE(boolParamChoice1 == boolParamChoice2);

    const int8_t value = 99;
    boolParamChoice1.setBlack(value);
    ASSERT_FALSE(boolParamChoice1 == boolParamChoice2);

    boolParamChoice2.setBlack(value);
    ASSERT_TRUE(boolParamChoice1 == boolParamChoice2);

    const int8_t diffValue = value + 1;
    boolParamChoice2.setBlack(diffValue);
    ASSERT_FALSE(boolParamChoice1 == boolParamChoice2);
}

TEST_F(BoolParamChoiceTest, hashCode)
{
    BoolParamChoice boolParamChoice1;
    boolParamChoice1.initialize(parameterExpressionsTrue);
    BoolParamChoice boolParamChoice2;
    boolParamChoice2.initialize(parameterExpressionsTrue);
    ASSERT_EQ(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    const int8_t value = 99;
    boolParamChoice1.setBlack(value);
    ASSERT_NE(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    boolParamChoice2.setBlack(value);
    ASSERT_EQ(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    boolParamChoice2.setBlack(diffValue);
    ASSERT_NE(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31623, boolParamChoice1.hashCode());
    ASSERT_EQ(31624, boolParamChoice2.hashCode());
}

TEST_F(BoolParamChoiceTest, write)
{
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(parameterExpressionsTrue);
    const int8_t valueB = 99;
    boolParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    boolParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    BoolParamChoice readBoolParamChoiceB(readerB, parameterExpressionsTrue);
    ASSERT_EQ(valueB, readBoolParamChoiceB.getBlack());

    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(parameterExpressionsFalse);
    const int16_t valueG = 234;
    boolParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG(bitBuffer);
    boolParamChoiceG.write(writerG);

    zserio::BitStreamReader readerG(writerG.getWriteBuffer(), writerG.getBitPosition(), zserio::BitsTag());
    BoolParamChoice readBoolParamChoiceG(readerG, parameterExpressionsFalse);
    ASSERT_EQ(valueG, readBoolParamChoiceG.getGrey());
}

} // namespace bool_param_choice
} // namespace choice_types
