package generated_symbols.choice_choice_tag_property_clash_error;

choice TestChoice(bool selector) on selector
{
    case true:
        string value1;
    default:
        uint32 choiceTag; // clashes with property in generated API
};
