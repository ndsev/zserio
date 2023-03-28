#include "gtest/gtest.h"

#include "expressions/question_mark/QuestionMarkExpression.h"

namespace expressions
{
namespace question_mark
{

TEST(QuestionMarkTest, firstValue)
{
    QuestionMarkExpression questionMarkExpression;
    const uint8_t firstValue = 0x11;
    questionMarkExpression.setFirstValue(firstValue);
    questionMarkExpression.setSecondValue(0x22);
    questionMarkExpression.setIsFirstValueValid(true);

    ASSERT_EQ(firstValue, questionMarkExpression.funcValidValue());
}

TEST(QuestionMarkTest, secondValue)
{
    QuestionMarkExpression questionMarkExpression;
    questionMarkExpression.setFirstValue(0x11);
    const uint8_t secondValue = 0x22;
    questionMarkExpression.setSecondValue(secondValue);
    questionMarkExpression.setIsFirstValueValid(false);

    ASSERT_EQ(secondValue, questionMarkExpression.funcValidValue());
}

} // namespace question_mark
} // namespace expressions
