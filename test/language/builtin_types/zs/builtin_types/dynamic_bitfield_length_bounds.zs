package builtin_types.dynamic_bitfield_length_bounds;

struct Container
{
    bit:4 unsignedBitLength; // 4 bits to map unsignedValue to short in Java
    bit<unsignedBitLength> unsignedValue;

    uint8 unsignedBigBitLength; // uint8 to map unsignedBigValue to BigInteger in Java
    bit<unsignedBigBitLength> unsignedBigValue;

    uint64 signedBitLength; // mapped to BigInteger in Java
    int<signedBitLength> signedValue;
};
