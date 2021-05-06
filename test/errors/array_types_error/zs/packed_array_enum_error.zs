package packed_array_enum_error;

enum uint16 TestEnum
{
    ONE,
    TWO
};

struct PackedArrayEnumError
{
    packed TestEnum array[];
};
