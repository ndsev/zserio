#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"

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

    static const std::string BLOB_NAME_BASE;
    static const uint64_t VARIANT_A_SELECTOR;
    static const uint64_t VARIANT_B_SELECTOR;
    static const uint64_t VARIANT_C_SELECTOR;
    static const uint64_t EMPTY_SELECTOR;

    UInt64ParamChoice::ParameterExpressions parameterExpressionsA = {
            nullptr, 0, [](void*, size_t) { return VARIANT_A_SELECTOR; } };
    UInt64ParamChoice::ParameterExpressions parameterExpressionsB = {
            nullptr, 0, [](void*, size_t) { return VARIANT_B_SELECTOR; } };
    UInt64ParamChoice::ParameterExpressions parameterExpressionsC = {
            nullptr, 0, [](void*, size_t) { return VARIANT_C_SELECTOR; } };
    UInt64ParamChoice::ParameterExpressions parameterExpressionsEmpty = {
            nullptr, 0, [](void*, size_t) { return EMPTY_SELECTOR; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string UInt64ParamChoiceTest::BLOB_NAME_BASE = "language/choice_types/uint64_param_choice_";
const uint64_t UInt64ParamChoiceTest::VARIANT_A_SELECTOR = 1;
const uint64_t UInt64ParamChoiceTest::VARIANT_B_SELECTOR = 2;
const uint64_t UInt64ParamChoiceTest::VARIANT_C_SELECTOR = 7;
const uint64_t UInt64ParamChoiceTest::EMPTY_SELECTOR = 5;

TEST_F(UInt64ParamChoiceTest, emptyConstructor)
{
    {
        UInt64ParamChoice uint64ParamChoice;
        ASSERT_THROW(uint64ParamChoice.getSelector(), zserio::CppRuntimeException);
    }
    {
        UInt64ParamChoice uint64ParamChoice = {};
        ASSERT_THROW(uint64ParamChoice.getSelector(), zserio::CppRuntimeException);
    }
}

TEST_F(UInt64ParamChoiceTest, bitStreamReaderConstructor)
{
    const int8_t value = 99;
    zserio::BitStreamWriter writer(bitBuffer);
    writeUInt64ParamChoiceToByteArray(writer, VARIANT_A_SELECTOR, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UInt64ParamChoice uint64ParamChoice(reader, parameterExpressionsA);
    ASSERT_EQ(VARIANT_A_SELECTOR, uint64ParamChoice.getSelector());
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, copyConstructor)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);

    const UInt64ParamChoice uint64ParamChoiceCopy(uint64ParamChoice);
    ASSERT_EQ(VARIANT_A_SELECTOR, uint64ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceCopy.getA());
}

TEST_F(UInt64ParamChoiceTest, assignmentOperator)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsB);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);

    UInt64ParamChoice uint64ParamChoiceCopy;
    uint64ParamChoiceCopy = uint64ParamChoice;
    ASSERT_EQ(VARIANT_B_SELECTOR, uint64ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceCopy.getB());
}

TEST_F(UInt64ParamChoiceTest, moveConstructor)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);

    const UInt64ParamChoice uint64ParamChoiceMoved(std::move(uint64ParamChoice));
    ASSERT_EQ(VARIANT_A_SELECTOR, uint64ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceMoved.getA());
}

TEST_F(UInt64ParamChoiceTest, moveAssignmentOperator)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsB);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);

    UInt64ParamChoice uint64ParamChoiceMoved;
    uint64ParamChoiceMoved = std::move(uint64ParamChoice);
    ASSERT_EQ(VARIANT_B_SELECTOR, uint64ParamChoiceMoved.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceMoved.getB());
}

TEST_F(UInt64ParamChoiceTest, propagateAllocatorCopyConstructor)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);

    const UInt64ParamChoice uint64ParamChoiceCopy(zserio::PropagateAllocator, uint64ParamChoice,
            UInt64ParamChoice::allocator_type());
    ASSERT_EQ(VARIANT_A_SELECTOR, uint64ParamChoiceCopy.getSelector());
    ASSERT_EQ(value, uint64ParamChoiceCopy.getA());
}

TEST_F(UInt64ParamChoiceTest, initialize)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsB);
    ASSERT_EQ(VARIANT_B_SELECTOR, uint64ParamChoice.getSelector());
}

TEST_F(UInt64ParamChoiceTest, getSelector)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    ASSERT_EQ(VARIANT_A_SELECTOR, uint64ParamChoice.getSelector());
}

TEST_F(UInt64ParamChoiceTest, getSetA)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    const int8_t value = 99;
    uint64ParamChoice.setA(value);
    ASSERT_EQ(value, uint64ParamChoice.getA());
}

TEST_F(UInt64ParamChoiceTest, getSetB)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsB);
    const int16_t value = 234;
    uint64ParamChoice.setB(value);
    ASSERT_EQ(value, uint64ParamChoice.getB());
}

TEST_F(UInt64ParamChoiceTest, getSetC)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsC);
    const int32_t value = 23456;
    uint64ParamChoice.setC(value);
    ASSERT_EQ(value, uint64ParamChoice.getC());
}

TEST_F(UInt64ParamChoiceTest, choiceTag)
{
    UInt64ParamChoice uint64ParamChoice;
    uint64ParamChoice.initialize(parameterExpressionsA);
    ASSERT_EQ(UInt64ParamChoice::CHOICE_a, uint64ParamChoice.choiceTag());

    uint64ParamChoice.initialize(parameterExpressionsB);
    ASSERT_EQ(UInt64ParamChoice::CHOICE_b, uint64ParamChoice.choiceTag());

    uint64ParamChoice.initialize(parameterExpressionsC);
    ASSERT_EQ(UInt64ParamChoice::CHOICE_c, uint64ParamChoice.choiceTag());

    uint64ParamChoice.initialize(parameterExpressionsEmpty);
    ASSERT_EQ(UInt64ParamChoice::UNDEFINED_CHOICE, uint64ParamChoice.choiceTag());
}

TEST_F(UInt64ParamChoiceTest, bitSizeOf)
{
    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(parameterExpressionsA);
    ASSERT_EQ(8, uint64ParamChoiceB.bitSizeOf());

    UInt64ParamChoice uint64ParamChoiceG;
    uint64ParamChoiceG.initialize(parameterExpressionsB);
    ASSERT_EQ(16, uint64ParamChoiceG.bitSizeOf());
}

TEST_F(UInt64ParamChoiceTest, initializeOffsets)
{
    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(parameterExpressionsA);
    const size_t bitPosition = 1;
    ASSERT_EQ(9, uint64ParamChoiceB.initializeOffsets(bitPosition));

    UInt64ParamChoice uint64ParamChoiceG;
    uint64ParamChoiceG.initialize(parameterExpressionsB);
    ASSERT_EQ(17, uint64ParamChoiceG.initializeOffsets(bitPosition));
}

TEST_F(UInt64ParamChoiceTest, operatorEquality)
{
    UInt64ParamChoice uint64ParamChoice1;
    uint64ParamChoice1.initialize(parameterExpressionsA);
    UInt64ParamChoice uint64ParamChoice2;
    uint64ParamChoice2.initialize(parameterExpressionsA);
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
    uint64ParamChoice1.initialize(parameterExpressionsA);
    UInt64ParamChoice uint64ParamChoice2;
    uint64ParamChoice2.initialize(parameterExpressionsA);
    ASSERT_EQ(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    const int8_t value = 99;
    uint64ParamChoice1.setA(value);
    ASSERT_NE(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    uint64ParamChoice2.setA(value);
    ASSERT_EQ(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    const int8_t diffValue = value + 1;
    uint64ParamChoice2.setA(diffValue);
    ASSERT_NE(uint64ParamChoice1.hashCode(), uint64ParamChoice2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(31623, uint64ParamChoice1.hashCode());
    ASSERT_EQ(31624, uint64ParamChoice2.hashCode());
}

TEST_F(UInt64ParamChoiceTest, writeRead)
{
    UInt64ParamChoice uint64ParamChoiceA;
    uint64ParamChoiceA.initialize(parameterExpressionsA);
    const int8_t valueA = 99;
    uint64ParamChoiceA.setA(valueA);
    zserio::BitStreamWriter writerA(bitBuffer);
    uint64ParamChoiceA.write(writerA);

    zserio::BitStreamReader readerA(writerA.getWriteBuffer(), writerA.getBitPosition(), zserio::BitsTag());
    UInt64ParamChoice readUInt64ParamChoiceA(readerA, parameterExpressionsA);
    ASSERT_EQ(valueA, readUInt64ParamChoiceA.getA());

    UInt64ParamChoice uint64ParamChoiceB;
    uint64ParamChoiceB.initialize(parameterExpressionsB);
    const int16_t valueB = 234;
    uint64ParamChoiceB.setB(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    uint64ParamChoiceB.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    UInt64ParamChoice readUInt64ParamChoiceB(readerB, parameterExpressionsB);
    ASSERT_EQ(valueB, readUInt64ParamChoiceB.getB());
}

TEST_F(UInt64ParamChoiceTest, writeReadFile)
{
    UInt64ParamChoice uint64ParamChoiceA;
    const int8_t valueA = 99;
    uint64ParamChoiceA.setA(valueA);
    const std::string fileNameA = BLOB_NAME_BASE + "a.blob";
    zserio::serializeToFile(uint64ParamChoiceA, fileNameA, parameterExpressionsA);

    const UInt64ParamChoice readUInt64ParamChoiceA =
            zserio::deserializeFromFile<UInt64ParamChoice>(fileNameA, parameterExpressionsA);
    ASSERT_EQ(valueA, readUInt64ParamChoiceA.getA());

    UInt64ParamChoice uint64ParamChoiceB;
    const int16_t valueB = 234;
    uint64ParamChoiceB.setB(valueB);
    const std::string fileNameB = BLOB_NAME_BASE + "b.blob";
    zserio::serializeToFile(uint64ParamChoiceB, fileNameB, parameterExpressionsB);

    const UInt64ParamChoice readUInt64ParamChoiceB =
            zserio::deserializeFromFile<UInt64ParamChoice>(fileNameB, parameterExpressionsB);
    ASSERT_EQ(valueB, readUInt64ParamChoiceB.getB());
}

} // namespace uint64_param_choice
} // namespace choice_types
