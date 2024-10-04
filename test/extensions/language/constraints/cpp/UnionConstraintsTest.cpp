#include "constraints/union_constraints/UnionConstraints.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace constraints
{
namespace union_constraints
{

class UnionConstraintsTest : public ::testing::Test
{
protected:
    void writeUnionConstraintsToByteArray(zserio::BitStreamWriter& writer, uint8_t value8)
    {
        writer.writeVarSize(static_cast<uint32_t>(UnionConstraints::CHOICE_value8));
        writer.writeBits(value8, 8);
    }

    void writeUnionConstraintsToByteArray(zserio::BitStreamWriter& writer, uint16_t value16)
    {
        writer.writeVarSize(static_cast<uint32_t>(UnionConstraints::CHOICE_value8));
        writer.writeBits(value16, 16);
    }

    static const uint8_t VALUE8_CORRECT_CONSTRAINT;
    static const uint8_t VALUE8_WRONG_CONSTRAINT;

    static const uint16_t VALUE16_CORRECT_CONSTRAINT;
    static const uint16_t VALUE16_WRONG_CONSTRAINT;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t UnionConstraintsTest::VALUE8_CORRECT_CONSTRAINT = 1;
const uint8_t UnionConstraintsTest::VALUE8_WRONG_CONSTRAINT = 0;

const uint16_t UnionConstraintsTest::VALUE16_CORRECT_CONSTRAINT = 256;
const uint16_t UnionConstraintsTest::VALUE16_WRONG_CONSTRAINT = 255;

TEST_F(UnionConstraintsTest, readConstructorCorrectConstraints)
{
    const uint8_t value8 = VALUE8_CORRECT_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeUnionConstraintsToByteArray(writer, value8);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    UnionConstraints unionConstraints(reader);

    ASSERT_EQ(UnionConstraints::CHOICE_value8, unionConstraints.choiceTag());
    ASSERT_EQ(value8, unionConstraints.getValue8());
}

TEST_F(UnionConstraintsTest, readConstructorWrongValue8Constraint)
{
    const uint8_t value8 = VALUE8_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeUnionConstraintsToByteArray(writer, value8);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(UnionConstraints unionConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, readConstructorWrongValue16Constraint)
{
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer(bitBuffer);
    writeUnionConstraintsToByteArray(writer, value16);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(UnionConstraints unionConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, writeCorrectConstraints)
{
    const uint16_t value16 = VALUE16_CORRECT_CONSTRAINT;
    UnionConstraints unionConstraints;
    unionConstraints.setValue16(value16);

    zserio::BitStreamWriter writer(bitBuffer);
    unionConstraints.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const UnionConstraints readUnionConstraints(reader);
    ASSERT_EQ(UnionConstraints::CHOICE_value16, readUnionConstraints.choiceTag());
    ASSERT_EQ(value16, readUnionConstraints.getValue16());
    ASSERT_TRUE(unionConstraints == readUnionConstraints);
}

TEST_F(UnionConstraintsTest, writeWrongValue8Constraint)
{
    const uint8_t value8 = VALUE8_WRONG_CONSTRAINT;
    UnionConstraints unionConstraints;
    unionConstraints.setValue8(value8);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(unionConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, writeWrongValue16Constraint)
{
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    UnionConstraints unionConstraints;
    unionConstraints.setValue16(value16);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(unionConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace union_constraints
} // namespace constraints
