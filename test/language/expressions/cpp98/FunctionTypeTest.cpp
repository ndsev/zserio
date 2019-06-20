#include "math.h"

#include "gtest/gtest.h"

#include "expressions/function_type/FunctionTypeExpression.h"

namespace expressions
{
namespace function_type
{

TEST(FunctionTypeTest, bitSizeOfWithOptional)
{
    FunctionTypeExpression functionTypeExpression;
    functionTypeExpression.setColor(Color::RED);
    functionTypeExpression.setIsRedColorLight(true);

    const size_t functionTypeExpressionBitSizeWithOptional = 9;
    ASSERT_EQ(functionTypeExpressionBitSizeWithOptional, functionTypeExpression.bitSizeOf());
}

TEST(FunctionTypeTest, bitSizeOfWithoutOptional)
{
    FunctionTypeExpression functionTypeExpression;
    functionTypeExpression.setColor(Color::BLUE);

    const size_t functionTypeExpressionBitSizeWithoutOptional = 8;
    ASSERT_EQ(functionTypeExpressionBitSizeWithoutOptional, functionTypeExpression.bitSizeOf());
}

} // namespace function_type
} // namespace expressions
