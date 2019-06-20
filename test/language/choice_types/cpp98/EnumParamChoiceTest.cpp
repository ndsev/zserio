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
        switch (selector.getValue())
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

TEST_F(EnumParamChoiceTest, emptyConstructor)
{
    EnumParamChoice enumParamChoice;
    ASSERT_THROW(enumParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(EnumParamChoiceTest, bitStreamReaderConstructor)
{
    const Selector selector = Selector::BLACK;
    const int8_t value = 99;
    zserio::BitStreamWriter writer;
    writeEnumParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    EnumParamChoice enumParamChoice(reader, selector);
    ASSERT_EQ(selector, enumParamChoice.getSelector());
    ASSERT_EQ(value, enumParamChoice.getBlack());
}

TEST_F(EnumParamChoiceTest, copyConstructor)
{
    const Selector selector = Selector::BLACK;
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(selector);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);

    const EnumParamChoice enumParamChoiceCopy(enumParamChoice);
    ASSERT_EQ(selector, enumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, enumParamChoiceCopy.getBlack());
}

TEST_F(EnumParamChoiceTest, operatorAssignment)
{
    const Selector selector = Selector::GREY;
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(selector);
    const int16_t value = 234;
    enumParamChoice.setGrey(value);

    const EnumParamChoice enumParamChoiceCopy = enumParamChoice;
    ASSERT_EQ(selector, enumParamChoiceCopy.getSelector());
    ASSERT_EQ(value, enumParamChoiceCopy.getGrey());
}

TEST_F(EnumParamChoiceTest, initialize)
{
    const Selector selector = Selector::GREY;
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(selector);
    ASSERT_EQ(selector, enumParamChoice.getSelector());
}

TEST_F(EnumParamChoiceTest, getSelector)
{
    const Selector selector = Selector::BLACK;
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(selector);
    ASSERT_EQ(selector, enumParamChoice.getSelector());
}

TEST_F(EnumParamChoiceTest, getSetBlack)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(Selector::BLACK);
    const int8_t value = 99;
    enumParamChoice.setBlack(value);
    ASSERT_EQ(value, enumParamChoice.getBlack());
}

TEST_F(EnumParamChoiceTest, getSetGrey)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(Selector::GREY);
    const int16_t value = 234;
    enumParamChoice.setGrey(value);
    ASSERT_EQ(value, enumParamChoice.getGrey());
}

TEST_F(EnumParamChoiceTest, getSetWhite)
{
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(Selector::WHITE);
    const int32_t value = 65535;
    enumParamChoice.setWhite(value);
    ASSERT_EQ(value, enumParamChoice.getWhite());
}

TEST_F(EnumParamChoiceTest, bitSizeOf)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(Selector::BLACK);
    ASSERT_EQ(8, enumParamChoiceB.bitSizeOf());

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(Selector::GREY);
    ASSERT_EQ(16, enumParamChoiceG.bitSizeOf());

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(Selector::WHITE);
    ASSERT_EQ(32, enumParamChoiceW.bitSizeOf());
}

TEST_F(EnumParamChoiceTest, initializeOffsets)
{
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(Selector::BLACK);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, enumParamChoiceB.initializeOffsets(bitPosition));

    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(Selector::GREY);
    ASSERT_EQ(17, enumParamChoiceG.initializeOffsets(bitPosition));

    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(Selector::WHITE);
    ASSERT_EQ(33, enumParamChoiceW.initializeOffsets(bitPosition));
}

TEST_F(EnumParamChoiceTest, operatorEquality)
{
    EnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(Selector::BLACK);
    EnumParamChoice enumParamChoice2;
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

TEST_F(EnumParamChoiceTest, hashCode)
{
    EnumParamChoice enumParamChoice1;
    enumParamChoice1.initialize(Selector::BLACK);
    EnumParamChoice enumParamChoice2;
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

TEST_F(EnumParamChoiceTest, read)
{
    const Selector selector = Selector::BLACK;
    EnumParamChoice enumParamChoice;
    enumParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeEnumParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    enumParamChoice.read(reader);

    ASSERT_EQ(selector, enumParamChoice.getSelector());
    ASSERT_EQ(value, enumParamChoice.getBlack());
}

TEST_F(EnumParamChoiceTest, write)
{
    Selector selector = Selector::BLACK;
    EnumParamChoice enumParamChoiceB;
    enumParamChoiceB.initialize(selector);
    const int8_t valueB = 99;
    enumParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB;
    enumParamChoiceB.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    EnumParamChoice readEnumParamChoiceB(readerB, selector);
    ASSERT_EQ(valueB, readEnumParamChoiceB.getBlack());

    selector = Selector::GREY;
    EnumParamChoice enumParamChoiceG;
    enumParamChoiceG.initialize(selector);
    const int16_t valueG = 234;
    enumParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG;
    enumParamChoiceG.write(writerG);
    size_t writeBufferByteSizeG;
    const uint8_t* writeBufferG = writerG.getWriteBuffer(writeBufferByteSizeG);
    zserio::BitStreamReader readerG(writeBufferG, writeBufferByteSizeG);
    EnumParamChoice readEnumParamChoiceG(readerG, selector);
    ASSERT_EQ(valueG, readEnumParamChoiceG.getGrey());

    selector = Selector::WHITE;
    EnumParamChoice enumParamChoiceW;
    enumParamChoiceW.initialize(selector);
    const int32_t valueW = 65535;
    enumParamChoiceW.setWhite(valueW);
    zserio::BitStreamWriter writerW;
    enumParamChoiceW.write(writerW);
    size_t writeBufferByteSizeW;
    const uint8_t* writeBufferW = writerW.getWriteBuffer(writeBufferByteSizeW);
    zserio::BitStreamReader readerW(writeBufferW, writeBufferByteSizeW);
    EnumParamChoice readEnumParamChoiceW(readerW, selector);
    ASSERT_EQ(valueW, readEnumParamChoiceW.getWhite());
}

} // namespace enum_param_choice
} // namespace choice_types
