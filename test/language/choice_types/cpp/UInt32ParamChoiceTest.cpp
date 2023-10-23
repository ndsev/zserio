#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/uint32_param_choice/UInt32ParamChoice.h"

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

    UInt32ParamChoice::ParameterExpressions parameterExpressionsA = {
            nullptr, 0, [](void*, size_t) { return VARIANT_A_SELECTOR; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsB1 = {
            nullptr, 0, [](void*, size_t) { return VARIANT_B_SELECTOR1; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsB2 = {
            nullptr, 0, [](void*, size_t) { return VARIANT_B_SELECTOR2; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsB3 = {
            nullptr, 0, [](void*, size_t) { return VARIANT_B_SELECTOR3; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsEmpty1 = {
            nullptr, 0, [](void*, size_t) { return EMPTY_SELECTOR1; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsEmpty2 = {
            nullptr, 0, [](void*, size_t) { return EMPTY_SELECTOR2; } };
    UInt32ParamChoice::ParameterExpressions parameterExpressionsC = {
            nullptr, 0, [](void*, size_t) { return VARIANT_C_SELECTOR; } };

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
    zserio::BitStreamWriter writer(bitBuffer);
    const int8_t value = 99;
    writeUInt32ParamChoiceToByteArray(writer, VARIANT_A_SELECTOR, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const UInt32ParamChoice uint32ParamChoice(reader, parameterExpressionsA);
    ASSERT_EQ(VARIANT_A_SELECTOR, uint32ParamChoice.getSelector());
    ASSERT_EQ(value, uint32ParamChoice.getA());
}

TEST_F(UInt32ParamChoiceTest, copyConstructor)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);

    const VariantA value = 99;
    uint32ParamChoice.setA(value);

    const UInt32ParamChoice uint32ParamChoiceCopy(uint32ParamChoice);
    ASSERT_EQ(VARIANT_A_SELECTOR, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getA());
}

TEST_F(UInt32ParamChoiceTest, assignmentOperator)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsB3);

    const VariantB value = 234;
    uint32ParamChoice.setB(value);

    UInt32ParamChoice uint32ParamChoiceCopy;
    uint32ParamChoiceCopy = uint32ParamChoice;
    ASSERT_EQ(VARIANT_B_SELECTOR3, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getB());
}

TEST_F(UInt32ParamChoiceTest, moveConstructor)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);

    const VariantA value = 99;
    uint32ParamChoice.setA(value);

    const UInt32ParamChoice uint32ParamChoiceMoved(std::move(uint32ParamChoice));
    ASSERT_EQ(VARIANT_A_SELECTOR, uint32ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceMoved.getA());
}

TEST_F(UInt32ParamChoiceTest, moveAssignmentOperator)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsB3);

    const VariantB value = 234;
    uint32ParamChoice.setB(value);

    UInt32ParamChoice uint32ParamChoiceMoved;
    uint32ParamChoiceMoved = std::move(uint32ParamChoice);
    ASSERT_EQ(VARIANT_B_SELECTOR3, uint32ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceMoved.getB());
}

TEST_F(UInt32ParamChoiceTest, propagateAllocatorCopyConstructor)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);

    const VariantA value = 99;
    uint32ParamChoice.setA(value);

    const UInt32ParamChoice uint32ParamChoiceCopy(zserio::PropagateAllocator, uint32ParamChoice,
            UInt32ParamChoice::allocator_type());
    ASSERT_EQ(VARIANT_A_SELECTOR, uint32ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint32ParamChoiceCopy.getA());
}

TEST_F(UInt32ParamChoiceTest, initialize)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsEmpty1);
    ASSERT_EQ(EMPTY_SELECTOR1, uint32ParamChoice.getSelector());
}

TEST_F(UInt32ParamChoiceTest, getSelector)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsEmpty2);
    ASSERT_EQ(EMPTY_SELECTOR2, uint32ParamChoice.getSelector());
}

TEST_F(UInt32ParamChoiceTest, getSetA)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);

    const VariantA value = 99;
    uint32ParamChoice.setA(value);
    ASSERT_EQ(value, uint32ParamChoice.getA());
}

TEST_F(UInt32ParamChoiceTest, getSetB)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsB2);

    const VariantB value = 234;
    uint32ParamChoice.setB(value);
    ASSERT_EQ(value, uint32ParamChoice.getB());
}

TEST_F(UInt32ParamChoiceTest, getSetC)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsC);

    const VariantC value = 65535;
    uint32ParamChoice.setC(value);
    ASSERT_EQ(value, uint32ParamChoice.getC());
}

TEST_F(UInt32ParamChoiceTest, choiceTag)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_a, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(parameterExpressionsB1);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_b, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(parameterExpressionsC);
    ASSERT_EQ(UInt32ParamChoice::CHOICE_c, uint32ParamChoice.choiceTag());

    uint32ParamChoice.initialize(parameterExpressionsEmpty1);
    ASSERT_EQ(UInt32ParamChoice::UNDEFINED_CHOICE, uint32ParamChoice.choiceTag());
}

TEST_F(UInt32ParamChoiceTest, bitSizeOf)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);
    const VariantA valueA = 99;
    uint32ParamChoice.setA(valueA);
    ASSERT_EQ(8, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(parameterExpressionsB2);
    const VariantB valueB = 234;
    uint32ParamChoice.setB(valueB);
    ASSERT_EQ(16, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(parameterExpressionsEmpty2);
    ASSERT_EQ(0, uint32ParamChoice.bitSizeOf());

    uint32ParamChoice.initialize(parameterExpressionsC);
    const VariantC valueC = 65535;
    uint32ParamChoice.setC(valueC);
    ASSERT_EQ(32, uint32ParamChoice.bitSizeOf());
}

TEST_F(UInt32ParamChoiceTest, initializeOffsets)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(parameterExpressionsB1);
    ASSERT_EQ(17, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(parameterExpressionsEmpty1);
    ASSERT_EQ(1, uint32ParamChoice.initializeOffsets(bitPosition));

    uint32ParamChoice.initialize(parameterExpressionsC);
    ASSERT_EQ(33, uint32ParamChoice.initializeOffsets(bitPosition));
}

TEST_F(UInt32ParamChoiceTest, operatorEquality)
{
    UInt32ParamChoice uint32ParamChoice1;
    uint32ParamChoice1.initialize(parameterExpressionsA);
    UInt32ParamChoice uint32ParamChoice2;
    uint32ParamChoice2.initialize(parameterExpressionsA);
    ASSERT_TRUE(uint32ParamChoice1 == uint32ParamChoice2);

    const VariantA valueA = 99;
    uint32ParamChoice1.setA(valueA);
    ASSERT_FALSE(uint32ParamChoice1 == uint32ParamChoice2);

    uint32ParamChoice2.setA(valueA);
    ASSERT_TRUE(uint32ParamChoice1 == uint32ParamChoice2);

    const VariantA diffValueA = valueA + 1;
    uint32ParamChoice2.setA(diffValueA);
    ASSERT_FALSE(uint32ParamChoice1 == uint32ParamChoice2);
}

TEST_F(UInt32ParamChoiceTest, hashCode)
{
    UInt32ParamChoice uint32ParamChoice1;
    uint32ParamChoice1.initialize(parameterExpressionsA);
    UInt32ParamChoice uint32ParamChoice2;
    uint32ParamChoice2.initialize(parameterExpressionsA);
    ASSERT_EQ(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    const VariantA valueA = 99;
    uint32ParamChoice1.setA(valueA);
    ASSERT_NE(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    uint32ParamChoice2.setA(valueA);
    ASSERT_EQ(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    const VariantA diffValueA = valueA + 1;
    uint32ParamChoice2.setA(diffValueA);
    ASSERT_NE(uint32ParamChoice1.hashCode(), uint32ParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31623, uint32ParamChoice1.hashCode());
    ASSERT_EQ(31624, uint32ParamChoice2.hashCode());
}

TEST_F(UInt32ParamChoiceTest, write)
{
    UInt32ParamChoice uint32ParamChoice;
    uint32ParamChoice.initialize(parameterExpressionsA);

    const VariantA valueA = 99;
    uint32ParamChoice.setA(valueA);
    zserio::BitStreamWriter writerA(bitBuffer);
    uint32ParamChoice.write(writerA);

    zserio::BitStreamReader readerA(writerA.getWriteBuffer(), writerA.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceA(readerA, parameterExpressionsA);
    ASSERT_EQ(valueA, readUInt32ParamChoiceA.getA());

    uint32ParamChoice.initialize(parameterExpressionsB2);
    const VariantB valueB = 234;
    uint32ParamChoice.setB(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    uint32ParamChoice.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceB(readerB, parameterExpressionsB2);
    ASSERT_EQ(valueB, readUInt32ParamChoiceB.getB());

    uint32ParamChoice.initialize(parameterExpressionsC);
    const VariantC valueC = 65535;
    uint32ParamChoice.setC(valueC);
    zserio::BitStreamWriter writerC(bitBuffer);
    uint32ParamChoice.write(writerC);

    zserio::BitStreamReader readerC(writerC.getWriteBuffer(), writerC.getBitPosition(), zserio::BitsTag());
    UInt32ParamChoice readUInt32ParamChoiceC(readerC, parameterExpressionsC);
    ASSERT_EQ(valueC, readUInt32ParamChoiceC.getC());
}

} // namespace uint32_param_choice
} // namespace choice_types
