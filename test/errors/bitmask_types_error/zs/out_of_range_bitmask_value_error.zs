package out_of_range_bitmask_value_error;

bitmask uint8 OutOfRangeValue
{
    NONE       = 000b,
    READ       = 128,
    WRITE // calculated to 256
};
