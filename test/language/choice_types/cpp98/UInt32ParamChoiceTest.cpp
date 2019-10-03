#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/uint32_param_choice/UInt32ParamChoice.h"

namespace choice_types
{
namespace uint32_param_choice
{

class UIn32ParamChoiceTest : public ::testing::Test
{
protected:
    void writeUIn32ParamChoiceToByteArray(zserio::BitStreamWriter& writer, uint32_t selector, int32_t value)
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

    static const uint32_t VARIANT_A_SELECTOR;
    static const uint32_t VARIANT_B_SELECTOR1;
    static const uint32_t VARIANT_B_SELECTOR2;
    static const uint32_t VARIANT_B_SELECTOR3;
    static const uint32_t EMPTY_SELECTOR1;
    static const uint32_t EMPTY_SELECTOR2;
    static const uint32_t VARIANT_C_SELECTOR;
};

const uint32_t UIn32ParamChoiceTest::VARIANT_A_SELECTOR = 1;
const uint32_t UIn32ParamChoiceTest::VARIANT_B_SELECTOR1 = 2;
const uint32_t UIn32ParamChoiceTest::VARIANT_B_SELECTOR2 = 3;
const uint32_t UIn32ParamChoiceTest::VARIANT_B_SELECTOR3 = 4;
const uint32_t UIn32ParamChoiceTest::EMPTY_SELECTOR1 = 5;
const uint32_t UIn32ParamChoiceTest::EMPTY_SELECTOR2 = 6;
const uint32_t UIn32ParamChoiceTest::VARIANT_C_SELECTOR = 7;

TEST_F(UIn32ParamChoiceTest, emptyConstructor)
{
    UInt32ParamChoice uin32ParamChoice;
    ASSERT_THROW(uin32ParamChoice.getSelector(), zserio::CppRuntimeException);
}

TEST_F(UIn32ParamChoiceTest, bitStreamReaderConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUIn32ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    const UInt32ParamChoice uin32ParamChoice(reader, selector);
    ASSERT_EQ(selector, uin32ParamChoice.getSelector());
    ASSERT_EQ(value, uin32ParamChoice.getA());
}

TEST_F(UIn32ParamChoiceTest, copyConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selector);

    const VariantA value = 99;
    uin32ParamChoice.setA(value);

    const UInt32ParamChoice uin32ParamChoiceCopy(uin32ParamChoice);
    ASSERT_EQ(selector, uin32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uin32ParamChoiceCopy.getA());
}

TEST_F(UIn32ParamChoiceTest, operatorAssignment)
{
    const uint32_t selector = VARIANT_B_SELECTOR3;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selector);

    const VariantB value = 234;
    uin32ParamChoice.setB(value);

    const UInt32ParamChoice uin32ParamChoiceCopy = uin32ParamChoice;
    ASSERT_EQ(selector, uin32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uin32ParamChoiceCopy.getB());
}

TEST_F(UIn32ParamChoiceTest, initialize)
{
    const uint32_t selector = EMPTY_SELECTOR1;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uin32ParamChoice.getSelector());
}

TEST_F(UIn32ParamChoiceTest, getSelector)
{
    const uint32_t selector = EMPTY_SELECTOR2;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uin32ParamChoice.getSelector());
}

TEST_F(UIn32ParamChoiceTest, getSetA)
{
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(VARIANT_A_SELECTOR);

    const VariantA value = 99;
    uin32ParamChoice.setA(value);
    ASSERT_EQ(value, uin32ParamChoice.getA());
}

TEST_F(UIn32ParamChoiceTest, getSetB)
{
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(VARIANT_B_SELECTOR2);

    const VariantB value = 234;
    uin32ParamChoice.setB(value);
    ASSERT_EQ(value, uin32ParamChoice.getB());
}

TEST_F(UIn32ParamChoiceTest, getSetC)
{
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(VARIANT_C_SELECTOR);

    const VariantC value = 65535;
    uin32ParamChoice.setC(value);
    ASSERT_EQ(value, uin32ParamChoice.getC());
}

TEST_F(UIn32ParamChoiceTest, bitSizeOf)
{
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(VARIANT_A_SELECTOR);
    const VariantA valueA = 99;
    uin32ParamChoice.setA(valueA);
    ASSERT_EQ(8, uin32ParamChoice.bitSizeOf());

    uin32ParamChoice.initialize(VARIANT_B_SELECTOR2);
    const VariantB valueB = 234;
    uin32ParamChoice.setB(valueB);
    ASSERT_EQ(32, uin32ParamChoice.bitSizeOf());

    uin32ParamChoice.initialize(EMPTY_SELECTOR2);
    ASSERT_EQ(0, uin32ParamChoice.bitSizeOf());

    uin32ParamChoice.initialize(VARIANT_C_SELECTOR);
    const VariantC valueC = 65535;
    uin32ParamChoice.setC(valueC);
    ASSERT_EQ(32, uin32ParamChoice.bitSizeOf());
}

TEST_F(UIn32ParamChoiceTest, initializeOffsets)
{
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uin32ParamChoice.initializeOffsets(bitPosition));

    uin32ParamChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(17, uin32ParamChoice.initializeOffsets(bitPosition));

    uin32ParamChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(1, uin32ParamChoice.initializeOffsets(bitPosition));

    uin32ParamChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(33, uin32ParamChoice.initializeOffsets(bitPosition));
}

TEST_F(UIn32ParamChoiceTest, operatorEquality)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uin32ParamChoice1;
    uin32ParamChoice1.initialize(selector);
    UInt32ParamChoice uin32ParamChoice2;
    uin32ParamChoice2.initialize(selector);
    ASSERT_TRUE(uin32ParamChoice1 == uin32ParamChoice2);

    const VariantA valueA = 99;
    uin32ParamChoice1.setA(valueA);
    ASSERT_FALSE(uin32ParamChoice1 == uin32ParamChoice2);

    uin32ParamChoice2.setA(valueA);
    ASSERT_TRUE(uin32ParamChoice1 == uin32ParamChoice2);

    const VariantA diffValueA = valueA + 1;
    uin32ParamChoice2.setA(diffValueA);
    ASSERT_FALSE(uin32ParamChoice1 == uin32ParamChoice2);
}

TEST_F(UIn32ParamChoiceTest, hashCode)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uin32ParamChoice1;
    uin32ParamChoice1.initialize(selector);
    UInt32ParamChoice uin32ParamChoice2;
    uin32ParamChoice2.initialize(selector);
    ASSERT_EQ(uin32ParamChoice1.hashCode(), uin32ParamChoice2.hashCode());

    const VariantA valueA = 99;
    uin32ParamChoice1.setA(valueA);
    ASSERT_NE(uin32ParamChoice1.hashCode(), uin32ParamChoice2.hashCode());

    uin32ParamChoice2.setA(valueA);
    ASSERT_EQ(uin32ParamChoice1.hashCode(), uin32ParamChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    uin32ParamChoice2.setA(diffValueA);
    ASSERT_NE(uin32ParamChoice1.hashCode(), uin32ParamChoice2.hashCode());
}

TEST_F(UIn32ParamChoiceTest, read)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selector);

    zserio::BitStreamWriter writer;
    const int8_t value = 99;
    writeUIn32ParamChoiceToByteArray(writer, selector, value);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    uin32ParamChoice.read(reader);

    ASSERT_EQ(selector, uin32ParamChoice.getSelector());
    ASSERT_EQ(value, uin32ParamChoice.getA());
}

TEST_F(UIn32ParamChoiceTest, write)
{
    const uint32_t selectorA = VARIANT_A_SELECTOR;
    UInt32ParamChoice uin32ParamChoice;
    uin32ParamChoice.initialize(selectorA);

    const VariantA valueA = 99;
    uin32ParamChoice.setA(valueA);
    zserio::BitStreamWriter writerA;
    uin32ParamChoice.write(writerA);
    size_t writeBufferByteSizeA;
    const uint8_t* writeBufferA = writerA.getWriteBuffer(writeBufferByteSizeA);
    zserio::BitStreamReader readerA(writeBufferA, writeBufferByteSizeA);
    UInt32ParamChoice readUIn32ParamChoiceA(readerA, selectorA);
    ASSERT_EQ(valueA, readUIn32ParamChoiceA.getA());

    const uint32_t selectorB = VARIANT_B_SELECTOR2;
    uin32ParamChoice.initialize(selectorB);
    const VariantB valueB = 234;
    uin32ParamChoice.setB(valueB);
    zserio::BitStreamWriter writerB;
    uin32ParamChoice.write(writerB);
    size_t writeBufferByteSizeB;
    const uint8_t* writeBufferB = writerB.getWriteBuffer(writeBufferByteSizeB);
    zserio::BitStreamReader readerB(writeBufferB, writeBufferByteSizeB);
    UInt32ParamChoice readUIn32ParamChoiceB(readerB, selectorB);
    ASSERT_EQ(valueB, readUIn32ParamChoiceB.getB());

    const uint32_t selectorC = VARIANT_C_SELECTOR;
    uin32ParamChoice.initialize(selectorC);
    const VariantC valueC = 65535;
    uin32ParamChoice.setC(valueC);
    zserio::BitStreamWriter writerC;
    uin32ParamChoice.write(writerC);
    size_t writeBufferByteSizeC;
    const uint8_t* writeBufferC = writerC.getWriteBuffer(writeBufferByteSizeC);
    zserio::BitStreamReader readerC(writeBufferC, writeBufferByteSizeC);
    UInt32ParamChoice readUIn32ParamChoiceC(readerC, selectorC);
    ASSERT_EQ(valueC, readUIn32ParamChoiceC.getC());
}

} // namespace uint32_param_choice
} // namespace choice_types
