#include "gtest/gtest.h"

#include "choice_types/full_bitmask_param_choice/Selector.h"
#include "choice_types/full_bitmask_param_choice/FullBitmaskParamChoice.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace choice_types
{
namespace full_bitmask_param_choice
{

class FullBitmaskParamChoiceTest : public ::testing::Test
{
protected:
    void writeFullBitmaskParamChoiceToByteArray(zserio::BitStreamWriter& writer,
            Selector selector, uint16_t value)
    {
        switch (selector.getValue())
        {
        case static_cast<Selector::underlying_type>(Selector::Values::BLACK):
        case static_cast<Selector::underlying_type>(Selector::Values::WHITE):
            writer.writeSignedBits(value, 8);
            break;

        case static_cast<Selector::underlying_type>(Selector::Values::BLACK_AND_WHITE):
            writer.writeSignedBits(value, 16);
            break;

        default:
            FAIL() << "Bad choice selector";
        }
    }

    FullBitmaskParamChoice::ParameterExpressions parameterExpressionsBlack = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::BLACK); } };
    FullBitmaskParamChoice::ParameterExpressions parameterExpressionsWhite = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::WHITE); } };
    FullBitmaskParamChoice::ParameterExpressions parameterExpressionsBlackAndWhite = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::BLACK_AND_WHITE); } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(FullBitmaskParamChoiceTest, emptyConstructor)
{
    {
        FullBitmaskParamChoice fullBitmaskParamChoice;
        ASSERT_THROW(fullBitmaskParamChoice.getSelector(), zserio::CppRuntimeException);
    }

    {
        FullBitmaskParamChoice fullBitmaskParamChoice = {};
        ASSERT_THROW(fullBitmaskParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(FullBitmaskParamChoiceTest, bitStreamReaderConstructor)
{
    const uint8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeFullBitmaskParamChoiceToByteArray(writer, Selector::Values::BLACK, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FullBitmaskParamChoice fullBitmaskParamChoice(reader, parameterExpressionsBlack);
    ASSERT_EQ(Selector::Values::BLACK, fullBitmaskParamChoice.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoice.getBlack());
}

TEST_F(FullBitmaskParamChoiceTest, copyConstructor)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    fullBitmaskParamChoice.setBlack(value);

    const FullBitmaskParamChoice fullBitmaskParamChoiceCopy(fullBitmaskParamChoice);
    ASSERT_EQ(Selector::Values::BLACK, fullBitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoiceCopy.getBlack());
}

TEST_F(FullBitmaskParamChoiceTest, assignmentOperator)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    fullBitmaskParamChoice.setWhite(value);

    FullBitmaskParamChoice fullBitmaskParamChoiceCopy;
    fullBitmaskParamChoiceCopy = fullBitmaskParamChoice;
    ASSERT_EQ(Selector::Values::WHITE, fullBitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoiceCopy.getWhite());
}

TEST_F(FullBitmaskParamChoiceTest, moveCopyConstructor)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    fullBitmaskParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const FullBitmaskParamChoice fullBitmaskParamChoiceMoved(std::move(fullBitmaskParamChoice));
    ASSERT_EQ(Selector::Values::BLACK, fullBitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoiceMoved.getBlack());
}

TEST_F(FullBitmaskParamChoiceTest, moveAssignmentOperator)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    fullBitmaskParamChoice.setWhite(value);

    // note that it doesn't ensure that move ctor was called
    FullBitmaskParamChoice fullBitmaskParamChoiceMoved;
    fullBitmaskParamChoiceMoved = std::move(fullBitmaskParamChoice);
    ASSERT_EQ(Selector::Values::WHITE, fullBitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoiceMoved.getWhite());
}

TEST_F(FullBitmaskParamChoiceTest, propagateAllocatorCopyConstructor)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    fullBitmaskParamChoice.setBlack(value);

    const FullBitmaskParamChoice fullBitmaskParamChoiceCopy(zserio::PropagateAllocator, fullBitmaskParamChoice,
            FullBitmaskParamChoice::allocator_type());
    ASSERT_EQ(Selector::Values::BLACK, fullBitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullBitmaskParamChoiceCopy.getBlack());
}

TEST_F(FullBitmaskParamChoiceTest, initialize)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsWhite);
    ASSERT_EQ(Selector::Values::WHITE, fullBitmaskParamChoice.getSelector());
}

TEST_F(FullBitmaskParamChoiceTest, getSelector)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(Selector::Values::BLACK, fullBitmaskParamChoice.getSelector());
}

TEST_F(FullBitmaskParamChoiceTest, getSetBlack)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    fullBitmaskParamChoice.setBlack(value);
    ASSERT_EQ(value, fullBitmaskParamChoice.getBlack());
}

TEST_F(FullBitmaskParamChoiceTest, getSetWhite)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    fullBitmaskParamChoice.setWhite(value);
    ASSERT_EQ(value, fullBitmaskParamChoice.getWhite());
}

TEST_F(FullBitmaskParamChoiceTest, getSetBlackAndWhite)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlackAndWhite);
    const uint16_t value = 65535;
    fullBitmaskParamChoice.setBlackAndWhite(value);
    ASSERT_EQ(value, fullBitmaskParamChoice.getBlackAndWhite());
}

TEST_F(FullBitmaskParamChoiceTest, choiceTag)
{
    FullBitmaskParamChoice fullBitmaskParamChoice;
    fullBitmaskParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(FullBitmaskParamChoice::CHOICE_black, fullBitmaskParamChoice.choiceTag());

    fullBitmaskParamChoice.initialize(parameterExpressionsWhite);
    ASSERT_EQ(FullBitmaskParamChoice::CHOICE_white, fullBitmaskParamChoice.choiceTag());

    fullBitmaskParamChoice.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(FullBitmaskParamChoice::CHOICE_blackAndWhite, fullBitmaskParamChoice.choiceTag());
}

TEST_F(FullBitmaskParamChoiceTest, bitSizeOf)
{
    FullBitmaskParamChoice fullBitmaskParamChoiceB;
    fullBitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(8, fullBitmaskParamChoiceB.bitSizeOf());

    FullBitmaskParamChoice fullBitmaskParamChoiceW;
    fullBitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(8, fullBitmaskParamChoiceW.bitSizeOf());

    FullBitmaskParamChoice fullBitmaskParamChoiceBW;
    fullBitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(16, fullBitmaskParamChoiceBW.bitSizeOf());
}

TEST_F(FullBitmaskParamChoiceTest, initializeOffsets)
{
    FullBitmaskParamChoice fullBitmaskParamChoiceB;
    fullBitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, fullBitmaskParamChoiceB.initializeOffsets(bitPosition));

    FullBitmaskParamChoice fullBitmaskParamChoiceW;
    fullBitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(9, fullBitmaskParamChoiceW.initializeOffsets(bitPosition));

    FullBitmaskParamChoice fullBitmaskParamChoiceBW;
    fullBitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(17, fullBitmaskParamChoiceBW.initializeOffsets(bitPosition));
}

TEST_F(FullBitmaskParamChoiceTest, operatorEquality)
{
    FullBitmaskParamChoice fullBitmaskParamChoice1;
    fullBitmaskParamChoice1.initialize(parameterExpressionsBlack);
    FullBitmaskParamChoice fullBitmaskParamChoice2;
    fullBitmaskParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_TRUE(fullBitmaskParamChoice1 == fullBitmaskParamChoice2);

    const uint8_t value = 99;
    fullBitmaskParamChoice1.setBlack(value);
    ASSERT_FALSE(fullBitmaskParamChoice1 == fullBitmaskParamChoice2);

    fullBitmaskParamChoice2.setBlack(value);
    ASSERT_TRUE(fullBitmaskParamChoice1 == fullBitmaskParamChoice2);

    const int8_t diffValue = value + 1;
    fullBitmaskParamChoice2.setBlack(diffValue);
    ASSERT_FALSE(fullBitmaskParamChoice1 == fullBitmaskParamChoice2);
}

TEST_F(FullBitmaskParamChoiceTest, hashCode)
{
    FullBitmaskParamChoice fullBitmaskParamChoice1;
    fullBitmaskParamChoice1.initialize(parameterExpressionsBlack);
    FullBitmaskParamChoice fullBitmaskParamChoice2;
    fullBitmaskParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_EQ(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

    const uint8_t value = 99;
    fullBitmaskParamChoice1.setBlack(value);
    ASSERT_NE(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

    fullBitmaskParamChoice2.setBlack(value);
    ASSERT_EQ(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    fullBitmaskParamChoice2.setBlack(diffValue);
    ASSERT_NE(fullBitmaskParamChoice1.hashCode(), fullBitmaskParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(63110, fullBitmaskParamChoice1.hashCode());
    ASSERT_EQ(63111, fullBitmaskParamChoice2.hashCode());
}

TEST_F(FullBitmaskParamChoiceTest, write)
{
    FullBitmaskParamChoice fullBitmaskParamChoiceB;
    fullBitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    const uint8_t valueB = 99;
    fullBitmaskParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    fullBitmaskParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    FullBitmaskParamChoice readFullBitmaskParamChoiceB(readerB, parameterExpressionsBlack);
    ASSERT_EQ(valueB, readFullBitmaskParamChoiceB.getBlack());

    FullBitmaskParamChoice fullBitmaskParamChoiceW;
    fullBitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    const uint8_t valueW = 234;
    fullBitmaskParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW(bitBuffer);
    fullBitmaskParamChoiceW.write(writerW);

    zserio::BitStreamReader readerW(writerW.getWriteBuffer(), writerW.getBitPosition(), zserio::BitsTag());
    FullBitmaskParamChoice readFullBitmaskParamChoiceW(readerW, parameterExpressionsWhite);
    ASSERT_EQ(valueW, readFullBitmaskParamChoiceW.getWhite());

    FullBitmaskParamChoice fullBitmaskParamChoiceBW;
    fullBitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    const uint16_t valueBW = 65535;
    fullBitmaskParamChoiceBW.setBlackAndWhite(valueBW);
    zserio::BitStreamWriter writerBW(bitBuffer);
    fullBitmaskParamChoiceBW.write(writerBW);

    zserio::BitStreamReader readerBW(writerBW.getWriteBuffer(), writerBW.getBitPosition(), zserio::BitsTag());
    FullBitmaskParamChoice readFullBitmaskParamChoiceBW(readerBW, parameterExpressionsBlackAndWhite);
    ASSERT_EQ(valueBW, readFullBitmaskParamChoiceBW.getBlackAndWhite());
}

} // namespace full_bitmask_param_choice
} // namespace choice_types
