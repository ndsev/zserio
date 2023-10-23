#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "choice_types/default_empty_choice/DefaultEmptyChoice.h"

namespace choice_types
{
namespace default_empty_choice
{

class DefaultEmptyChoiceTest : public ::testing::Test
{
protected:
    void writeDefaultEmptyChoiceToByteArray(zserio::BitStreamWriter& writer, uint8_t tag, int32_t value)
    {
        switch (tag)
        {
        case 1:
            writer.writeSignedBits(value, 8);
            break;

        case 2:
            writer.writeSignedBits(value, 16);
            break;

        default:
            break;
        }
    }

    static const uint8_t VARIANT_A_SELECTOR;
    static const uint8_t VARIANT_B_SELECTOR;
    static const uint8_t DEFAULT_SELECTOR;

    DefaultEmptyChoice::ParameterExpressions parameterExpressionsVariantA = {
            nullptr, 0, [](void*, size_t) { return VARIANT_A_SELECTOR; } };
    DefaultEmptyChoice::ParameterExpressions parameterExpressionsVariantB = {
            nullptr, 0, [](void*, size_t) { return VARIANT_B_SELECTOR; } };
    DefaultEmptyChoice::ParameterExpressions parameterExpressionsDefault = {
            nullptr, 0, [](void*, size_t) { return DEFAULT_SELECTOR; } };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t DefaultEmptyChoiceTest::VARIANT_A_SELECTOR = 1;
const uint8_t DefaultEmptyChoiceTest::VARIANT_B_SELECTOR = 2;
const uint8_t DefaultEmptyChoiceTest::DEFAULT_SELECTOR = 3;

TEST_F(DefaultEmptyChoiceTest, bitStreamReaderConstructor)
{
    const uint8_t tag = VARIANT_A_SELECTOR;
    zserio::BitStreamWriter writer(bitBuffer);
    const int8_t value = 99;
    writeDefaultEmptyChoiceToByteArray(writer, tag, value);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const DefaultEmptyChoice defaultEmptyChoice(reader, parameterExpressionsVariantA);
    ASSERT_EQ(tag, defaultEmptyChoice.getTag());
    ASSERT_EQ(value, defaultEmptyChoice.getA());
}

TEST_F(DefaultEmptyChoiceTest, choiceTag)
{
    DefaultEmptyChoice defaultEmptyChoice;
    defaultEmptyChoice.initialize(parameterExpressionsVariantA);
    ASSERT_EQ(DefaultEmptyChoice::CHOICE_a, defaultEmptyChoice.choiceTag());

    defaultEmptyChoice.initialize(parameterExpressionsVariantB);
    ASSERT_EQ(DefaultEmptyChoice::CHOICE_b, defaultEmptyChoice.choiceTag());

    defaultEmptyChoice.initialize(parameterExpressionsDefault);
    ASSERT_EQ(DefaultEmptyChoice::UNDEFINED_CHOICE, defaultEmptyChoice.choiceTag());
}

TEST_F(DefaultEmptyChoiceTest, write)
{
    DefaultEmptyChoice defaultEmptyChoice;
    defaultEmptyChoice.initialize(parameterExpressionsVariantA);

    const VariantA valueA = 99;
    defaultEmptyChoice.setA(valueA);
    zserio::BitStreamWriter writerA(bitBuffer);
    defaultEmptyChoice.write(writerA);

    zserio::BitStreamReader readerA(writerA.getWriteBuffer(), writerA.getBitPosition(), zserio::BitsTag());
    DefaultEmptyChoice readDefaultEmptyChoiceA(readerA, parameterExpressionsVariantA);
    ASSERT_EQ(valueA, readDefaultEmptyChoiceA.getA());

    defaultEmptyChoice.initialize(parameterExpressionsVariantB);
    const VariantB valueB = 234;
    defaultEmptyChoice.setB(valueB);
    zserio::BitStreamWriter writerB(bitBuffer);
    defaultEmptyChoice.write(writerB);

    zserio::BitStreamReader readerB(writerB.getWriteBuffer(), writerB.getBitPosition(), zserio::BitsTag());
    DefaultEmptyChoice readDefaultEmptyChoiceB(readerB, parameterExpressionsVariantB);
    ASSERT_EQ(valueB, readDefaultEmptyChoiceB.getB());

    defaultEmptyChoice.initialize(parameterExpressionsDefault);
    zserio::BitStreamWriter writerDefault(bitBuffer);
    defaultEmptyChoice.write(writerDefault);

    zserio::BitStreamReader readerDefault(writerDefault.getWriteBuffer(),
            writerDefault.getBitPosition(), zserio::BitsTag());
    DefaultEmptyChoice readDefaultEmptyChoiceDefault(readerDefault, parameterExpressionsDefault);
    ASSERT_EQ(DEFAULT_SELECTOR, readDefaultEmptyChoiceDefault.getTag());
}

} // namespace default_empty_choice
} // namespace choice_types
