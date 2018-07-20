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
        if (selector == true)
            writer.writeSignedBits(value, 8);
        else
            writer.writeSignedBits(value, 16);
    }
};

TEST_F(BoolParamChoiceTest, emptyConstructor)
{
    BoolParamChoice boolParamChoice;
    ASSERT_THROW(boolParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(BoolParamChoiceTest, bitStreamReaderConstructor)
{
    const bool selector = true;
    const int8_t value = 99;
    zserio::BitStreamWriter writer;
    writeBoolParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    BoolParamChoice boolParamChoice(reader, selector);
    ASSERT_EQ(selector, boolParamChoice.getSelector());
    ASSERT_EQ(value, boolParamChoice.getBlack());
}

TEST_F(BoolParamChoiceTest, copyConstructor)
{
    const bool selector = true;
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(selector);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);

    const BoolParamChoice boolParamChoiceCopy(boolParamChoice);
    ASSERT_EQ(selector, boolParamChoiceCopy.getSelector());
    ASSERT_EQ(value, boolParamChoiceCopy.getBlack());
}

TEST_F(BoolParamChoiceTest, operatorAssignment)
{
    const bool selector = false;
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(selector);
    const int16_t value = 234;
    boolParamChoice.setGrey(value);

    const BoolParamChoice boolParamChoiceCopy = boolParamChoice;
    ASSERT_EQ(selector, boolParamChoiceCopy.getSelector());
    ASSERT_EQ(value, boolParamChoiceCopy.getGrey());
}

TEST_F(BoolParamChoiceTest, initialize)
{
    const bool selector = false;
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(selector);
    ASSERT_EQ(selector, boolParamChoice.getSelector());
}

TEST_F(BoolParamChoiceTest, getSelector)
{
    const bool selector = true;
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(selector);
    ASSERT_EQ(selector, boolParamChoice.getSelector());
}

TEST_F(BoolParamChoiceTest, getSetBlack)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(true);
    const int8_t value = 99;
    boolParamChoice.setBlack(value);
    ASSERT_EQ(value, boolParamChoice.getBlack());
}

TEST_F(BoolParamChoiceTest, getSetGrey)
{
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(false);
    const int16_t value = 234;
    boolParamChoice.setGrey(value);
    ASSERT_EQ(value, boolParamChoice.getGrey());
}

TEST_F(BoolParamChoiceTest, bitSizeOf)
{
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(true);
    ASSERT_EQ(8, boolParamChoiceB.bitSizeOf());

    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(false);
    ASSERT_EQ(16, boolParamChoiceG.bitSizeOf());
}

TEST_F(BoolParamChoiceTest, initializeOffsets)
{
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(true);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, boolParamChoiceB.initializeOffsets(bitPosition));

    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(false);
    ASSERT_EQ(17, boolParamChoiceG.initializeOffsets(bitPosition));
}

TEST_F(BoolParamChoiceTest, operatorEquality)
{
    BoolParamChoice boolParamChoice1;
    boolParamChoice1.initialize(true);
    BoolParamChoice boolParamChoice2;
    boolParamChoice2.initialize(true);
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
    boolParamChoice1.initialize(true);
    BoolParamChoice boolParamChoice2;
    boolParamChoice2.initialize(true);
    ASSERT_EQ(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    const int8_t value = 99;
    boolParamChoice1.setBlack(value);
    ASSERT_NE(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    boolParamChoice2.setBlack(value);
    ASSERT_EQ(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    boolParamChoice2.setBlack(diffValue);
    ASSERT_NE(boolParamChoice1.hashCode(), boolParamChoice2.hashCode());
}

TEST_F(BoolParamChoiceTest, read)
{
    const bool selector = true;
    BoolParamChoice boolParamChoice;
    boolParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeBoolParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    boolParamChoice.read(reader);

    ASSERT_EQ(selector, boolParamChoice.getSelector());
    ASSERT_EQ(value, boolParamChoice.getBlack());
}

TEST_F(BoolParamChoiceTest, write)
{
    bool selector = true;
    BoolParamChoice boolParamChoiceB;
    boolParamChoiceB.initialize(selector);
    const int8_t valueB = 99;
    boolParamChoiceB.setBlack(valueB);
    zserio::BitStreamWriter writerB;
    boolParamChoiceB.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    BoolParamChoice readBoolParamChoiceB(readerB, selector);
    ASSERT_EQ(valueB, readBoolParamChoiceB.getBlack());

    selector = false;
    BoolParamChoice boolParamChoiceG;
    boolParamChoiceG.initialize(selector);
    const int16_t valueG = 234;
    boolParamChoiceG.setGrey(valueG);
    zserio::BitStreamWriter writerG;
    boolParamChoiceG.write(writerG);
    size_t writeBufferByteSizeG;
    const uint8_t* writeBufferG = writerG.getWriteBuffer(writeBufferByteSizeG);
    zserio::BitStreamReader readerG(writeBufferG, writeBufferByteSizeG);
    BoolParamChoice readBoolParamChoiceG(readerG, selector);
    ASSERT_EQ(valueG, readBoolParamChoiceG.getGrey());
}

} // namespace bool_param_choice
} // namespace choice_types
