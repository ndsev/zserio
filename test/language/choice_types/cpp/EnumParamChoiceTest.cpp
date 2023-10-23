#include "gtest/gtest.h"

#include "choice_types/enum_param_choice/Selector.h"
#include "choice_types/enum_param_choice/EnumParamChoice.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace choice_types
{
namespace enum_param_choice
{

class EnumParamChoiceTest : public ::testing::Test
{
protected:
    void writeEnumParamChoiceToByteArray(zserio::BitStreamWriter& writer, Selector selector, int32_t value)
    {
        switch (selector)
        {
        case Selector::BLACK:
            writer.writeSignedBits(value, 8);
            break;

        case Selector::GREY:
            writer.writeSignedBits(value, 16);
            break;

        case Selector::WHITE:
            writer.writeSignedBits(value, 32);
            break;

        default:
            FAIL() << "Bad choice selector";
        }
    }

    EnumParamChoice::ParameterExpressions parameterExpressionsBlack = {
            nullptr, 0, [](void*, size_t) { return Selector::BLACK; } };
    EnumParamChoice::ParameterExpressions parameterExpressionsGrey = {
            nullptr, 0, [](void*, size_t) { return Selector::GREY; } };
    EnumParamChoice::ParameterExpressions parameterExpressionsWhite = {
            nullptr, 0, [](void*, size_t) { return Selector::WHITE; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(EnumParamChoiceTest, emptyConstructor)
{
    {
        EnumParamChoice enumParamChoice;
        ASSERT_THROW(enumParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        EnumParamChoice enumParamChoice = {};
        ASSERT_THROW(enumParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(EnumParamChoiceTest, bitStreamReaderConstructor)
{
    const int8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeEnumParamChoiceToByteArray(writer, Selector::BLACK, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    EnumParamChoice enumParamChoice(reader, parameterExpressionsBlack);
    ASSERT_EQ(Selector::BLACK, enumParamChoice.getSelector());
    ASSERT_EQ(value, enumParamChoice.getBlack());
}

TEST_F(EnumParamChoiceTest, copyConstructor)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);

    const EnumParamChoice enumParamChoiceCopy(enumParamChoice);
    ASSERT_EQ(Selector::BLACK, enumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, enumParamChoiceCopy.getBlack());
}

TEST_F(EnumParamChoiceTest, assignmentOperator)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    enumParamChoice.setGrey(value);

    EnumParamChoice enumParamChoiceCopy;
    enumParamChoiceCopy = enumParamChoice;
    ASSERT_EQ(Selector::GREY, enumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, enumParamChoiceCopy.getGrey());
}

TEST_F(EnumParamChoiceTest, moveCopyConstructor)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const EnumParamChoice enumParamChoiceMoved(std::move(enumParamChoice));
    ASSERT_EQ(Selector::BLACK, enumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, enumParamChoiceMoved.getBlack());
}

TEST_F(EnumParamChoiceTest, moveAssignmentOperator)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    enumParamChoice.setGrey(value);

    // note that it doesn't ensure that move ctor was called
    EnumParamChoice enumParamChoiceMoved;
    enumParamChoiceMoved = std::move(enumParamChoice);
    ASSERT_EQ(Selector::GREY, enumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, enumParamChoiceMoved.getGrey());
}

TEST_F(EnumParamChoiceTest, propagateAllocatorCopyConstructor)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);

    const EnumParamChoice enumParamChoiceCopy(zserio::PropagateAllocator, enumParamChoice,
            EnumParamChoice::allocator_type());
    ASSERT_EQ(Selector::BLACK, enumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, enumParamChoiceCopy.getBlack());
}

TEST_F(EnumParamChoiceTest, initialize)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsGrey);
    ASSERT_EQ(Selector::GREY, enumParamChoice.getSelector());
}

TEST_F(EnumParamChoiceTest, getSelector)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(Selector::BLACK, enumParamChoice.getSelector());
}

TEST_F(EnumParamChoiceTest, getSetBlack)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);
    ASSERT_EQ(value, enumParamChoice.getBlack());
}

TEST_F(EnumParamChoiceTest, getSetGrey)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    enumParamChoice.setGrey(value);
    ASSERT_EQ(value, enumParamChoice.getGrey());
}

TEST_F(EnumParamChoiceTest, getSetWhite)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(parameterExpressionsWhite);
    const int32_t value = 65535;
    enumParamChoice.setWhite(value);
    ASSERT_EQ(value, enumParamChoice.getWhite());
}

TEST_F(EnumParamChoiceTest, choiceTag)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(EnumParamChoice::CHOICE_black, enumParamChoiceB.choiceTag());

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(EnumParamChoice::CHOICE_grey, enumParamChoiceG.choiceTag());

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(EnumParamChoice::CHOICE_white, enumParamChoiceW.choiceTag());
}

TEST_F(EnumParamChoiceTest, bitSizeOf)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(8, enumParamChoiceB.bitSizeOf());

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(16, enumParamChoiceG.bitSizeOf());

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(32, enumParamChoiceW.bitSizeOf());
}

TEST_F(EnumParamChoiceTest, initializeOffsets)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(parameterExpressionsBlack);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, enumParamChoiceB.initializeOffsets(bitPosition));

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(17, enumParamChoiceG.initializeOffsets(bitPosition));

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(33, enumParamChoiceW.initializeOffsets(bitPosition));
}

TEST_F(EnumParamChoiceTest, operatorEquality)
{
    EnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(parameterExpressionsBlack);
    EnumParamChoice enumParamChoice2;
    enumParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_TRUE(enumParamChoice1 == enumParamChoice2);

    const int8_t value = 99;
    enumParamChoice1.setBlack(value);
    ASSERT_FALSE(enumParamChoice1 == enumParamChoice2);

    enumParamChoice2.setBlack(value);
    ASSERT_TRUE(enumParamChoice1 == enumParamChoice2);

    const int8_t diffValue = value + 1;
    enumParamChoice2.setBlack(diffValue);
    ASSERT_FALSE(enumParamChoice1 == enumParamChoice2);
}

TEST_F(EnumParamChoiceTest, hashCode)
{
    EnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(parameterExpressionsBlack);
    EnumParamChoice enumParamChoice2;
    enumParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_EQ(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    const int8_t value = 99;
    enumParamChoice1.setBlack(value);
    ASSERT_NE(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    enumParamChoice2.setBlack(value);
    ASSERT_EQ(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    enumParamChoice2.setBlack(diffValue);
    ASSERT_NE(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(63073, enumParamChoice1.hashCode());
    ASSERT_EQ(63074, enumParamChoice2.hashCode());
}

TEST_F(EnumParamChoiceTest, write)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(parameterExpressionsBlack);
    const int8_t valueB = 99;
    enumParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    enumParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    EnumParamChoice readEnumParamChoiceB(readerB, parameterExpressionsBlack);
    ASSERT_EQ(valueB, readEnumParamChoiceB.getBlack());

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(parameterExpressionsGrey);
    const int16_t valueG = 234;
    enumParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG(bitBuffer);
    enumParamChoiceG.write(writerG);

    zserio::BitStreamReader readerG(writerG.getWriteBuffer(), writerG.getBitPosition(), zserio::BitsTag());
    EnumParamChoice readEnumParamChoiceG(readerG, parameterExpressionsGrey);
    ASSERT_EQ(valueG, readEnumParamChoiceG.getGrey());

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(parameterExpressionsWhite);
    const int32_t valueW = 65535;
    enumParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW(bitBuffer);
    enumParamChoiceW.write(writerW);

    zserio::BitStreamReader readerW(writerW.getWriteBuffer(), writerW.getBitPosition(), zserio::BitsTag());
    EnumParamChoice readEnumParamChoiceW(readerW, parameterExpressionsWhite);
    ASSERT_EQ(valueW, readEnumParamChoiceW.getWhite());
}

} // namespace enum_param_choice
} // namespace choice_types
