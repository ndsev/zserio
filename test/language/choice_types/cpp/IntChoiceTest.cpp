#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/int_choice/IntChoice.h"

namespace choice_types
{
namespace int_choice
{

class IntChoiceTest : public ::testing::Test
{
protected:
    void writeIntChoiceToByteArray(zserio::BitStreamWriter& writer, uint16_t tag, int32_t value)
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

const uint16_t IntChoiceTest::VARIANT_A_SELECTOR = 1;
const uint16_t IntChoiceTest::VARIANT_B_SELECTOR1 = 2;
const uint16_t IntChoiceTest::VARIANT_B_SELECTOR2 = 3;
const uint16_t IntChoiceTest::VARIANT_B_SELECTOR3 = 4;
const uint16_t IntChoiceTest::EMPTY_SELECTOR1 = 5;
const uint16_t IntChoiceTest::EMPTY_SELECTOR2 = 6;
const uint16_t IntChoiceTest::VARIANT_C_SELECTOR = 7;

TEST_F(IntChoiceTest, emptyConstructor)
{
    IntChoice intChoice;
    ASSERT_THROW(intChoice.getTag(), zserio::CppRuntimeException);
}

TEST_F(IntChoiceTest, bitStreamReaderConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeIntChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const IntChoice intChoice(reader, tag);
    ASSERT_EQ(tag, intChoice.getTag());
    ASSERT_EQ(value, intChoice.getA());
}

TEST_F(IntChoiceTest, copyConstructor)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    IntChoice intChoice;
    intChoice.initialize(tag);

    const VariantA value = 99;
    intChoice.setA(value);

    const IntChoice intChoiceCopy(intChoice);
    ASSERT_EQ(tag, intChoiceCopy.getTag());
    ASSERT_EQ(value, intChoiceCopy.getA());
}

TEST_F(IntChoiceTest, operatorAssignment)
{
    const uint16_t tag = VARIANT_B_SELECTOR3;
    IntChoice intChoice;
    intChoice.initialize(tag);

    const VariantB value = 234;
    intChoice.setB(value);

    const IntChoice intChoiceCopy = intChoice;
    ASSERT_EQ(tag, intChoiceCopy.getTag());
    ASSERT_EQ(value, intChoiceCopy.getB());
}

TEST_F(IntChoiceTest, initialize)
{
    const uint16_t tag = EMPTY_SELECTOR1;
    IntChoice intChoice;
    intChoice.initialize(tag);
    ASSERT_EQ(tag, intChoice.getTag());
}

TEST_F(IntChoiceTest, getTag)
{
    const uint16_t tag = EMPTY_SELECTOR2;
    IntChoice intChoice;
    intChoice.initialize(tag);
    ASSERT_EQ(tag, intChoice.getTag());
}

TEST_F(IntChoiceTest, getSetA)
{
    IntChoice intChoice;
    intChoice.initialize(VARIANT_A_SELECTOR);

    const VariantA value = 99;
    intChoice.setA(value);
    ASSERT_EQ(value, intChoice.getA());
}

TEST_F(IntChoiceTest, getSetB)
{
    IntChoice intChoice;
    intChoice.initialize(VARIANT_B_SELECTOR2);

    const VariantB value = 234;
    intChoice.setB(value);
    ASSERT_EQ(value, intChoice.getB());
}

TEST_F(IntChoiceTest, getSetC)
{
    IntChoice intChoice;
    intChoice.initialize(VARIANT_C_SELECTOR);

    const VariantC value = 65535;
    intChoice.setC(value);
    ASSERT_EQ(value, intChoice.getC());
}

TEST_F(IntChoiceTest, bitSizeOf)
{
    IntChoice intChoice;
    intChoice.initialize(VARIANT_A_SELECTOR);
    const VariantA valueA = 99;
    intChoice.setA(valueA);
    ASSERT_EQ(8, intChoice.bitSizeOf());

    intChoice.initialize(VARIANT_B_SELECTOR2);
    const VariantB valueB = 234;
    intChoice.setB(valueB);
    ASSERT_EQ(16, intChoice.bitSizeOf());

    intChoice.initialize(EMPTY_SELECTOR2);
    ASSERT_EQ(0, intChoice.bitSizeOf());

    intChoice.initialize(VARIANT_C_SELECTOR);
    const VariantC valueC = 65535;
    intChoice.setC(valueC);
    ASSERT_EQ(32, intChoice.bitSizeOf());
}

TEST_F(IntChoiceTest, initializeOffsets)
{
    IntChoice intChoice;
    intChoice.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, intChoice.initializeOffsets(bitPosition));

    intChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(17, intChoice.initializeOffsets(bitPosition));

    intChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(1, intChoice.initializeOffsets(bitPosition));

    intChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(33, intChoice.initializeOffsets(bitPosition));
}

TEST_F(IntChoiceTest, operatorEquality)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    IntChoice intChoice1;
    intChoice1.initialize(tag);
    IntChoice intChoice2;
    intChoice2.initialize(tag);
    ASSERT_TRUE(intChoice1 == intChoice2);

    const VariantA valueA = 99;
    intChoice1.setA(valueA);
    ASSERT_FALSE(intChoice1 == intChoice2);

    intChoice2.setA(valueA);
    ASSERT_TRUE(intChoice1 == intChoice2);

    const VariantA diffValueA = valueA + 1;
    intChoice2.setA(diffValueA);
    ASSERT_FALSE(intChoice1 == intChoice2);
}

TEST_F(IntChoiceTest, hashCode)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    IntChoice intChoice1;
    intChoice1.initialize(tag);
    IntChoice intChoice2;
    intChoice2.initialize(tag);
    ASSERT_EQ(intChoice1.hashCode(), intChoice2.hashCode());

    const VariantA valueA = 99;
    intChoice1.setA(valueA);
    ASSERT_NE(intChoice1.hashCode(), intChoice2.hashCode());

    intChoice2.setA(valueA);
    ASSERT_EQ(intChoice1.hashCode(), intChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    intChoice2.setA(diffValueA);
    ASSERT_NE(intChoice1.hashCode(), intChoice2.hashCode());
}

TEST_F(IntChoiceTest, read)
{
    const uint16_t tag = VARIANT_A_SELECTOR;
    IntChoice intChoice;
    intChoice.initialize(tag);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeIntChoiceToByteArray(writer, tag, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    intChoice.read(reader);

    ASSERT_EQ(tag, intChoice.getTag());
    ASSERT_EQ(value, intChoice.getA());
}

TEST_F(IntChoiceTest, write)
{
    const uint16_t tagA = VARIANT_A_SELECTOR;
    IntChoice intChoice;
    intChoice.initialize(tagA);

    const VariantA valueA = 99;
    intChoice.setA(valueA);
    zserio::BitStreamWriter writerA;
    intChoice.write(writerA);
    size_t writeBufferByteSizeA;
    const uint8_t* writeBufferA = writerA.getWriteBuffer(writeBufferByteSizeA);
    zserio::BitStreamReader readerA(writeBufferA, writeBufferByteSizeA);
    IntChoice readIntChoiceA(readerA, tagA);
    ASSERT_EQ(valueA, readIntChoiceA.getA());

    const uint16_t tagB = VARIANT_B_SELECTOR2;
    intChoice.initialize(tagB);
    const VariantB valueB = 234;
    intChoice.setB(valueB);
    zserio::BitStreamWriter writerB;
    intChoice.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    IntChoice readIntChoiceB(readerB, tagB);
    ASSERT_EQ(valueB, readIntChoiceB.getB());

    const uint16_t tagC= VARIANT_C_SELECTOR;
    intChoice.initialize(tagC);
    const VariantC valueC = 65535;
    intChoice.setC(valueC);
    zserio::BitStreamWriter writerC;
    intChoice.write(writerC);
    size_t writeBufferByteSizeC;
    const uint8_t* writeBufferC = writerC.getWriteBuffer(writeBufferByteSizeC);
    zserio::BitStreamReader readerC(writeBufferC, writeBufferByteSizeC);
    IntChoice readIntChoiceC(readerC, tagC);
    ASSERT_EQ(valueC, readIntChoiceC.getC());
}

} // namespace int_choice
} // namespace choice_types
