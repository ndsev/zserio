package enumeration_types.bitfield_const_enum;

const uint8 NUM_ENUM_BITS = 5;

enum bit<NUM_ENUM_BITS> Color
{
    NONE  = 000b,
    RED   = 010b,
    BLUE,
    // This checks if comma is allowed after the last item.
    GREEN = 111b,
};
