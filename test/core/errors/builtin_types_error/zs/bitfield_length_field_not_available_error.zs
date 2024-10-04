package bitfield_length_field_not_available_error;

struct Container
{
    bit<BitfieldLengthConst>    fieldA;             // constant should be visible
    int8                        nextBitfieldLength;
    bit<nextBitfieldLength>     fieldB;             // nextBitfieldLength is visible
    bit<prevBitfieldLength>     fieldC;             // prevBitfieldLength not available
    int8                        prevBitfieldLength;
};

const int8 BitfieldLengthConst = 13;
