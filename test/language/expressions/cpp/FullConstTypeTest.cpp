#include "gtest/gtest.h"

#include "expressions/full_const_type/FullConstTypeExpression.h"

namespace expressions
{
namespace full_const_type
{

TEST(FullConstTypeTest, bitSizeOfWithOptional)
{
    FullConstTypeExpression fullConstTypeExpression;
    const uint8_t fullValidValue = 0x01;
    const uint8_t additionalValue = 0x03;
    fullConstTypeExpression.setValue(fullValidValue);
    fullConstTypeExpression.setAdditionalValue(additionalValue);

    const size_t fullConstTypeExpressionBitSizeWithOptional = 10;
    ASSERT_EQ(fullConstTypeExpressionBitSizeWithOptional, fullConstTypeExpression.bitSizeOf());
}

TEST(FullConstTypeTest, bitSizeOfWithoutOptional)
{
    FullConstTypeExpression fullConstTypeExpression;
    const uint8_t fullInvalidValue = 0x00;
    fullConstTypeExpression.setValue(fullInvalidValue);

    const size_t fullConstTypeExpressionBitSizeWithoutOptional = 7;
    ASSERT_EQ(fullConstTypeExpressionBitSizeWithoutOptional, fullConstTypeExpression.bitSizeOf());
}

} // namespace full_const_type
} // namespace expressions
