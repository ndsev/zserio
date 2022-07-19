package parameterized_types.dynamic_bitfield_param;

struct DynamicBitfieldParamHolder
{
    bit:4 length = 5;
    int<length> bitfield = 11;
    DynamicBitfieldParam(bitfield) dynamicBitfieldParam;
};

struct DynamicBitfieldParam(int param)
{
    uint16    value;
    uint32    extraValue if param == 11;
};
