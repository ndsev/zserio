package scope_symbols.choice_field_names_clash_error;

choice TestChoice(bool param) on param
{
    case true:
        uint32 someField;
    case false:
        float32 some_field;
};
