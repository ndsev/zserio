package bitmask_types.bitfield_bitmask;

bitmask bit:3 Permission
{
    NONE  = 000b,
    READ   = 010b,
    // This checks if comma is allowed after the last item.
    WRITE,
};
