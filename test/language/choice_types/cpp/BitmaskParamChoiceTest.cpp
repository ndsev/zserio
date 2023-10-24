#include "gtest/gtest.h"

#include "choice_types/bitmask_param_choice/Selector.h"
#include "choice_types/bitmask_param_choice/BitmaskParamChoice.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace choice_types
{
namespace bitmask_param_choice
{

class BitmaskParamChoiceTest : public ::testing::Test
{
protected:
    void writeBitmaskParamChoiceToByteArray(zserio::BitStreamWriter& writer, Selector selector, uint16_t value)
    {
        switch (selector.getValue())
        {
        case static_cast<Selector::underlying_type>(Selector::Values::BLACK):
        case static_cast<Selector::underlying_type>(Selector::Values::WHITE):
            writer.writeBits(value, 8);
            break;

        case static_cast<Selector::underlying_type>(Selector::Values::BLACK_AND_WHITE):
            writer.writeBits(value, 16);
            break;

        default:
            FAIL() << "Bad choice selector";
        }
    }

    BitmaskParamChoice::ParameterExpressions parameterExpressionsBlack = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::BLACK); } };
    BitmaskParamChoice::ParameterExpressions parameterExpressionsWhite = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::WHITE); } };
    BitmaskParamChoice::ParameterExpressions parameterExpressionsBlackAndWhite = {
            nullptr, 0, [](void*, size_t) { return Selector(Selector::Values::BLACK_AND_WHITE); } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(BitmaskParamChoiceTest, emptyConstructor)
{
    {
        BitmaskParamChoice bitmaskParamChoice;
        ASSERT_THROW(bitmaskParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        BitmaskParamChoice bitmaskParamChoice = {};
        ASSERT_THROW(bitmaskParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(BitmaskParamChoiceTest, bitStreamReaderConstructor)
{
    const uint8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBitmaskParamChoiceToByteArray(writer, Selector::Values::BLACK, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice bitmaskParamChoice(reader, parameterExpressionsBlack);
    ASSERT_EQ(Selector::Values::BLACK, bitmaskParamChoice.getSelector());
    ASSERT_EQ(value, bitmaskParamChoice.getBlack());
}

TEST_F(BitmaskParamChoiceTest, copyConstructor)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    const BitmaskParamChoice bitmaskParamChoiceCopy(bitmaskParamChoice);
    ASSERT_EQ(Selector::Values::BLACK, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getBlack());
}

TEST_F(BitmaskParamChoiceTest, assignmentOperator)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);

    BitmaskParamChoice bitmaskParamChoiceCopy;
    bitmaskParamChoiceCopy = bitmaskParamChoice;
    ASSERT_EQ(Selector::Values::WHITE, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getWhite());
}

TEST_F(BitmaskParamChoiceTest, moveCopyConstructor)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const BitmaskParamChoice bitmaskParamChoiceMoved(std::move(bitmaskParamChoice));
    ASSERT_EQ(Selector::Values::BLACK, bitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceMoved.getBlack());
}

TEST_F(BitmaskParamChoiceTest, moveAssignmentOperator)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);

    // note that it doesn't ensure that move ctor was called
    BitmaskParamChoice bitmaskParamChoiceMoved;
    bitmaskParamChoiceMoved = std::move(bitmaskParamChoice);
    ASSERT_EQ(Selector::Values::WHITE, bitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceMoved.getWhite());
}

TEST_F(BitmaskParamChoiceTest, propagateAllocatorCopyConstructor)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    const BitmaskParamChoice bitmaskParamChoiceCopy(zserio::PropagateAllocator, bitmaskParamChoice,
            BitmaskParamChoice::allocator_type());
    ASSERT_EQ(Selector::Values::BLACK, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getBlack());
}

TEST_F(BitmaskParamChoiceTest, initialize)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsWhite);
    ASSERT_EQ(Selector::Values::WHITE, bitmaskParamChoice.getSelector());
}

TEST_F(BitmaskParamChoiceTest, getSelector)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(Selector::Values::BLACK, bitmaskParamChoice.getSelector());
}

TEST_F(BitmaskParamChoiceTest, getSetBlack)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);
    ASSERT_EQ(value, bitmaskParamChoice.getBlack());
}

TEST_F(BitmaskParamChoiceTest, getSetWhite)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsWhite);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);
    ASSERT_EQ(value, bitmaskParamChoice.getWhite());
}

TEST_F(BitmaskParamChoiceTest, getSetBlackAndWhite)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlackAndWhite);
    const uint16_t value = 65535;
    bitmaskParamChoice.setBlackAndWhite(value);
    ASSERT_EQ(value, bitmaskParamChoice.getBlackAndWhite());
}

TEST_F(BitmaskParamChoiceTest, choiceTag)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(parameterExpressionsBlack);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_black, bitmaskParamChoice.choiceTag());

    bitmaskParamChoice.initialize(parameterExpressionsWhite);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_white, bitmaskParamChoice.choiceTag());

    bitmaskParamChoice.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_blackAndWhite, bitmaskParamChoice.choiceTag());
}

TEST_F(BitmaskParamChoiceTest, bitSizeOf)
{
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    ASSERT_EQ(8, bitmaskParamChoiceB.bitSizeOf());

    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(8, bitmaskParamChoiceW.bitSizeOf());

    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(16, bitmaskParamChoiceBW.bitSizeOf());
}

TEST_F(BitmaskParamChoiceTest, initializeOffsets)
{
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, bitmaskParamChoiceB.initializeOffsets(bitPosition));

    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    ASSERT_EQ(9, bitmaskParamChoiceW.initializeOffsets(bitPosition));

    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    ASSERT_EQ(17, bitmaskParamChoiceBW.initializeOffsets(bitPosition));
}

TEST_F(BitmaskParamChoiceTest, operatorEquality)
{
    BitmaskParamChoice bitmaskParamChoice1;
    bitmaskParamChoice1.initialize(parameterExpressionsBlack);
    BitmaskParamChoice bitmaskParamChoice2;
    bitmaskParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_TRUE(bitmaskParamChoice1 == bitmaskParamChoice2);

    const uint8_t value = 99;
    bitmaskParamChoice1.setBlack(value);
    ASSERT_FALSE(bitmaskParamChoice1 == bitmaskParamChoice2);

    bitmaskParamChoice2.setBlack(value);
    ASSERT_TRUE(bitmaskParamChoice1 == bitmaskParamChoice2);

    const int8_t diffValue = value + 1;
    bitmaskParamChoice2.setBlack(diffValue);
    ASSERT_FALSE(bitmaskParamChoice1 == bitmaskParamChoice2);
}

TEST_F(BitmaskParamChoiceTest, hashCode)
{
    BitmaskParamChoice bitmaskParamChoice1;
    bitmaskParamChoice1.initialize(parameterExpressionsBlack);
    BitmaskParamChoice bitmaskParamChoice2;
    bitmaskParamChoice2.initialize(parameterExpressionsBlack);
    ASSERT_EQ(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    const uint8_t value = 99;
    bitmaskParamChoice1.setBlack(value);
    ASSERT_NE(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    bitmaskParamChoice2.setBlack(value);
    ASSERT_EQ(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    bitmaskParamChoice2.setBlack(diffValue);
    ASSERT_NE(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(63110, bitmaskParamChoice1.hashCode());
    ASSERT_EQ(63111, bitmaskParamChoice2.hashCode());
}

TEST_F(BitmaskParamChoiceTest, write)
{
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(parameterExpressionsBlack);
    const uint8_t valueB = 99;
    bitmaskParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    bitmaskParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceB(readerB, parameterExpressionsBlack);
    ASSERT_EQ(valueB, readBitmaskParamChoiceB.getBlack());

    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(parameterExpressionsWhite);
    const uint8_t valueW = 234;
    bitmaskParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW(bitBuffer);
    bitmaskParamChoiceW.write(writerW);

    zserio::BitStreamReader readerW(writerW.getWriteBuffer(), writerW.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceW(readerW, parameterExpressionsWhite);
    ASSERT_EQ(valueW, readBitmaskParamChoiceW.getWhite());

    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(parameterExpressionsBlackAndWhite);
    const uint16_t valueBW = 65535;
    bitmaskParamChoiceBW.setBlackAndWhite(valueBW);
    zserio::BitStreamWriter writerBW(bitBuffer);
    bitmaskParamChoiceBW.write(writerBW);

    zserio::BitStreamReader readerBW(writerBW.getWriteBuffer(), writerBW.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceBW(readerBW, parameterExpressionsBlackAndWhite);
    ASSERT_EQ(valueBW, readBitmaskParamChoiceBW.getBlackAndWhite());
}

} // namespace bitmask_param_choice
} // namespace choice_types
