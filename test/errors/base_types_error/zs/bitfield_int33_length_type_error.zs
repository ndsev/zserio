package bitfield_int33_length_type_error;

struct Int33LengthTypeBitfield
{
    int:33      length;
    bit<length> wrong;
};
