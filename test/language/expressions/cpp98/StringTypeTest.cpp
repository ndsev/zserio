#include "math.h"

#include "gtest/gtest.h"

#include "expressions/string_type/StringTypeExpression.h"

namespace expressions
{
namespace string_type
{

TEST(StringTypeTest, append)
{
    StringTypeExpression stringTypeExpression;
    const std::string firstValue = "first";
    stringTypeExpression.setFirstValue(firstValue);
    const std::string secondValue = "second";
    stringTypeExpression.setSecondValue(secondValue);

    ASSERT_EQ(firstValue + secondValue + "_appendix", stringTypeExpression.funcAppend());
}

} // namespace string_type
} // namespace expressions
