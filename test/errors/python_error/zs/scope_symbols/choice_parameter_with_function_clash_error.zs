package scope_symbols.choice_parameter_with_function_clash_error;

choice TestChoice(uint32 funcArray) on funcArray // parameter clashes with generated function method
{
    case 1:
        uint32 simple;
    case 0:
        ; // empty
    default:
        uint32 array[funcArray];

    function bool func_array()
    {
        return funcArray > 0;
    }
};
