package function_with_parameter_error;

struct ParameterError
{
    int32 field;

    function int32 functionWithParam(int32 param)
    {
        return param * field;
    }
};
