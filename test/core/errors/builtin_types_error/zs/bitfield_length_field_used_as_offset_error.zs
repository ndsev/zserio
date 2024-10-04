package bitfield_length_field_used_as_offset_error;

struct Container
{
    uint32 offset;
offset:
    string field;
    bit<offset> bitField;
};
