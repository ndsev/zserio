#include "gtest/gtest.h"

#include "choice_types/expression_selector_choice/ExpressionSelectorChoice.h"

namespace choice_types
{
namespace expression_selector_choice
{

class ExpressionSelectorChoiceTest : public ::testing::Test
{
protected:
    ExpressionSelectorChoice::ParameterExpressions parameterExpressions0 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint16_t>(0); } };
    ExpressionSelectorChoice::ParameterExpressions parameterExpressions1 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint16_t>(1); } };
    ExpressionSelectorChoice::ParameterExpressions parameterExpressions2 = {
            nullptr, 0, [](void*, size_t) { return static_cast<uint16_t>(2); } };
};

TEST_F(ExpressionSelectorChoiceTest, field8)
{
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(parameterExpressions0);
    const uint8_t value8 = 0x7F;
    expressionSelectorChoice.setField8(value8);
    ASSERT_EQ(value8, expressionSelectorChoice.getField8());
    ASSERT_EQ(8, expressionSelectorChoice.bitSizeOf());
}

TEST_F(ExpressionSelectorChoiceTest, field16)
{
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(parameterExpressions1);
    const uint16_t value16 = 0x7F7F;
    expressionSelectorChoice.setField16(value16);
    ASSERT_EQ(value16, expressionSelectorChoice.getField16());
    ASSERT_EQ(16, expressionSelectorChoice.bitSizeOf());
}

TEST_F(ExpressionSelectorChoiceTest, field32)
{
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(parameterExpressions2);
    const uint32_t value32 = 0x7F7F7F7F;
    expressionSelectorChoice.setField32(value32);
    ASSERT_EQ(value32, expressionSelectorChoice.getField32());
    ASSERT_EQ(32, expressionSelectorChoice.bitSizeOf());
}

} // namespace expression_selector_choice
} // namespace choice_types
