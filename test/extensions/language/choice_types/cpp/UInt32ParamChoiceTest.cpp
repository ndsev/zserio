#include "choice_types/uint32_param_choice/UInt32ParamChoice.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace choice_types
{
namespace uint32_param_choice
{

class UInt32ParamChoiceTest : public ::testing::Test
{
protected:
    void writeUInt32ParamChoiceToByteArray(zserio::BitStreamWriter& writer, uint32_t selector, int32_t value)
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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t UInt32ParamChoiceTest::VARIANT_A_SELECTOR = 1;
const uint32_t UInt32ParamChoiceTest::VARIANT_B_SELECTOR1 = 2;
const uint32_t UInt32ParamChoiceTest::VARIANT_B_SELECTOR2 = 3;
const uint32_t UInt32ParamChoiceTest::VARIANT_B_SELECTOR3 = 4;
const uint32_t UInt32ParamChoiceTest::EMPTY_SELECTOR1 = 5;
const uint32_t UInt32ParamChoiceTest::EMPTY_SELECTOR2 = 6;
const uint32_t UInt32ParamChoiceTest::VARIANT_C_SELECTOR = 7;

TEST_F(UInt32ParamChoiceTest, emptyConstructor)
{
    {
        UInt32ParamChoice uint32ParamChoice;
        ASSERT_THROW(uint32ParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        UInt32ParamChoice uint32ParamChoice = {};
        ASSERT_THROW(uint32ParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(UInt32ParamChoiceTest, bitStreamReaderConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer(bitBuffer);
    const int8_t value = 99;
    writeUInt32ParamChoiceToByteArray(writer, selector, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const UInt32ParamChoice uint32ParamChoice(reader, selector);
    ASSERT_EQ(selector, uint32ParamChoice.getSelector());
    ASSERT_EQ(value, uint32ParamChoice.getValueA());
}

TEST_F(UInt32ParamChoiceTest, copyConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);

    const VariantA value = 99;
    uint32ParamChoice.setValueA(value);

    const UInt32ParamChoice uint32ParamChoiceCopy(uint32ParamChoice);
    ASSERT_EQ(selector, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getValueA());
}

TEST_F(UInt32ParamChoiceTest, assignmentOperator)
{
    const uint32_t selector = VARIANT_B_SELECTOR3;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);

    const VariantB value = 234;
    uint32ParamChoice.setValueB(value);

    UInt32ParamChoice uint32ParamChoiceCopy;
    uint32ParamChoiceCopy = uint32ParamChoice;
    ASSERT_EQ(selector, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getValueB());
}

TEST_F(UInt32ParamChoiceTest, moveConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);

    const VariantA value = 99;
    uint32ParamChoice.setValueA(value);

    const UInt32ParamChoice uint32ParamChoiceMoved(std::move(uint32ParamChoice));
    ASSERT_EQ(selector, uint32ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceMoved.getValueA());
}

TEST_F(UInt32ParamChoiceTest, moveAssignmentOperator)
{
    const uint32_t selector = VARIANT_B_SELECTOR3;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);

    const VariantB value = 234;
    uint32ParamChoice.setValueB(value);

    UInt32ParamChoice uint32ParamChoiceMoved;
    uint32ParamChoiceMoved = std::move(uint32ParamChoice);
    ASSERT_EQ(selector, uint32ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceMoved.getValueB());
}

TEST_F(UInt32ParamChoiceTest, propagateAllocatorCopyConstructor)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);

    const VariantA value = 99;
    uint32ParamChoice.setValueA(value);

    const UInt32ParamChoice uint32ParamChoiceCopy(
            zserio::PropagateAllocator, uint32ParamChoice, UInt32ParamChoice::allocator_type());
    ASSERT_EQ(selector, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getValueA());
}

TEST_F(UInt32ParamChoiceTest, initialize)
{
    const uint32_t selector = EMPTY_SELECTOR1;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uint32ParamChoice.getSelector());
}

TEST_F(UInt32ParamChoiceTest, getSelector)
{
    const uint32_t selector = EMPTY_SELECTOR2;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);
    ASSERT_EQ(selector, uint32ParamChoice.getSelector());
}

TEST_F(UInt32ParamChoiceTest, getSetA)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_A_SELECTOR);

    const VariantA value = 99;
    uint32ParamChoice.setValueA(value);
    ASSERT_EQ(value, uint32ParamChoice.getValueA());
}

TEST_F(UInt32ParamChoiceTest, getSetB)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_B_SELECTOR2);

    const VariantB value = 234;
    uint32ParamChoice.setValueB(value);
    ASSERT_EQ(value, uint32ParamChoice.getValueB());
}

TEST_F(UInt32ParamChoiceTest, getSetC)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_C_SELECTOR);

    const VariantC value = 65535;
    uint32ParamChoice.setValueC(value);
    ASSERT_EQ(value, uint32ParamChoice.getValueC());
}

TEST_F(UInt32ParamChoiceTest, choiceTag)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_A_SELECTOR);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_valueA, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_valueB, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_valueC, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(UInt32ParamChoice::UNDEFINED_CHOICE, uint32ParamChoice.choiceTag());
}

TEST_F(UInt32ParamChoiceTest, bitSizeOf)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_A_SELECTOR);
    const VariantA valueA = 99;
    uint32ParamChoice.setValueA(valueA);
    ASSERT_EQ(8, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(VARIANT_B_SELECTOR2);
    const VariantB valueB = 234;
    uint32ParamChoice.setValueB(valueB);
    ASSERT_EQ(32, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(EMPTY_SELECTOR2);
    ASSERT_EQ(0, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(VARIANT_C_SELECTOR);
    const VariantC valueC = 65535;
    uint32ParamChoice.setValueC(valueC);
    ASSERT_EQ(32, uint32ParamChoice.bitSizeOf());
}

TEST_F(UInt32ParamChoiceTest, initializeOffsets)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(VARIANT_A_SELECTOR);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(VARIANT_B_SELECTOR1);
    ASSERT_EQ(17, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(EMPTY_SELECTOR1);
    ASSERT_EQ(1, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(VARIANT_C_SELECTOR);
    ASSERT_EQ(33, uint32ParamChoice.initializeOffsets(bitPosition));
}

TEST_F(UInt32ParamChoiceTest, operatorEquality)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice1;
    uint32ParamChoice1.initialize(selector);
    UInt32ParamChoice uint32ParamChoice2;
    uint32ParamChoice2.initialize(selector);
    ASSERT_TRUE(uint32ParamChoice1 == uint32ParamChoice2);

    const VariantA valueA = 99;
    uint32ParamChoice1.setValueA(valueA);
    ASSERT_FALSE(uint32ParamChoice1 == uint32ParamChoice2);

    uint32ParamChoice2.setValueA(valueA);
    ASSERT_TRUE(uint32ParamChoice1 == uint32ParamChoice2);

    const VariantA diffValueA = valueA + 1;
    uint32ParamChoice2.setValueA(diffValueA);
    ASSERT_FALSE(uint32ParamChoice1 == uint32ParamChoice2);
}

TEST_F(UInt32ParamChoiceTest, operatorLessThan)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice1;
    uint32ParamChoice1.initialize(selector);
    UInt32ParamChoice uint32ParamChoice2;
    uint32ParamChoice2.initialize(VARIANT_C_SELECTOR);
    ASSERT_TRUE(uint32ParamChoice1 < uint32ParamChoice2);
    ASSERT_FALSE(uint32ParamChoice2 < uint32ParamChoice1);

    uint32ParamChoice2.initialize(selector);
    ASSERT_FALSE(uint32ParamChoice1 < uint32ParamChoice2);
    ASSERT_FALSE(uint32ParamChoice2 < uint32ParamChoice1);

    const VariantA valueA = 99;
    uint32ParamChoice1.setValueA(valueA);
    ASSERT_FALSE(uint32ParamChoice1 < uint32ParamChoice2);
    ASSERT_TRUE(uint32ParamChoice2 < uint32ParamChoice1);

    uint32ParamChoice2.setValueA(valueA);
    ASSERT_FALSE(uint32ParamChoice1 < uint32ParamChoice2);
    ASSERT_FALSE(uint32ParamChoice2 < uint32ParamChoice1);

    const VariantA diffValueA = valueA + 1;
    uint32ParamChoice2.setValueA(diffValueA);
    ASSERT_TRUE(uint32ParamChoice1 < uint32ParamChoice2);
    ASSERT_FALSE(uint32ParamChoice2 < uint32ParamChoice1);
}

TEST_F(UInt32ParamChoiceTest, hashCode)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice1;
    uint32ParamChoice1.initialize(selector);
    UInt32ParamChoice uint32ParamChoice2;
    uint32ParamChoice2.initialize(selector);
    ASSERT_EQ(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    const VariantA valueA = 99;
    uint32ParamChoice1.setValueA(valueA);
    ASSERT_NE(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    uint32ParamChoice2.setValueA(valueA);
    ASSERT_EQ(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    uint32ParamChoice2.setValueA(diffValueA);
    ASSERT_NE(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31623, uint32ParamChoice1.hashCode());
    ASSERT_EQ(31624, uint32ParamChoice2.hashCode());
}

TEST_F(UInt32ParamChoiceTest, read)
{
    const uint32_t selector = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer(bitBuffer);
    const int8_t value = 99;
    writeUInt32ParamChoiceToByteArray(writer, selector, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selector);
    uint32ParamChoice.read(reader);

    ASSERT_EQ(selector, uint32ParamChoice.getSelector());
    ASSERT_EQ(value, uint32ParamChoice.getValueA());
}

TEST_F(UInt32ParamChoiceTest, write)
{
    const uint32_t selectorA = VARIANT_A_SELECTOR;
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(selectorA);

    const VariantA valueA = 99;
    uint32ParamChoice.setValueA(valueA);
    zserio::BitStreamWriter writerA(bitBuffer);
    uint32ParamChoice.write(writerA);

    zserio::BitStreamReader readerA(writerA.getWriteBuffer(), writerA.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceA(readerA, selectorA);
    ASSERT_EQ(valueA, readUInt32ParamChoiceA.getValueA());

    const uint32_t selectorB = VARIANT_B_SELECTOR2;
    uint32ParamChoice.initialize(selectorB);
    const VariantB valueB = 234;
    uint32ParamChoice.setValueB(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    uint32ParamChoice.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceB(readerB, selectorB);
    ASSERT_EQ(valueB, readUInt32ParamChoiceB.getValueB());

    const uint32_t selectorC = VARIANT_C_SELECTOR;
    uint32ParamChoice.initialize(selectorC);
    const VariantC valueC = 65535;
    uint32ParamChoice.setValueC(valueC);
    zserio::BitStreamWriter writerC(bitBuffer);
    uint32ParamChoice.write(writerC);

    zserio::BitStreamReader readerC(writerC.getWriteBuffer(), writerC.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceC(readerC, selectorC);
    ASSERT_EQ(valueC, readUInt32ParamChoiceC.getValueC());
}

} // namespace uint32_param_choice
} // namespace choice_types
