#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/uint16_param_choice/UInt16ParamChoice.h"

namespace choice_types
{
namespace uint16_param_choice
{

class UInt16ParamChoiceTest : public ::testing::Test
{
protected:
    void writeUInt16ParamChoiceToByteArray(zserio::BitStreamWriter& writer, uint16_t tag, int32_t value)
    {
        switch (tag)
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

    static const uint16_t VARIANT_A_SELECTOR;
    static const uint16_t VARIANT_B_SELECTOR1;
    static const uint16_t VARIANT_B_SELECTOR2;
    static const uint16_t VARIANT_B_SELECTOR3;
    static const uint16_t EMPTY_SELECTOR1;
    static const uint16_t EMPTY_SELECTOR2;
    static const uint16_t VARIANT_C_SELECTOR;
};

const uint16_t UInt16ParamChoiceTest::VARIANT_A_SELECTOR = 1;
const uint16_t UInt16ParamChoiceTest::VARIANT_B_SELECTOR1 = 2;
const uint16_t UInt16ParamChoiceTest::VARIANT_B_SELECTOR2 = 3;
const uint16_t UInt16ParamChoiceTest::VARIANT_B_SELECTOR3 = 4;
const uint16_t UInt16ParamChoiceTest::EMPTY_SELECTOR1 = 5;
const uint16_t UInt16ParamChoiceTest::EMPTY_SELECTOR2 = 6;
const uint16_t UInt16ParamChoiceTest::VARIANT_C_SELECTOR = 7;

TEST_F(UInt16ParamChoiceTest, emptyConstructor)
{
    UInt16ParamChoice uint16ParamChoice;
    ASSERT_THROW(uint16ParamChoice.getTag(), zserio::CppRuntimeException);
}

TEST_F(UInt16ParamChoiceTest, bitStreamReaderConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUInt16ParamChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const UInt16ParamChoice uint16ParamChoice(reader, tag);
    ASSERT_EQ(tag, uint16ParamChoice.getTag());
    ASSERT_EQ(value, uint16ParamChoice.getA());
}

TEST_F(UInt16ParamChoiceTest, fieldConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    const int8_t value = 99;

    UInt16ParamChoice uint16ParamChoice(value);
    uint16ParamChoice.initialize(tag);
    ASSERT_EQ(tag, uint16ParamChoice.getTag());
    ASSERT_EQ(value, uint16ParamChoice.getA());
}

TEST_F(UInt16ParamChoiceTest, copyConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);

    const VariantA value = 99;
    uint16ParamChoice.setA(value);

    const UInt16ParamChoice uint16ParamChoiceCopy(uint16ParamChoice);
    ASSERT_EQ(tag, uint16ParamChoiceCopy.getTag());
    ASSERT_EQ(value, uint16ParamChoiceCopy.getA());
}

TEST_F(UInt16ParamChoiceTest, assignmentOperator)
{
    const uint16_t tag = VARIANT_B_SELECTOR3;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);

    const VariantB value = 234;
    uint16ParamChoice.setB(value);

    UInt16ParamChoice uint16ParamChoiceCopy;
    uint16ParamChoiceCopy = uint16ParamChoice;
    ASSERT_EQ(tag, uint16ParamChoiceCopy.getTag());
    ASSERT_EQ(value, uint16ParamChoiceCopy.getB());
}

TEST_F(UInt16ParamChoiceTest, moveConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);

    const VariantA value = 99;
    uint16ParamChoice.setA(value);

    const UInt16ParamChoice uint16ParamChoiceMoved(std::move(uint16ParamChoice));
    ASSERT_EQ(tag, uint16ParamChoiceMoved.getTag());
    ASSERT_EQ(value, uint16ParamChoiceMoved.getA());
}

TEST_F(UInt16ParamChoiceTest, moveAssignmentOperator)
{
    const uint16_t tag = VARIANT_B_SELECTOR3;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);

    const VariantB value = 234;
    uint16ParamChoice.setB(value);

    UInt16ParamChoice uint16ParamChoiceMoved;
    uint16ParamChoiceMoved = std::move(uint16ParamChoice);
    ASSERT_EQ(tag, uint16ParamChoiceMoved.getTag());
    ASSERT_EQ(value, uint16ParamChoiceMoved.getB());
}

TEST_F(UInt16ParamChoiceTest, initialize)
{
    const uint16_t tag = EMPTY_SELECTOR1;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);
    ASSERT_EQ(tag, uint16ParamChoice.getTag());
}

TEST_F(UInt16ParamChoiceTest, getTag)
{
    const uint16_t tag = EMPTY_SELECTOR2;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tag);
    ASSERT_EQ(tag, uint16ParamChoice.getTag());
}

TEST_F(UInt16ParamChoiceTest, getSetA)
{
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(VARIANT_A_SELECTOR);

    const VariantA value = 99;
    uint16ParamChoice.setA(value);
    ASSERT_EQ(value, uint16ParamChoice.getA());
}

TEST_F(UInt16ParamChoiceTest, getSetB)
{
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(VARIANT_B_SELECTOR2);

    const VariantB value = 234;
    uint16ParamChoice.setB(value);
    ASSERT_EQ(value, uint16ParamChoice.getB());
}

TEST_F(UInt16ParamChoiceTest, getSetC)
{
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(VARIANT_C_SELECTOR);

    const VariantC value = 65535;
    uint16ParamChoice.setC(value);
    ASSERT_EQ(value, uint16ParamChoice.getC());
}

TEST_F(UInt16ParamChoiceTest, bitSizeOf)
{
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(VARIANT_A_SELECTOR);
    const VariantA valueA = 99;
    uint16ParamChoice.setA(valueA);
    ASSERT_EQ(8, uint16ParamChoice.bitSizeOf());

    uint16ParamChoice.initialize(VARIANT_B_SELECTOR2);
    const VariantB valueB = 234;
    uint16ParamChoice.setB(valueB);
    ASSERT_EQ(16, uint16ParamChoice.bitSizeOf());

    uint16ParamChoice.initialize(EMPTY_SELECTOR2);
    ASSERT_EQ(0, uint16ParamChoice.bitSizeOf());

    uint16ParamChoice.initialize(VARIANT_C_SELECTOR);
    const VariantC valueC = 65535;
    uint16ParamChoice.setC(valueC);
    ASSERT_EQ(32, uint16ParamChoice.bitSizeOf());
}

TEST_F(UInt16ParamChoiceTest, initializeOffsets)
{
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uint16ParamChoice.initializeOffsets(bitPosition));

    uint16ParamChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(17, uint16ParamChoice.initializeOffsets(bitPosition));

    uint16ParamChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(1, uint16ParamChoice.initializeOffsets(bitPosition));

    uint16ParamChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(33, uint16ParamChoice.initializeOffsets(bitPosition));
}

TEST_F(UInt16ParamChoiceTest, operatorEquality)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    UInt16ParamChoice uint16ParamChoice1;
    uint16ParamChoice1.initialize(tag);
    UInt16ParamChoice uint16ParamChoice2;
    uint16ParamChoice2.initialize(tag);
    ASSERT_TRUE(uint16ParamChoice1 == uint16ParamChoice2);

    const VariantA valueA = 99;
    uint16ParamChoice1.setA(valueA);
    ASSERT_FALSE(uint16ParamChoice1 == uint16ParamChoice2);

    uint16ParamChoice2.setA(valueA);
    ASSERT_TRUE(uint16ParamChoice1 == uint16ParamChoice2);

    const VariantA diffValueA = valueA + 1;
    uint16ParamChoice2.setA(diffValueA);
    ASSERT_FALSE(uint16ParamChoice1 == uint16ParamChoice2);
}

TEST_F(UInt16ParamChoiceTest, hashCode)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    UInt16ParamChoice uint16ParamChoice1;
    uint16ParamChoice1.initialize(tag);
    UInt16ParamChoice uint16ParamChoice2;
    uint16ParamChoice2.initialize(tag);
    ASSERT_EQ(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

    const VariantA valueA = 99;
    uint16ParamChoice1.setA(valueA);
    ASSERT_NE(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

    uint16ParamChoice2.setA(valueA);
    ASSERT_EQ(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    uint16ParamChoice2.setA(diffValueA);
    ASSERT_NE(uint16ParamChoice1.hashCode(), uint16ParamChoice2.hashCode());
}

TEST_F(UInt16ParamChoiceTest, read)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUInt16ParamChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    const UInt16ParamChoice uint16ParamChoice(reader, tag);

    ASSERT_EQ(tag, uint16ParamChoice.getTag());
    ASSERT_EQ(value, uint16ParamChoice.getA());
}

TEST_F(UInt16ParamChoiceTest, write)
{
    const uint16_t tagA = VARIANT_A_SELECTOR;
    UInt16ParamChoice uint16ParamChoice;
    uint16ParamChoice.initialize(tagA);

    const VariantA valueA = 99;
    uint16ParamChoice.setA(valueA);
    zserio::BitStreamWriter writerA;
    uint16ParamChoice.write(writerA);
    size_t writeBufferByteSizeA;
    const uint8_t* writeBufferA = writerA.getWriteBuffer(writeBufferByteSizeA);
    zserio::BitStreamReader readerA(writeBufferA, writeBufferByteSizeA);
    UInt16ParamChoice readUInt16ParamChoiceA(readerA, tagA);
    ASSERT_EQ(valueA, readUInt16ParamChoiceA.getA());

    const uint16_t tagB = VARIANT_B_SELECTOR2;
    uint16ParamChoice.initialize(tagB);
    const VariantB valueB = 234;
    uint16ParamChoice.setB(valueB);
    zserio::BitStreamWriter writerB;
    uint16ParamChoice.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    UInt16ParamChoice readUInt16ParamChoiceB(readerB, tagB);
    ASSERT_EQ(valueB, readUInt16ParamChoiceB.getB());

    const uint16_t tagC= VARIANT_C_SELECTOR;
    uint16ParamChoice.initialize(tagC);
    const VariantC valueC = 65535;
    uint16ParamChoice.setC(valueC);
    zserio::BitStreamWriter writerC;
    uint16ParamChoice.write(writerC);
    size_t writeBufferByteSizeC;
    const uint8_t* writeBufferC = writerC.getWriteBuffer(writeBufferByteSizeC);
    zserio::BitStreamReader readerC(writeBufferC, writeBufferByteSizeC);
    UInt16ParamChoice readUInt16ParamChoiceC(readerC, tagC);
    ASSERT_EQ(valueC, readUInt16ParamChoiceC.getC());
}

} // namespace uint16_param_choice
} // namespace choice_types
