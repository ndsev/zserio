#include "constraints/literal_constraint/LiteralConstraint.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/RebindAlloc.h"

namespace constraints
{
namespace literal_constraint
{

using allocator_type = LiteralConstraint::allocator_type;

class LiteralConstraintTest : public ::testing::Test
{
protected:
    void writeLiteralConstraintToByteArray(zserio::BitStreamWriter& writer, int32_t value)
    {
        writer.writeSignedBits(value, 32);
    }

    static const int32_t CORRECT_VALUE = 6;
    static const int32_t WRONG_VALUE_ZERO = 0;
    static const int32_t WRONG_VALUE_LESS = -268435456;
    static const int32_t WRONG_VALUE_GREATER = 268435456;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const int32_t LiteralConstraintTest::CORRECT_VALUE;
const int32_t LiteralConstraintTest::WRONG_VALUE_ZERO;
const int32_t LiteralConstraintTest::WRONG_VALUE_LESS;
const int32_t LiteralConstraintTest::WRONG_VALUE_GREATER;

TEST_F(LiteralConstraintTest, readConstructorCorrectValue)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeLiteralConstraintToByteArray(writer, CORRECT_VALUE);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    LiteralConstraint literalConstraint(reader);
    ASSERT_EQ(CORRECT_VALUE, literalConstraint.getValue());
}

TEST_F(LiteralConstraintTest, readConstructorWrongValueZero)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeLiteralConstraintToByteArray(writer, WRONG_VALUE_ZERO);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(LiteralConstraint literalConstraint(reader), zserio::CppRuntimeException);
}

TEST_F(LiteralConstraintTest, readConstructorWrongValueLess)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeLiteralConstraintToByteArray(writer, WRONG_VALUE_LESS);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(LiteralConstraint literalConstraint(reader), zserio::CppRuntimeException);
}

TEST_F(LiteralConstraintTest, readConstructorWrongValueGreater)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeLiteralConstraintToByteArray(writer, WRONG_VALUE_GREATER);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(LiteralConstraint literalConstraint(reader), zserio::CppRuntimeException);
}

TEST_F(LiteralConstraintTest, writeCorrectValue)
{
    LiteralConstraint literalConstraint(CORRECT_VALUE);
    zserio::BitStreamWriter writer(bitBuffer);
    literalConstraint.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    LiteralConstraint readLiteralConstraint(reader);
    ASSERT_EQ(CORRECT_VALUE, readLiteralConstraint.getValue());
    ASSERT_TRUE(literalConstraint == readLiteralConstraint);
}

TEST_F(LiteralConstraintTest, writeWrongValueZero)
{
    LiteralConstraint literalConstraint(WRONG_VALUE_ZERO);
    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(literalConstraint.write(writer), zserio::CppRuntimeException);
}

TEST_F(LiteralConstraintTest, writeWrongValueLess)
{
    LiteralConstraint literalConstraint(WRONG_VALUE_LESS);
    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(literalConstraint.write(writer), zserio::CppRuntimeException);
}

TEST_F(LiteralConstraintTest, writeWrongValueGreater)
{
    LiteralConstraint literalConstraint(WRONG_VALUE_GREATER);
    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(literalConstraint.write(writer), zserio::CppRuntimeException);
}

} // namespace literal_constraint
} // namespace constraints
