package bitmask_types.bitfield_const_bitmask;

const uint8 NUM_BITS = 3;

bitmask bit<NUM_BITS> Permission
{
    NONE  = 000b,
    READ  = 010b,
    WRITE
};
