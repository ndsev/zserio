#include "gtest/gtest.h"

#include "expressions/negation_operator/NegationOperatorExpression.h"

namespace expressions
{
namespace negation_operator
{

TEST(NumBitsOperatorTest, NegatedValue)
{
    NegationOperatorExpression negationOperatorExpression;
    negationOperatorExpression.setValue(true);
    ASSERT_FALSE(negationOperatorExpression.funcNegatedValue());

    negationOperatorExpression.setValue(false);
    ASSERT_TRUE(negationOperatorExpression.funcNegatedValue());
}

} // namespace negation_operator
} // namespace expressions
