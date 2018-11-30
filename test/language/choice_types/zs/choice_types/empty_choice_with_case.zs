package choice_types.empty_choice_with_case;

choice EmptyChoiceWithCase(uint8 selector) on selector
{
    case 0:
        ; // no fields to ensure that the generated code is valid
};
