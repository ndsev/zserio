#include "gtest/gtest.h"

#include "choice_types/function_selector_choice/TestChoice.h"

// just testChoice setters and getters
namespace choice_types
{
namespace function_selector_choice
{

class FunctionSelectorChoiceTest : public ::testing::Test
{
protected:
    struct SelectorHolder
    {
        SelectorHolder(int8_t numBits):
                selector(numBits)
        {}

        static Selector& getSelector(void* owner, size_t)
        {
            return static_cast<SelectorHolder*>(owner)->selector;
        }

        Selector selector;
        TestChoice::ParameterExpressions choiceParameterExpressions = { this, 0, getSelector };
    };
};

TEST_F(FunctionSelectorChoiceTest, field8)
{
    SelectorHolder selectorHolder(8);
    TestChoice testChoice;
    testChoice.initialize(selectorHolder.choiceParameterExpressions);
    testChoice.setField8(0x7F);
    ASSERT_EQ(0x7F, testChoice.getField8());
    ASSERT_EQ(8, testChoice.bitSizeOf());
}

TEST_F(FunctionSelectorChoiceTest, field16)
{
    SelectorHolder selectorHolder(16);
    TestChoice testChoice;
    testChoice.initialize(selectorHolder.choiceParameterExpressions);
    testChoice.setField16(0x7F7F);
    ASSERT_EQ(0x7F7F, testChoice.getField16());
    ASSERT_EQ(16, testChoice.bitSizeOf());
}

} // namespace function_selector_choice
} // namespace choice_types
