#include "expressions/parenthesis/ParenthesisExpression.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace parenthesis
{

TEST(ParenthesisTest, firstValue)
{
    ParenthesisExpression parenthesisExpression;
    const uint8_t firstValue = 0x11;
    parenthesisExpression.setFirstValue(firstValue);
    const uint8_t secondValue = 0x22;
    parenthesisExpression.setSecondValue(secondValue);

    ASSERT_EQ(firstValue * (secondValue + 1), parenthesisExpression.funcResult());
}

} // namespace parenthesis
} // namespace expressions
