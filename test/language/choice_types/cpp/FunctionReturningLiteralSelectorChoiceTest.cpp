#include "choice_types/function_returning_literal_selector_choice/TestChoice.h"
#include "gtest/gtest.h"

// just testChoice setters and getters
namespace choice_types
{
namespace function_returning_literal_selector_choice
{

TEST(FunctionReturningLiteralSelectorChoiceTest, field8)
{
    Selector selector;
    selector.initialize(false);
    TestChoice testChoice;
    testChoice.initialize(selector);
    testChoice.setField8(0x7F);
    ASSERT_EQ(0x7F, testChoice.getField8());
    ASSERT_EQ(8, testChoice.bitSizeOf());
}

TEST(FunctionReturningLiteralSelectorChoiceTest, field16)
{
    Selector selector;
    selector.initialize(true);
    TestChoice testChoice;
    testChoice.initialize(selector);
    testChoice.setField16(0x7F7F);
    ASSERT_EQ(0x7F7F, testChoice.getField16());
    ASSERT_EQ(16, testChoice.bitSizeOf());
}

} // namespace function_returning_literal_selector_choice
} // namespace choice_types
