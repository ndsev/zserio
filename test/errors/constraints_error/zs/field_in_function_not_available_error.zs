package field_in_function_not_available_error;

struct Container
{
    int16 extraMinValue;
    int32 extraValue : checkExtra();        // ok, extraMinValue already defined
    int32 specialValue : checkSpecial();    // specialMinValue is not available
    int16 specialMinValue;

    function bool checkExtra()
    {
        return extraValue > extraMinValue;
    }

    function bool checkSpecial()
    {
        return specialValue > specialMinValue;
    }
};
