#include "choice_types/expression_selector_choice/ExpressionSelectorChoice.h"
#include "gtest/gtest.h"

namespace choice_types
{
namespace expression_selector_choice
{

TEST(ExpressionSelectorChoiceTest, field8)
{
    const uint16_t selector = 0;
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(selector);
    const uint8_t value8 = 0x7F;
    expressionSelectorChoice.setField8(value8);
    ASSERT_EQ(value8, expressionSelectorChoice.getField8());
    ASSERT_EQ(8, expressionSelectorChoice.bitSizeOf());
}

TEST(ExpressionSelectorChoiceTest, field16)
{
    const uint16_t selector = 1;
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(selector);
    const uint16_t value16 = 0x7F7F;
    expressionSelectorChoice.setField16(value16);
    ASSERT_EQ(value16, expressionSelectorChoice.getField16());
    ASSERT_EQ(16, expressionSelectorChoice.bitSizeOf());
}

TEST(ExpressionSelectorChoiceTest, field32)
{
    const uint16_t selector = 2;
    ExpressionSelectorChoice expressionSelectorChoice;
    expressionSelectorChoice.initialize(selector);
    const uint32_t value32 = 0x7F7F7F7F;
    expressionSelectorChoice.setField32(value32);
    ASSERT_EQ(value32, expressionSelectorChoice.getField32());
    ASSERT_EQ(32, expressionSelectorChoice.bitSizeOf());
}

} // namespace expression_selector_choice
} // namespace choice_types
