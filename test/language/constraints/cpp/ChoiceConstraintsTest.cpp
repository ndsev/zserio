#include "constraints/choice_constraints/ChoiceConstraints.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace constraints
{
namespace choice_constraints
{

class ChoiceConstraintsTest : public ::testing::Test
{
protected:
    void writeChoiceConstraintsToByteArray(
            zserio::BitStreamWriter& writer, bool selector, uint8_t value8, uint16_t value16)
    {
        if (selector)
            writer.writeBits(value8, 8);
        else
            writer.writeBits(value16, 16);
    }

    static const uint8_t VALUE8_CORRECT_CONSTRAINT;
    static const uint8_t VALUE8_WRONG_CONSTRAINT;

    static const uint16_t VALUE16_CORRECT_CONSTRAINT;
    static const uint16_t VALUE16_WRONG_CONSTRAINT;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t ChoiceConstraintsTest::VALUE8_CORRECT_CONSTRAINT = 1;
const uint8_t ChoiceConstraintsTest::VALUE8_WRONG_CONSTRAINT = 0;

const uint16_t ChoiceConstraintsTest::VALUE16_CORRECT_CONSTRAINT = 256;
const uint16_t ChoiceConstraintsTest::VALUE16_WRONG_CONSTRAINT = 255;

TEST_F(ChoiceConstraintsTest, readConstructorCorrectConstraints)
{
    const bool selector = true;
    const uint8_t value8 = VALUE8_CORRECT_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeChoiceConstraintsToByteArray(writer, selector, value8, 0);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ChoiceConstraints choiceConstraints(reader, selector);

    ASSERT_EQ(selector, choiceConstraints.getSelector());
    ASSERT_EQ(value8, choiceConstraints.getValue8());
}

TEST_F(ChoiceConstraintsTest, readConstructorWrongValue8Constraint)
{
    const bool selector = true;
    const uint8_t value8 = VALUE8_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeChoiceConstraintsToByteArray(writer, selector, value8, 0);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(ChoiceConstraints choiceConstraints(reader, selector), zserio::CppRuntimeException);
}

TEST_F(ChoiceConstraintsTest, readConstructorWrongValue16Constraint)
{
    const bool selector = false;
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeChoiceConstraintsToByteArray(writer, selector, 0, value16);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(ChoiceConstraints choiceConstraints(reader, selector), zserio::CppRuntimeException);
}

TEST_F(ChoiceConstraintsTest, writeCorrectConstraints)
{
    const bool selector = false;
    const uint16_t value16 = VALUE16_CORRECT_CONSTRAINT;
    ChoiceConstraints choiceConstraints;
    choiceConstraints.initialize(selector);
    choiceConstraints.setValue16(value16);

    zserio::BitStreamWriter writer(bitBuffer);
    choiceConstraints.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const ChoiceConstraints readChoiceConstraints(reader, selector);
    ASSERT_EQ(selector, readChoiceConstraints.getSelector());
    ASSERT_EQ(value16, readChoiceConstraints.getValue16());
    ASSERT_TRUE(choiceConstraints == readChoiceConstraints);
}

TEST_F(ChoiceConstraintsTest, writeWrongValue8Constraint)
{
    const bool selector = true;
    const uint8_t value8 = VALUE8_WRONG_CONSTRAINT;
    ChoiceConstraints choiceConstraints;
    choiceConstraints.initialize(selector);
    choiceConstraints.setValue8(value8);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(choiceConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(ChoiceConstraintsTest, writeWrongValue16Constraint)
{
    const bool selector = false;
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    ChoiceConstraints choiceConstraints;
    choiceConstraints.initialize(selector);
    choiceConstraints.setValue16(value16);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(choiceConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace choice_constraints
} // namespace constraints
