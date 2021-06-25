package array_types_warning.packed_array_unpackable_bool_element;

bitmask uint8 TestBitmask
{
    BLACK,
    WHITE
};

enum uint16 TestEnum
{
    ONE,
    TWO
};

struct PackedArrayUnpackableBoolElement
{
    packed uint32 array1[];
    packed TestEnum array2[];
    packed TestBitmask array3[];
    packed bit:5 array4[];
    uint8 bitLength;
    packed int<bitLength> array5[];
    packed bool array6[]; // unpackable
};
