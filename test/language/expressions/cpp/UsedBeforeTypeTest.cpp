#include "math.h"

#include "gtest/gtest.h"

#include "expressions/used_before_type/UsedBeforeTypeExpression.h"

namespace expressions
{
namespace used_before_type
{

TEST(UsedBeforeTypeTest, bitSizeOfWithOptional)
{
    UsedBeforeTypeExpression usedBeforeTypeExpression;
    usedBeforeTypeExpression.setColor(Color::RED);
    usedBeforeTypeExpression.setIsRedColorLight(true);

    const size_t usedBeforeTypeExpressionBitSizeWithOptional = 8;
    ASSERT_EQ(usedBeforeTypeExpressionBitSizeWithOptional, usedBeforeTypeExpression.bitSizeOf());
}

TEST(UsedBeforeTypeTest, bitSizeOfWithoutOptional)
{
    UsedBeforeTypeExpression usedBeforeTypeExpression;
    usedBeforeTypeExpression.setColor(Color::BLUE);

    const size_t usedBeforeTypeExpressionBitSizeWithoutOptional = 7;
    ASSERT_EQ(usedBeforeTypeExpressionBitSizeWithoutOptional, usedBeforeTypeExpression.bitSizeOf());
}

} // namespace used_before_type
} // namespace expressions
