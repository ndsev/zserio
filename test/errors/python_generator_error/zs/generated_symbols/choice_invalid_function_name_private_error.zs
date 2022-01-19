package generated_symbols.choice_invalid_function_name_private_error;

choice TestChoice(uint32 param) on param
{
    case 0:
        uint32 field;
    default:
        ;

    function uint32 _choice() // starts with '_' (and yet clashes with a private member)
    {
        return field;
    }
};
