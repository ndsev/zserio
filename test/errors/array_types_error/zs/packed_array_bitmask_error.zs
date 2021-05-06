package packed_array_bitmask_error;

bitmask uint16 TestBitmask
{
    BLACK,
    WHITE
};

struct PackedArrayBitmaskError
{
    packed TestBitmask array[];
};
