package with_range_check_code.optional_bit31_range_check;

struct OptionalBit31RangeCheckCompound
{
    bool    hasOptional;
    bit:31  value if hasOptional == true;
};
