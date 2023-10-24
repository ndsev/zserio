#include "gtest/gtest.h"

#include "choice_types/full_enum_param_choice/Selector.h"
#include "choice_types/full_enum_param_choice/FullEnumParamChoice.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace choice_types
{
namespace full_enum_param_choice
{

class FullEnumParamChoiceTest : public ::testing::Test
{
protected:
    void writeFullEnumParamChoiceToByteArray(zserio::BitStreamWriter& writer, Selector selector, int32_t value)
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

    FullEnumParamChoice::ParameterExpressions parameterExpressionsBlack = {
            nullptr, 0, [](void*, size_t) { return Selector::BLACK; } };
    FullEnumParamChoice::ParameterExpressions parameterExpressionsGrey = {
            nullptr, 0, [](void*, size_t) { return Selector::GREY; } };
    FullEnumParamChoice::ParameterExpressions parameterExpressionsWhite = {
            nullptr, 0, [](void*, size_t) { return Selector::WHITE; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(FullEnumParamChoiceTest, emptyConstructor)
{
    {
        FullEnumParamChoice fullEnumParamChoice;
        ASSERT_THROW(fullEnumParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        FullEnumParamChoice fullEnumParamChoice = {};
        ASSERT_THROW(fullEnumParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(FullEnumParamChoiceTest, bitStreamReaderConstructor)
{
    const int8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeFullEnumParamChoiceToByteArray(writer, Selector::BLACK, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FullEnumParamChoice fullEnumParamChoice(reader, parameterExpressionsBlack);
    ASSERT_EQ(Selector::BLACK, fullEnumParamChoice.getSelector());
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, copyConstructor)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);

    const FullEnumParamChoice fullEnumParamChoiceCopy(fullEnumParamChoice);
    ASSERT_EQ(Selector::BLACK, fullEnumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceCopy.getBlack());
}

TEST_F(FullEnumParamChoiceTest, assignmentOperator)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);

    FullEnumParamChoice fullEnumParamChoiceCopy;
    fullEnumParamChoiceCopy = fullEnumParamChoice;
    ASSERT_EQ(Selector::GREY, fullEnumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceCopy.getGrey());
}

TEST_F(FullEnumParamChoiceTest, moveConstructor)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const FullEnumParamChoice fullEnumParamChoiceMoved(std::move(fullEnumParamChoice));
    ASSERT_EQ(Selector::BLACK, fullEnumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceMoved.getBlack());
}

TEST_F(FullEnumParamChoiceTest, moveAssignmentOperator)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);

    // note that it doesn't ensure that move ctor was called
    FullEnumParamChoice fullEnumParamChoiceMoved;
    fullEnumParamChoiceMoved = std::move(fullEnumParamChoice);
    ASSERT_EQ(Selector::GREY, fullEnumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceMoved.getGrey());
}

TEST_F(FullEnumParamChoiceTest, propagateAllocatorCopyConstructor)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);

    const FullEnumParamChoice fullEnumParamChoiceCopy(zserio::PropagateAllocator, fullEnumParamChoice,
            FullEnumParamChoice::allocator_type());
    ASSERT_EQ(Selector::BLACK, fullEnumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceCopy.getBlack());
}

TEST_F(FullEnumParamChoiceTest, initialize)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsGrey);
    ASSERT_EQ(Selector::GREY, fullEnumParamChoice.getSelector());
}

TEST_F(FullEnumParamChoiceTest, getSelector)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(Selector::BLACK, fullEnumParamChoice.getSelector());
}

TEST_F(FullEnumParamChoiceTest, getSetBlack)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsBlack);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, getSetGrey)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsGrey);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);
    ASSERT_EQ(value, fullEnumParamChoice.getGrey());
}

TEST_F(FullEnumParamChoiceTest, getSetWhite)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(parameterExpressionsWhite);
    const int32_t value = 65535;
    fullEnumParamChoice.setWhite(value);
    ASSERT_EQ(value, fullEnumParamChoice.getWhite());
}

TEST_F(FullEnumParamChoiceTest, choiceTag)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(FullEnumParamChoice::CHOICE_black, fullEnumParamChoiceB.choiceTag());

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(FullEnumParamChoice::CHOICE_grey, fullEnumParamChoiceG.choiceTag());

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(FullEnumParamChoice::CHOICE_white, fullEnumParamChoiceW.choiceTag());
}

TEST_F(FullEnumParamChoiceTest, bitSizeOf)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(8, fullEnumParamChoiceB.bitSizeOf());

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(16, fullEnumParamChoiceG.bitSizeOf());

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(32, fullEnumParamChoiceW.bitSizeOf());
}

TEST_F(FullEnumParamChoiceTest, initializeOffsets)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(parameterExpressionsBlack);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, fullEnumParamChoiceB.initializeOffsets(bitPosition));

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(parameterExpressionsGrey);
    ASSERT_EQ(17, fullEnumParamChoiceG.initializeOffsets(bitPosition));

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(33, fullEnumParamChoiceW.initializeOffsets(bitPosition));
}

TEST_F(FullEnumParamChoiceTest, operatorEquality)
{
    FullEnumParamChoice fullEnumParamChoice1;
    fullEnumParamChoice1.initialize(parameterExpressionsBlack);
    FullEnumParamChoice fullEnumParamChoice2;
    fullEnumParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_TRUE(fullEnumParamChoice1 == fullEnumParamChoice2);

    const int8_t value = 99;
    fullEnumParamChoice1.setBlack(value);
    ASSERT_FALSE(fullEnumParamChoice1 == fullEnumParamChoice2);

    fullEnumParamChoice2.setBlack(value);
    ASSERT_TRUE(fullEnumParamChoice1 == fullEnumParamChoice2);

    const int8_t diffValue = value + 1;
    fullEnumParamChoice2.setBlack(diffValue);
    ASSERT_FALSE(fullEnumParamChoice1 == fullEnumParamChoice2);
}

TEST_F(FullEnumParamChoiceTest, hashCode)
{
    FullEnumParamChoice fullEnumParamChoice1;
    fullEnumParamChoice1.initialize(parameterExpressionsBlack);
    FullEnumParamChoice fullEnumParamChoice2;
    fullEnumParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_EQ(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

    const int8_t value = 99;
    fullEnumParamChoice1.setBlack(value);
    ASSERT_NE(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

    fullEnumParamChoice2.setBlack(value);
    ASSERT_EQ(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    fullEnumParamChoice2.setBlack(diffValue);
    ASSERT_NE(fullEnumParamChoice1.hashCode(), fullEnumParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(63073, fullEnumParamChoice1.hashCode());
    ASSERT_EQ(63074, fullEnumParamChoice2.hashCode());
}

TEST_F(FullEnumParamChoiceTest, write)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(parameterExpressionsBlack);
    const int8_t valueB = 99;
    fullEnumParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    fullEnumParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    FullEnumParamChoice readEnumParamChoiceB(readerB, parameterExpressionsBlack);
    ASSERT_EQ(valueB, readEnumParamChoiceB.getBlack());

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(parameterExpressionsGrey);
    const int16_t valueG = 234;
    fullEnumParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG(bitBuffer);
    fullEnumParamChoiceG.write(writerG);

    zserio::BitStreamReader readerG(writerG.getWriteBuffer(), writerG.getBitPosition(), zserio::BitsTag());
    FullEnumParamChoice readEnumParamChoiceG(readerG, parameterExpressionsGrey);
    ASSERT_EQ(valueG, readEnumParamChoiceG.getGrey());

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(parameterExpressionsWhite);
    const int32_t valueW = 65535;
    fullEnumParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW(bitBuffer);
    fullEnumParamChoiceW.write(writerW);

    zserio::BitStreamReader readerW(writerW.getWriteBuffer(), writerW.getBitPosition(), zserio::BitsTag());
    FullEnumParamChoice readEnumParamChoiceW(readerW, parameterExpressionsWhite);
    ASSERT_EQ(valueW, readEnumParamChoiceW.getWhite());
}

} // namespace full_enum_param_choice
} // namespace choice_types
