package scope_symbols.choice_parameter_names_clash_error;

choice TestChoice(bool someParam, uint32 some_param) on someParam
{
    case true:
        uint32 someField;
    case false:
        float32 otherfield;
};
