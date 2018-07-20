#include "math.h"

#include "gtest/gtest.h"

#include "expressions/const_type/ConstTypeExpression.h"

namespace expressions
{
namespace const_type
{

TEST(ConstTypeTest, bitSizeOfWithOptional)
{
    ConstTypeExpression constTypeExpression;
    const uint8_t validValue = 0x01;
    const uint8_t additionalValue = 0x03;
    constTypeExpression.setValue(validValue);
    constTypeExpression.setAdditionalValue(additionalValue);

    const size_t constTypeExpressionBitSizeWithOptional = 10;
    ASSERT_EQ(constTypeExpressionBitSizeWithOptional, constTypeExpression.bitSizeOf());
}

TEST(ConstTypeTest, bitSizeOfWithoutOptional)
{
    ConstTypeExpression constTypeExpression;
    const uint8_t invalidValue = 0x00;
    constTypeExpression.setValue(invalidValue);

    const size_t constTypeExpressionBitSizeWithoutOptional = 7;
    ASSERT_EQ(constTypeExpressionBitSizeWithoutOptional, constTypeExpression.bitSizeOf());
}

} // namespace const_type
} // namespace expressions
