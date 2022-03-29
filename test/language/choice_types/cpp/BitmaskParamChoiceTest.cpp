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
            writer.writeBits(value, 8);
            break;

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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(BitmaskParamChoiceTest, emptyConstructor)
{
    BitmaskParamChoice bitmaskParamChoice;
    ASSERT_THROW(bitmaskParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(BitmaskParamChoiceTest, bitStreamReaderConstructor)
{
    const Selector selector = Selector::Values::BLACK;
    const uint8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeBitmaskParamChoiceToByteArray(writer, selector, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice bitmaskParamChoice(reader, selector);
    ASSERT_EQ(selector, bitmaskParamChoice.getSelector());
    ASSERT_EQ(value, bitmaskParamChoice.getBlack());
}

TEST_F(BitmaskParamChoiceTest, copyConstructor)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    const BitmaskParamChoice bitmaskParamChoiceCopy(bitmaskParamChoice);
    ASSERT_EQ(selector, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getBlack());
}

TEST_F(BitmaskParamChoiceTest, assignmentOperator)
{
    const Selector selector = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);

    BitmaskParamChoice bitmaskParamChoiceCopy;
    bitmaskParamChoiceCopy = bitmaskParamChoice;
    ASSERT_EQ(selector, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getWhite());
}

TEST_F(BitmaskParamChoiceTest, moveCopyConstructor)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const BitmaskParamChoice bitmaskParamChoiceMoved(std::move(bitmaskParamChoice));
    ASSERT_EQ(selector, bitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceMoved.getBlack());
}

TEST_F(BitmaskParamChoiceTest, moveAssignmentOperator)
{
    const Selector selector = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);

    // note that it doesn't ensure that move ctor was called
    BitmaskParamChoice bitmaskParamChoiceMoved;
    bitmaskParamChoiceMoved = std::move(bitmaskParamChoice);
    ASSERT_EQ(selector, bitmaskParamChoiceMoved.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceMoved.getWhite());
}

TEST_F(BitmaskParamChoiceTest, propagateAllocatorCopyConstructor)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);

    const BitmaskParamChoice bitmaskParamChoiceCopy(zserio::PropagateAllocator, bitmaskParamChoice,
            BitmaskParamChoice::allocator_type());
    ASSERT_EQ(selector, bitmaskParamChoiceCopy.getSelector());
    ASSERT_EQ(value, bitmaskParamChoiceCopy.getBlack());
}

TEST_F(BitmaskParamChoiceTest, initialize)
{
    const Selector selector = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    ASSERT_EQ(selector, bitmaskParamChoice.getSelector());
}

TEST_F(BitmaskParamChoiceTest, getSelector)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    ASSERT_EQ(selector, bitmaskParamChoice.getSelector());
}

TEST_F(BitmaskParamChoiceTest, getSetBlack)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 99;
    bitmaskParamChoice.setBlack(value);
    ASSERT_EQ(value, bitmaskParamChoice.getBlack());
}

TEST_F(BitmaskParamChoiceTest, getSetWhite)
{
    const Selector selector = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint8_t value = 234;
    bitmaskParamChoice.setWhite(value);
    ASSERT_EQ(value, bitmaskParamChoice.getWhite());
}

TEST_F(BitmaskParamChoiceTest, getSetBlackAndWhite)
{
    const Selector selector = Selector::Values::BLACK_AND_WHITE;
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(selector);
    const uint16_t value = 65535;
    bitmaskParamChoice.setBlackAndWhite(value);
    ASSERT_EQ(value, bitmaskParamChoice.getBlackAndWhite());
}

TEST_F(BitmaskParamChoiceTest, choiceTag)
{
    BitmaskParamChoice bitmaskParamChoice;
    bitmaskParamChoice.initialize(Selector::Values::BLACK);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_black, bitmaskParamChoice.choiceTag());

    bitmaskParamChoice.initialize(Selector::Values::WHITE);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_white, bitmaskParamChoice.choiceTag());

    bitmaskParamChoice.initialize(Selector::Values::BLACK_AND_WHITE);
    ASSERT_EQ(BitmaskParamChoice::CHOICE_blackAndWhite, bitmaskParamChoice.choiceTag());
}

TEST_F(BitmaskParamChoiceTest, bitSizeOf)
{
    Selector selectorB = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(selectorB);
    ASSERT_EQ(8, bitmaskParamChoiceB.bitSizeOf());

    Selector selectorW = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(selectorW);
    ASSERT_EQ(8, bitmaskParamChoiceW.bitSizeOf());

    Selector selectorBW = Selector::Values::BLACK | Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(selectorBW);
    ASSERT_EQ(16, bitmaskParamChoiceBW.bitSizeOf());
}

TEST_F(BitmaskParamChoiceTest, initializeOffsets)
{
    Selector selectorB = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(selectorB);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, bitmaskParamChoiceB.initializeOffsets(bitPosition));

    Selector selectorW = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(selectorW);
    ASSERT_EQ(9, bitmaskParamChoiceW.initializeOffsets(bitPosition));

    Selector selectorBW = Selector::Values::BLACK_AND_WHITE;
    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(selectorBW);
    ASSERT_EQ(17, bitmaskParamChoiceBW.initializeOffsets(bitPosition));
}

TEST_F(BitmaskParamChoiceTest, operatorEquality)
{
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice1;
    bitmaskParamChoice1.initialize(selector);
    BitmaskParamChoice bitmaskParamChoice2;
    bitmaskParamChoice2.initialize(selector);
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
    const Selector selector = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoice1;
    bitmaskParamChoice1.initialize(selector);
    BitmaskParamChoice bitmaskParamChoice2;
    bitmaskParamChoice2.initialize(selector);
    ASSERT_EQ(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    const uint8_t value = 99;
    bitmaskParamChoice1.setBlack(value);
    ASSERT_NE(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    bitmaskParamChoice2.setBlack(value);
    ASSERT_EQ(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    bitmaskParamChoice2.setBlack(diffValue);
    ASSERT_NE(bitmaskParamChoice1.hashCode(), bitmaskParamChoice2.hashCode());
}

TEST_F(BitmaskParamChoiceTest, write)
{
    const Selector selectorB = Selector::Values::BLACK;
    BitmaskParamChoice bitmaskParamChoiceB;
    bitmaskParamChoiceB.initialize(selectorB);
    const uint8_t valueB = 99;
    bitmaskParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    bitmaskParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceB(readerB, selectorB);
    ASSERT_EQ(valueB, readBitmaskParamChoiceB.getBlack());

    const Selector selectorW = Selector::Values::WHITE;
    BitmaskParamChoice bitmaskParamChoiceW;
    bitmaskParamChoiceW.initialize(selectorW);
    const uint8_t valueW = 234;
    bitmaskParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW(bitBuffer);
    bitmaskParamChoiceW.write(writerW);

    zserio::BitStreamReader readerW(writerW.getWriteBuffer(), writerW.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceW(readerW, selectorW);
    ASSERT_EQ(valueW, readBitmaskParamChoiceW.getWhite());

    const Selector selectorBW = Selector::Values::BLACK_AND_WHITE;
    BitmaskParamChoice bitmaskParamChoiceBW;
    bitmaskParamChoiceBW.initialize(selectorBW);
    const uint16_t valueBW = 65535;
    bitmaskParamChoiceBW.setBlackAndWhite(valueBW);
    zserio::BitStreamWriter writerBW(bitBuffer);
    bitmaskParamChoiceBW.write(writerBW);

    zserio::BitStreamReader readerBW(writerBW.getWriteBuffer(), writerBW.getBitPosition(), zserio::BitsTag());
    BitmaskParamChoice readBitmaskParamChoiceBW(readerBW, selectorBW);
    ASSERT_EQ(valueBW, readBitmaskParamChoiceBW.getBlackAndWhite());
}

} // namespace bitmask_param_choice
} // namespace choice_types
