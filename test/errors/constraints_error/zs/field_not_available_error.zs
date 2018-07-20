package field_not_available_error;

struct Container
{
    int16 extraMinValue;
    int32 extraValue : extraValue > extraMinValue;       // ok, extraMinValue already defined
    int32 specialValue : specialValue > specialMinValue; // specialMinValue is not available
    int16 specialMinValue;
};
