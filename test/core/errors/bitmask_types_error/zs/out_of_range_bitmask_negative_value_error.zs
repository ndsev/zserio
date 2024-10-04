package out_of_range_bitmask_negative_value_error;

bitmask uint8 OutOfRangeValue
{
    NONE       = 000b,
    READ       = 128,
    WRITE      = -1
};
