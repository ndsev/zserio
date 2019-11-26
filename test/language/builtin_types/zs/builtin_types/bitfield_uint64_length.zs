package builtin_types.bitfield_uint64_length;

struct Container
{
    // Type of this length has been chosen intentionally because it's mapped to BigInteger in Java.
    uint64      length;
    bit<length> unsignedBitField;
    int<length> signedBitField;
};
