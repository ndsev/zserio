package out_of_range_bitmask_explicit_value_error;

bitmask uint8 OutOfRangeValue
{
    NONE       = 000b,
    READ       = 255,
    WRITE      = 256
};
