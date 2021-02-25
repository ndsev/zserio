package property_names.choice_function_property_clash_error;

choice TestChoice(uint32 param) on param
{
    case 1:
        uint32 simple;
    case 0:
        ; // empty
    default:
        uint32 funcArray[param]; // clashes with generated function method

    function bool array()
    {
        return param > 0;
    }
};
