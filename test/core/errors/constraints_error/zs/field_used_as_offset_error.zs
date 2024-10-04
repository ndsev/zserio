package field_used_as_offset_error;

struct Container
{
    uint32 offset;
offset:
    string field : offset > 0;
};

