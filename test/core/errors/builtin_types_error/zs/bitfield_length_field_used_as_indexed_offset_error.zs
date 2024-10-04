package bitfield_length_field_used_as_indexed_offset_error;

struct Container
{
    uint32 offsets[];
offsets[@index]:
    string fields[];
    bit<offsets[0]> bitField;
};
