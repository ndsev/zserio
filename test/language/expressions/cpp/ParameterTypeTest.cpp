#include "math.h"

#include "gtest/gtest.h"

#include "expressions/parameter_type/ParameterTypeExpression.h"

namespace expressions
{
namespace parameter_type
{

TEST(ParameterTypeTest, bitSizeOfWithOptional)
{
    ParameterTypeExpression parameterTypeExpression;
    parameterTypeExpression.initialize(Color::RED);
    parameterTypeExpression.setValue(0x3F);
    parameterTypeExpression.setIsParameterRed(true);

    const size_t parameterTypeExpressionBitSizeWithOptional = 8;
    ASSERT_EQ(parameterTypeExpressionBitSizeWithOptional, parameterTypeExpression.bitSizeOf());
}

TEST(ParameterTypeTest, bitSizeOfWithoutOptional)
{
    ParameterTypeExpression parameterTypeExpression;
    parameterTypeExpression.initialize(Color::BLUE);
    parameterTypeExpression.setValue(0x3F);

    const size_t parameterTypeExpressionBitSizeWithoutOptional = 7;
    ASSERT_EQ(parameterTypeExpressionBitSizeWithoutOptional, parameterTypeExpression.bitSizeOf());
}

} // namespace parameter_type
} // namespace expressions
