#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "constraints/union_constraints/UnionConstraints.h"

namespace constraints
{
namespace union_constraints
{

class UnionConstraintsTest : public ::testing::Test
{
protected:
    void writeUnionConstraintsToByteArray(zserio::BitStreamWriter& writer, uint8_t value8)
    {
        writer.writeVarUInt64(UnionConstraints::CHOICE_value8);
        writer.writeBits(value8, 8);
    }

    void writeUnionConstraintsToByteArray(zserio::BitStreamWriter& writer, uint16_t value16)
    {
        writer.writeVarUInt64(UnionConstraints::CHOICE_value8);
        writer.writeBits(value16, 16);
    }

    static const uint8_t VALUE8_CORRECT_CONSTRAINT;
    static const uint8_t VALUE8_WRONG_CONSTRAINT;

    static const uint16_t VALUE16_CORRECT_CONSTRAINT;
    static const uint16_t VALUE16_WRONG_CONSTRAINT;
};

const uint8_t UnionConstraintsTest::VALUE8_CORRECT_CONSTRAINT = 1;
const uint8_t UnionConstraintsTest::VALUE8_WRONG_CONSTRAINT = 0;

const uint16_t UnionConstraintsTest::VALUE16_CORRECT_CONSTRAINT = 256;
const uint16_t UnionConstraintsTest::VALUE16_WRONG_CONSTRAINT = 255;

TEST_F(UnionConstraintsTest, readCorrectConstraints)
{
    const uint8_t value8 = VALUE8_CORRECT_CONSTRAINT;
    zserio::BitStreamWriter writer;
    writeUnionConstraintsToByteArray(writer, value8);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    UnionConstraints unionConstraints;
    unionConstraints.read(reader);

    ASSERT_EQ(UnionConstraints::CHOICE_value8, unionConstraints.choiceTag());
    ASSERT_EQ(value8, unionConstraints.getValue8());
}

TEST_F(UnionConstraintsTest, readWrongValue8Constraint)
{
    const uint8_t value8 = VALUE8_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer;
    writeUnionConstraintsToByteArray(writer, value8);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    UnionConstraints unionConstraints;
    ASSERT_THROW(unionConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, readWrongValue16Constraint)
{
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    zserio::BitStreamWriter writer;
    writeUnionConstraintsToByteArray(writer, value16);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    UnionConstraints unionConstraints;
    ASSERT_THROW(unionConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, writeCorrectConstraints)
{
    const uint16_t value16 = VALUE16_CORRECT_CONSTRAINT;
    UnionConstraints unionConstraints;
    unionConstraints.setValue16(value16);

    zserio::BitStreamWriter writer;
    unionConstraints.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
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

    zserio::BitStreamWriter writer;
    ASSERT_THROW(unionConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(UnionConstraintsTest, writeWrongValue16Constraint)
{
    const uint16_t value16 = VALUE16_WRONG_CONSTRAINT;
    UnionConstraints unionConstraints;
    unionConstraints.setValue16(value16);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(unionConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace union_constraints
} // namespace constraints
