package enumeration_types.bitfield_enum;

enum bit:3 Color
{
    NONE  = 000b,
    RED   = 010b,
    BLUE,
    // This checks if comma is allowed after the last item.
    GREEN = 111b,
};
