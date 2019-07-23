#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/uint64_param_choice/UInt64ParamChoice.h"

namespace choice_types
{
namespace uint64_param_choice
{

class UInt64ParamChoiceTest : public ::testing::Test
{
protected:
    void writeUInt64ParamChoiceToByteArray(zserio::BitStreamWriter& writer, uint64_t selector,
            int32_t value)
    {
        switch (selector)
        {
        case 1:
            writer.writeSignedBits(value, 8);
            break;

        case 2:
        case 3:
        case 4:
            writer.writeSignedBits(value, 16);
            break;

        case 5:
        case 6:
            break;

        default:
            writer.writeSignedBits(value, 32);
            break;
        }
    }

    static const uint64_t VARIANT_A_SELECTOR = 1;
    static const uint64_t VARIANT_B_SELECTOR = 2;
    static const uint64_t VARIANT_C_SELECTOR = 7;
};

TEST_F(UInt64ParamChoiceTest, emptyConstructor)
{
    UInt64ParamChoice uint64ParamChoice;
    ASSERT_THROW(uint64ParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(UInt64ParamChoiceTest, bitStreamReaderConstructor)
{
    const uint64_t selector = VARIANT_A_SELECTOR;
    const int8_t value = 99;
    zserio::BitStreamWriter writer;
    writeUInt64ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    UInt64ParamChoice uint64ParamChoice(reader, selector);
    ASSERT_EQ(selector, uint64ParamChoice.getSelector());
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, fieldConstructor)
{
    const uint64_t selector = VARIANT_A_SELECTOR;
    const int8_t value = 99;

    UInt64ParamChoice uint64ParamChoice(selector, value);
    ASSERT_EQ(selector, uint64ParamChoice.getSelector());
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, copyConstructor)
{
    const uint64_t selector = VARIANT_A_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);

    const UInt64ParamChoice uint64ParamChoiceCopy(uint64ParamChoice);
    ASSERT_EQ(selector, uint64ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceCopy.getA());
}

TEST_F(UInt64ParamChoiceTest, assignmentOperator)
{
    const uint64_t selector = VARIANT_B_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);

    UInt64ParamChoice uint64ParamChoiceCopy;
    uint64ParamChoiceCopy = uint64ParamChoice;
    ASSERT_EQ(selector, uint64ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceCopy.getB());
}

TEST_F(UInt64ParamChoiceTest, moveConstructor)
{
    const uint64_t selector = VARIANT_A_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);

    const UInt64ParamChoice uint64ParamChoiceMoved(std::move(uint64ParamChoice));
    ASSERT_EQ(selector, uint64ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceMoved.getA());
}

TEST_F(UInt64ParamChoiceTest, moveAssignmentOperator)
{
    const uint64_t selector = VARIANT_B_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);

    UInt64ParamChoice uint64ParamChoiceMoved;
    uint64ParamChoiceMoved = std::move(uint64ParamChoice);
    ASSERT_EQ(selector, uint64ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceMoved.getB());
}

TEST_F(UInt64ParamChoiceTest, initialize)
{
    const uint64_t selector = VARIANT_B_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uint64ParamChoice.getSelector());
}

TEST_F(UInt64ParamChoiceTest, getSelector)
{
    const uint64_t selector = true;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uint64ParamChoice.getSelector());
}

TEST_F(UInt64ParamChoiceTest, getSetA)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(VARIANT_A_SELECTOR);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, getSetB)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(VARIANT_B_SELECTOR);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);
    ASSERT_EQ(value, uint64ParamChoice.getB());
}

TEST_F(UInt64ParamChoiceTest, getSetC)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(VARIANT_C_SELECTOR);
    const int32_t value = 23456;
    uint64ParamChoice.setC(value);
    ASSERT_EQ(value, uint64ParamChoice.getC());
}

TEST_F(UInt64ParamChoiceTest, bitSizeOf)
{
    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(VARIANT_A_SELECTOR);
    ASSERT_EQ(8, uint64ParamChoiceB.bitSizeOf());

    UInt64ParamChoice uint64ParamChoiceG;
    uint64ParamChoiceG.initialize(VARIANT_B_SELECTOR);
    ASSERT_EQ(16, uint64ParamChoiceG.bitSizeOf());
}

TEST_F(UInt64ParamChoiceTest, initializeOffsets)
{
    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uint64ParamChoiceB.initializeOffsets(bitPosition));

    UInt64ParamChoice uint64ParamChoiceG;
    uint64ParamChoiceG.initialize(VARIANT_B_SELECTOR);
    ASSERT_EQ(17, uint64ParamChoiceG.initializeOffsets(bitPosition));
}

TEST_F(UInt64ParamChoiceTest, operatorEquality)
{
    UInt64ParamChoice uint64ParamChoice1;
    uint64ParamChoice1.initialize(VARIANT_A_SELECTOR);
    UInt64ParamChoice uint64ParamChoice2;
    uint64ParamChoice2.initialize(VARIANT_A_SELECTOR);
    ASSERT_TRUE(uint64ParamChoice1 == uint64ParamChoice2);

    const int8_t value = 99;
    uint64ParamChoice1.setA(value);
    ASSERT_FALSE(uint64ParamChoice1 == uint64ParamChoice2);

    uint64ParamChoice2.setA(value);
    ASSERT_TRUE(uint64ParamChoice1 == uint64ParamChoice2);

    const int8_t diffValue = value + 1;
    uint64ParamChoice2.setA(diffValue);
    ASSERT_FALSE(uint64ParamChoice1 == uint64ParamChoice2);
}

TEST_F(UInt64ParamChoiceTest, hashCode)
{
    UInt64ParamChoice uint64ParamChoice1;
    uint64ParamChoice1.initialize(VARIANT_A_SELECTOR);
    UInt64ParamChoice uint64ParamChoice2;
    uint64ParamChoice2.initialize(VARIANT_A_SELECTOR);
    ASSERT_EQ(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    const int8_t value = 99;
    uint64ParamChoice1.setA(value);
    ASSERT_NE(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    uint64ParamChoice2.setA(value);
    ASSERT_EQ(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    uint64ParamChoice2.setA(diffValue);
    ASSERT_NE(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());
}

TEST_F(UInt64ParamChoiceTest, read)
{
    const uint64_t selector = VARIANT_A_SELECTOR;
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUInt64ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    uint64ParamChoice.read(reader);

    ASSERT_EQ(selector, uint64ParamChoice.getSelector());
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, write)
{
    uint64_t selector = VARIANT_A_SELECTOR;
    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(selector);
    const int8_t valueB = 99;
    uint64ParamChoiceB.setA(valueB);
    zserio::BitStreamWriter writerB;
    uint64ParamChoiceB.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    UInt64ParamChoice readUInt64ParamChoiceB(readerB, selector);
    ASSERT_EQ(valueB, readUInt64ParamChoiceB.getA());

    selector = VARIANT_B_SELECTOR;
    UInt64ParamChoice uint64ParamChoiceG;
    uint64ParamChoiceG.initialize(selector);
    const int16_t valueG = 234;
    uint64ParamChoiceG.setB(valueG);
    zserio::BitStreamWriter writerG;
    uint64ParamChoiceG.write(writerG);
    size_t writeBufferByteSizeG;
    const uint8_t* writeBufferG = writerG.getWriteBuffer(writeBufferByteSizeG);
    zserio::BitStreamReader readerG(writeBufferG, writeBufferByteSizeG);
    UInt64ParamChoice readUInt64ParamChoiceG(readerG, selector);
    ASSERT_EQ(valueG, readUInt64ParamChoiceG.getB());
}

} // namespace uint64_param_choice
} // namespace choice_types
