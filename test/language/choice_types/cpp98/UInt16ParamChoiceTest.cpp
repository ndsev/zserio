#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/uint16_param_choice/UInt16ParamChoice.h"

namespace choice_types
{
namespace uint16_param_choice
{

class UIn16ParamChoiceTest : public ::testing::Test
{
protected:
    void writeUIn16ParamChoiceToByteArray(zserio::BitStreamWriter& writer, uint16_t selector, int32_t value)
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

    static const uint16_t VARIANT_A_SELECTOR;
    static const uint16_t VARIANT_B_SELECTOR1;
    static const uint16_t VARIANT_B_SELECTOR2;
    static const uint16_t VARIANT_B_SELECTOR3;
    static const uint16_t EMPTY_SELECTOR1;
    static const uint16_t EMPTY_SELECTOR2;
    static const uint16_t VARIANT_C_SELECTOR;
};

const uint16_t UIn16ParamChoiceTest::VARIANT_A_SELECTOR = 1;
const uint16_t UIn16ParamChoiceTest::VARIANT_B_SELECTOR1 = 2;
const uint16_t UIn16ParamChoiceTest::VARIANT_B_SELECTOR2 = 3;
const uint16_t UIn16ParamChoiceTest::VARIANT_B_SELECTOR3 = 4;
const uint16_t UIn16ParamChoiceTest::EMPTY_SELECTOR1 = 5;
const uint16_t UIn16ParamChoiceTest::EMPTY_SELECTOR2 = 6;
const uint16_t UIn16ParamChoiceTest::VARIANT_C_SELECTOR = 7;

TEST_F(UIn16ParamChoiceTest, emptyConstructor)
{
    UInt16ParamChoice uin16ParamChoice;
    ASSERT_THROW(uin16ParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(UIn16ParamChoiceTest, bitStreamReaderConstructor)
{
    const uint16_t selector = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUIn16ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const UInt16ParamChoice uin16ParamChoice(reader, selector);
    ASSERT_EQ(selector, uin16ParamChoice.getSelector());
    ASSERT_EQ(value, uin16ParamChoice.getA());
}

TEST_F(UIn16ParamChoiceTest, copyConstructor)
{
    const uint16_t selector = VARIANT_A_SELECTOR;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selector);

    const VariantA value = 99;
    uin16ParamChoice.setA(value);

    const UInt16ParamChoice uin16ParamChoiceCopy(uin16ParamChoice);
    ASSERT_EQ(selector, uin16ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uin16ParamChoiceCopy.getA());
}

TEST_F(UIn16ParamChoiceTest, operatorAssignment)
{
    const uint16_t selector = VARIANT_B_SELECTOR3;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selector);

    const VariantB value = 234;
    uin16ParamChoice.setB(value);

    const UInt16ParamChoice uin16ParamChoiceCopy = uin16ParamChoice;
    ASSERT_EQ(selector, uin16ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uin16ParamChoiceCopy.getB());
}

TEST_F(UIn16ParamChoiceTest, initialize)
{
    const uint16_t selector = EMPTY_SELECTOR1;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uin16ParamChoice.getSelector());
}

TEST_F(UIn16ParamChoiceTest, getSelector)
{
    const uint16_t selector = EMPTY_SELECTOR2;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uin16ParamChoice.getSelector());
}

TEST_F(UIn16ParamChoiceTest, getSetA)
{
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(VARIANT_A_SELECTOR);

    const VariantA value = 99;
    uin16ParamChoice.setA(value);
    ASSERT_EQ(value, uin16ParamChoice.getA());
}

TEST_F(UIn16ParamChoiceTest, getSetB)
{
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(VARIANT_B_SELECTOR2);

    const VariantB value = 234;
    uin16ParamChoice.setB(value);
    ASSERT_EQ(value, uin16ParamChoice.getB());
}

TEST_F(UIn16ParamChoiceTest, getSetC)
{
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(VARIANT_C_SELECTOR);

    const VariantC value = 65535;
    uin16ParamChoice.setC(value);
    ASSERT_EQ(value, uin16ParamChoice.getC());
}

TEST_F(UIn16ParamChoiceTest, bitSizeOf)
{
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(VARIANT_A_SELECTOR);
    const VariantA valueA = 99;
    uin16ParamChoice.setA(valueA);
    ASSERT_EQ(8, uin16ParamChoice.bitSizeOf());

    uin16ParamChoice.initialize(VARIANT_B_SELECTOR2);
    const VariantB valueB = 234;
    uin16ParamChoice.setB(valueB);
    ASSERT_EQ(16, uin16ParamChoice.bitSizeOf());

    uin16ParamChoice.initialize(EMPTY_SELECTOR2);
    ASSERT_EQ(0, uin16ParamChoice.bitSizeOf());

    uin16ParamChoice.initialize(VARIANT_C_SELECTOR);
    const VariantC valueC = 65535;
    uin16ParamChoice.setC(valueC);
    ASSERT_EQ(32, uin16ParamChoice.bitSizeOf());
}

TEST_F(UIn16ParamChoiceTest, initializeOffsets)
{
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uin16ParamChoice.initializeOffsets(bitPosition));

    uin16ParamChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(17, uin16ParamChoice.initializeOffsets(bitPosition));

    uin16ParamChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(1, uin16ParamChoice.initializeOffsets(bitPosition));

    uin16ParamChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(33, uin16ParamChoice.initializeOffsets(bitPosition));
}

TEST_F(UIn16ParamChoiceTest, operatorEquality)
{
    const uint16_t selector = VARIANT_A_SELECTOR;
    UInt16ParamChoice uin16ParamChoice1;
    uin16ParamChoice1.initialize(selector);
    UInt16ParamChoice uin16ParamChoice2;
    uin16ParamChoice2.initialize(selector);
    ASSERT_TRUE(uin16ParamChoice1 == uin16ParamChoice2);

    const VariantA valueA = 99;
    uin16ParamChoice1.setA(valueA);
    ASSERT_FALSE(uin16ParamChoice1 == uin16ParamChoice2);

    uin16ParamChoice2.setA(valueA);
    ASSERT_TRUE(uin16ParamChoice1 == uin16ParamChoice2);

    const VariantA diffValueA = valueA + 1;
    uin16ParamChoice2.setA(diffValueA);
    ASSERT_FALSE(uin16ParamChoice1 == uin16ParamChoice2);
}

TEST_F(UIn16ParamChoiceTest, hashCode)
{
    const uint16_t selector = VARIANT_A_SELECTOR;
    UInt16ParamChoice uin16ParamChoice1;
    uin16ParamChoice1.initialize(selector);
    UInt16ParamChoice uin16ParamChoice2;
    uin16ParamChoice2.initialize(selector);
    ASSERT_EQ(uin16ParamChoice1.hashCode(), uin16ParamChoice2.hashCode());

    const VariantA valueA = 99;
    uin16ParamChoice1.setA(valueA);
    ASSERT_NE(uin16ParamChoice1.hashCode(), uin16ParamChoice2.hashCode());

    uin16ParamChoice2.setA(valueA);
    ASSERT_EQ(uin16ParamChoice1.hashCode(), uin16ParamChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    uin16ParamChoice2.setA(diffValueA);
    ASSERT_NE(uin16ParamChoice1.hashCode(), uin16ParamChoice2.hashCode());
}

TEST_F(UIn16ParamChoiceTest, read)
{
    const uint16_t selector = VARIANT_A_SELECTOR;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUIn16ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    uin16ParamChoice.read(reader);

    ASSERT_EQ(selector, uin16ParamChoice.getSelector());
    ASSERT_EQ(value, uin16ParamChoice.getA());
}

TEST_F(UIn16ParamChoiceTest, write)
{
    const uint16_t selectorA = VARIANT_A_SELECTOR;
    UInt16ParamChoice uin16ParamChoice;
    uin16ParamChoice.initialize(selectorA);

    const VariantA valueA = 99;
    uin16ParamChoice.setA(valueA);
    zserio::BitStreamWriter writerA;
    uin16ParamChoice.write(writerA);
    size_t writeBufferByteSizeA;
    const uint8_t* writeBufferA = writerA.getWriteBuffer(writeBufferByteSizeA);
    zserio::BitStreamReader readerA(writeBufferA, writeBufferByteSizeA);
    UInt16ParamChoice readUIn16ParamChoiceA(readerA, selectorA);
    ASSERT_EQ(valueA, readUIn16ParamChoiceA.getA());

    const uint16_t selectorB = VARIANT_B_SELECTOR2;
    uin16ParamChoice.initialize(selectorB);
    const VariantB valueB = 234;
    uin16ParamChoice.setB(valueB);
    zserio::BitStreamWriter writerB;
    uin16ParamChoice.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    UInt16ParamChoice readUIn16ParamChoiceB(readerB, selectorB);
    ASSERT_EQ(valueB, readUIn16ParamChoiceB.getB());

    const uint16_t selectorC= VARIANT_C_SELECTOR;
    uin16ParamChoice.initialize(selectorC);
    const VariantC valueC = 65535;
    uin16ParamChoice.setC(valueC);
    zserio::BitStreamWriter writerC;
    uin16ParamChoice.write(writerC);
    size_t writeBufferByteSizeC;
    const uint8_t* writeBufferC = writerC.getWriteBuffer(writeBufferByteSizeC);
    zserio::BitStreamReader readerC(writeBufferC, writeBufferByteSizeC);
    UInt16ParamChoice readUIn16ParamChoiceC(readerC, selectorC);
    ASSERT_EQ(valueC, readUIn16ParamChoiceC.getC());
}

} // namespace uint16_param_choice
} // namespace choice_types
