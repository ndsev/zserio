#include "gtest/gtest.h"

#include "choice_types/function_returning_literal_selector_choice/TestChoice.h"

// just testChoice setters and getters
namespace choice_types
{
namespace function_returning_literal_selector_choice
{

class FunctionReturningLiteralSelectorChoiceTest : public ::testing::Test
{
protected:
    struct SelectorHolder
    {
        SelectorHolder(Selector::ParameterExpressions selectorParameterExpressions)
        {
            selector.initialize(selectorParameterExpressions);
            choiceParameterExpressions ={ this, 0, getSelector };
        }

        static Selector& getSelector(void* owner, size_t)
        {
            return static_cast<SelectorHolder*>(owner)->selector;
        }

        Selector selector;
        TestChoice::ParameterExpressions choiceParameterExpressions;
    };
};

TEST_F(FunctionReturningLiteralSelectorChoiceTest, field8)
{
    SelectorHolder selectorHolder({ nullptr, 0, [](void*, size_t) { return false; } });
    TestChoice testChoice;
    testChoice.initialize(selectorHolder.choiceParameterExpressions);
    testChoice.setField8(0x7F);
    ASSERT_EQ(0x7F, testChoice.getField8());
    ASSERT_EQ(8, testChoice.bitSizeOf());
}

TEST_F(FunctionReturningLiteralSelectorChoiceTest, field16)
{
    SelectorHolder selectorHolder({ nullptr, 0, [](void*, size_t) { return true; } });
    TestChoice testChoice;
    testChoice.initialize(selectorHolder.choiceParameterExpressions);
    testChoice.setField16(0x7F7F);
    ASSERT_EQ(0x7F7F, testChoice.getField16());
    ASSERT_EQ(16, testChoice.bitSizeOf());
}

} // namespace function_returning_literal_selector_choice
} // namespace choice_types
