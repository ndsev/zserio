package compound_selector_error;

struct CompoundSelector
{
    int32 value;
};

choice CompoundSelectorChoice(CompoundSelector selector) on selector
{
    default:
        int8 defaultChoice;
};
