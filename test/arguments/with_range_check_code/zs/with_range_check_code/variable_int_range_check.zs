package with_range_check_code.variable_int_range_check;

struct VariableIntRangeCheckCompound
{
    uint8           numBits;
    int<numBits>    value;
};
