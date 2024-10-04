package function_call_in_array_expression_error;

struct FunctionCallInArrayExpressionError
{
    uint32 offsets[];
offsets[get()]:
    string fields[];

    function uint32 get()
    {
        return 0;
    }
};
