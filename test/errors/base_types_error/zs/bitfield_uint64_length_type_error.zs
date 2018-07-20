package bitfield_uint64_length_type_error;

struct UInt64LengthTypeBitfield
{
    uint64      length;
    bit<length> wrong;
};
