package wrong_intfield_parameter_error;

struct Item(int version)
{
    uint16      param;
    uint32      extraParam if version >= 10;
};
