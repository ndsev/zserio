package offsets.signed_bitfield_error;

struct SignedBitfieldError
{
    int<12> offsets[];
offsets[@index]:
    uint32  values[];
};
