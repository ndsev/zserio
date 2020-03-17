package implicit_array_dynamic_bitfield_error;

struct ImplicitArrayDynamicBitfieldError
{
    uint8 len;
    implicit bit<len> array[];
};
