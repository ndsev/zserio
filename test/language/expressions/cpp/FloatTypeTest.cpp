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

    const bool result = (floatValue * 2.0f + 1.0f / 0.5f > 1.0f);
    ASSERT_EQ(result, floatTypeExpression.funcResult());
}

} // namespace float_type
} // namespace expressions
