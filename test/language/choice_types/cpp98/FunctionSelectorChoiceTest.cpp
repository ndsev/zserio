#include "gtest/gtest.h"

#include "choice_types/function_selector_choice/TestChoice.h"

// just testChoice setters and getters
namespace choice_types
{
namespace function_selector_choice
{

TEST(FunctionSelectorChoiceTest, field8)
{
    Selector selector;
    selector.setNumBits(8);
    TestChoice testChoice;
    testChoice.initialize(selector);
    testChoice.setField8(0x7F);
    ASSERT_EQ(0x7F, testChoice.getField8());
    ASSERT_EQ(8, testChoice.bitSizeOf());
}

TEST(FunctionSelectorChoiceTest, field16)
{
    Selector selector;
    selector.setNumBits(16);
    TestChoice testChoice;
    testChoice.initialize(selector);
    testChoice.setField16(0x7F7F);
    ASSERT_EQ(0x7F7F, testChoice.getField16());
    ASSERT_EQ(16, testChoice.bitSizeOf());
}

} // namespace function_selector_choice
} // namespace choice_types
