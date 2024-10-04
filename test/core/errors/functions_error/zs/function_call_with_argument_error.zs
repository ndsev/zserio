package function_call_with_argument_error;

struct ValueHolder
{
    int8 value;

    function int32 getPoweredValue()
    {
        return value * value;
    }
};

struct ArgumentError
{
    ValueHolder holder;
    int32 extraValue if holder.getPoweredValue(2) > 20;
};
