package choice_types.empty_choice_with_default;

choice EmptyChoiceWithDefault(uint8 selector) on selector
{
    default:
        ; // no fields to ensure that the generated code is valid
};
