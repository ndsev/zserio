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
            break;
        }
    }
};

TEST_F(FullEnumParamChoiceTest, emptyConstructor)
{
    FullEnumParamChoice fullEnumParamChoice;
    ASSERT_THROW(fullEnumParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(FullEnumParamChoiceTest, bitStreamReaderConstructor)
{
    const Selector selector = Selector::BLACK;
    const int8_t value = 99;
    zserio::BitStreamWriter writer;
    writeFullEnumParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    FullEnumParamChoice fullEnumParamChoice(reader, selector);
    ASSERT_EQ(selector, fullEnumParamChoice.getSelector());
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, fieldConstructor)
{
    const Selector selector = Selector::BLACK;
    const int8_t value = 99;
    FullEnumParamChoice fullEnumParamChoice(selector, value);
    ASSERT_EQ(selector, fullEnumParamChoice.getSelector());
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, copyConstructor)
{
    const Selector selector = Selector::BLACK;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);

    const FullEnumParamChoice fullEnumParamChoiceCopy(fullEnumParamChoice);
    ASSERT_EQ(selector, fullEnumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceCopy.getBlack());
}

TEST_F(FullEnumParamChoiceTest, assignmentOperator)
{
    const Selector selector = Selector::GREY;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);

    FullEnumParamChoice fullEnumParamChoiceCopy;
    fullEnumParamChoiceCopy = fullEnumParamChoice;
    ASSERT_EQ(selector, fullEnumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceCopy.getGrey());
}

TEST_F(FullEnumParamChoiceTest, moveConstructor)
{
    const Selector selector = Selector::BLACK;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);

    // note that it doesn't ensure that move ctor was called
    const FullEnumParamChoice fullEnumParamChoiceMoved(std::move(fullEnumParamChoice));
    ASSERT_EQ(selector, fullEnumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceMoved.getBlack());
}

TEST_F(FullEnumParamChoiceTest, moveAssignmentOperator)
{
    const Selector selector = Selector::GREY;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);

    // note that it doesn't ensure that move ctor was called
    FullEnumParamChoice fullEnumParamChoiceMoved;
    fullEnumParamChoiceMoved = std::move(fullEnumParamChoice);
    ASSERT_EQ(selector, fullEnumParamChoiceMoved.getSelector());
    ASSERT_EQ(value, fullEnumParamChoiceMoved.getGrey());
}

TEST_F(FullEnumParamChoiceTest, initialize)
{
    const Selector selector = Selector::GREY;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    ASSERT_EQ(selector, fullEnumParamChoice.getSelector());
}

TEST_F(FullEnumParamChoiceTest, getSelector)
{
    const Selector selector = Selector::BLACK;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);
    ASSERT_EQ(selector, fullEnumParamChoice.getSelector());
}

TEST_F(FullEnumParamChoiceTest, getSetBlack)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(Selector::BLACK);
    const int8_t value = 99;
    fullEnumParamChoice.setBlack(value);
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, getSetGrey)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(Selector::GREY);
    const int16_t value = 234;
    fullEnumParamChoice.setGrey(value);
    ASSERT_EQ(value, fullEnumParamChoice.getGrey());
}

TEST_F(FullEnumParamChoiceTest, getSetWhite)
{
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(Selector::WHITE);
    const int32_t value = 65535;
    fullEnumParamChoice.setWhite(value);
    ASSERT_EQ(value, fullEnumParamChoice.getWhite());
}

TEST_F(FullEnumParamChoiceTest, bitSizeOf)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(Selector::BLACK);
    ASSERT_EQ(8, fullEnumParamChoiceB.bitSizeOf());

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(Selector::GREY);
    ASSERT_EQ(16, fullEnumParamChoiceG.bitSizeOf());

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(Selector::WHITE);
    ASSERT_EQ(32, fullEnumParamChoiceW.bitSizeOf());
}

TEST_F(FullEnumParamChoiceTest, initializeOffsets)
{
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(Selector::BLACK);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, fullEnumParamChoiceB.initializeOffsets(bitPosition));

    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(Selector::GREY);
    ASSERT_EQ(17, fullEnumParamChoiceG.initializeOffsets(bitPosition));

    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(Selector::WHITE);
    ASSERT_EQ(33, fullEnumParamChoiceW.initializeOffsets(bitPosition));
}

TEST_F(FullEnumParamChoiceTest, operatorEquality)
{
    FullEnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(Selector::BLACK);
    FullEnumParamChoice enumParamChoice2;
    enumParamChoice2.initialize(Selector::BLACK);
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

TEST_F(FullEnumParamChoiceTest, hashCode)
{
    FullEnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(Selector::BLACK);
    FullEnumParamChoice enumParamChoice2;
    enumParamChoice2.initialize(Selector::BLACK);
    ASSERT_EQ(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    const int8_t value = 99;
    enumParamChoice1.setBlack(value);
    ASSERT_NE(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    enumParamChoice2.setBlack(value);
    ASSERT_EQ(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    enumParamChoice2.setBlack(diffValue);
    ASSERT_NE(enumParamChoice1.hashCode(), enumParamChoice2.hashCode());
}

TEST_F(FullEnumParamChoiceTest, read)
{
    const Selector selector = Selector::BLACK;
    FullEnumParamChoice fullEnumParamChoice;
    fullEnumParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeFullEnumParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    fullEnumParamChoice.read(reader);

    ASSERT_EQ(selector, fullEnumParamChoice.getSelector());
    ASSERT_EQ(value, fullEnumParamChoice.getBlack());
}

TEST_F(FullEnumParamChoiceTest, write)
{
    Selector selector = Selector::BLACK;
    FullEnumParamChoice fullEnumParamChoiceB;
    fullEnumParamChoiceB.initialize(selector);
    const int8_t valueB = 99;
    fullEnumParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB;
    fullEnumParamChoiceB.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    FullEnumParamChoice readEnumParamChoiceB(readerB, selector);
    ASSERT_EQ(valueB, readEnumParamChoiceB.getBlack());

    selector = Selector::GREY;
    FullEnumParamChoice fullEnumParamChoiceG;
    fullEnumParamChoiceG.initialize(selector);
    const int16_t valueG = 234;
    fullEnumParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG;
    fullEnumParamChoiceG.write(writerG);
    size_t writeBufferByteSizeG;
    const uint8_t* writeBufferG = writerG.getWriteBuffer(writeBufferByteSizeG);
    zserio::BitStreamReader readerG(writeBufferG, writeBufferByteSizeG);
    FullEnumParamChoice readEnumParamChoiceG(readerG, selector);
    ASSERT_EQ(valueG, readEnumParamChoiceG.getGrey());

    selector = Selector::WHITE;
    FullEnumParamChoice fullEnumParamChoiceW;
    fullEnumParamChoiceW.initialize(selector);
    const int32_t valueW = 65535;
    fullEnumParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW;
    fullEnumParamChoiceW.write(writerW);
    size_t writeBufferByteSizeW;
    const uint8_t* writeBufferW = writerW.getWriteBuffer(writeBufferByteSizeW);
    zserio::BitStreamReader readerW(writeBufferW, writeBufferByteSizeW);
    FullEnumParamChoice readEnumParamChoiceW(readerW, selector);
    ASSERT_EQ(valueW, readEnumParamChoiceW.getWhite());
}

} // namespace full_enum_param_choice
} // namespace choice_types
