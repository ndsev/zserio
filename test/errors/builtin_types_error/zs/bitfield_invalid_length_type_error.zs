package bitfield_invalid_length_type_error;

struct InvalidLengthTypeBitfield
{
    bool        length;
    bit<length> wrong;
};
