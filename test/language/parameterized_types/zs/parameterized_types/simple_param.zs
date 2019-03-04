package parameterized_types.simple_param;

struct Item(uint32 version)
{
    uint16    param;
    uint32    extraParam if version >= 10;
};
