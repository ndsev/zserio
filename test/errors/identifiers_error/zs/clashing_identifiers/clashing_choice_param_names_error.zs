package clashing_identifiers.clashing_choice_param_names_error;

struct Parameterized(uint16 param)
{
    uint8 arr[param];
};

choice Choice(uint8 param, uint16 Param) on param
{
    case 0:
        uint8 simple[Param];
    default:
        Parameterized(Param) parameterized;
};
