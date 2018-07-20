package choice_types.bool_param_choice;

subtype int8  Black;
subtype int16 Grey;

// This choice has unreachable default case. Such default case does not have to be generated.
choice BoolParamChoice(bool selector) on selector
{
    case true:
        Black black;

    case false:
        Grey grey;
};
