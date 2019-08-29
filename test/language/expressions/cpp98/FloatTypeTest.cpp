#include "math.h"

#include "gtest/gtest.h"

#include "expressions/float_type/FloatTypeExpression.h"

namespace expressions
{
namespace float_type
{

TEST(FloatTypeTest, result)
{
    FloatTypeExpression floatTypeExpression;
    const float floatValue = 15.0;
    floatTypeExpression.setFloatValue(floatValue);

    const bool result = (floatValue * 2.0 + 1.0 / 0.5 > 1.0);
    ASSERT_EQ(result, floatTypeExpression.funcResult());
}

} // namespace float_type
} // namespace expressions
