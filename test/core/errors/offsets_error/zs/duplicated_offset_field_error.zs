package duplicated_offset_field_error;

struct Holder
{
    uint32 offset;
};

struct Container
{
    Holder holder1;
    Holder holder2;
holder1.offset:
    string field1;
holder2.offset: // no problem, another holder
    string field2;
holder1.offset: // duplicated use of the offset field!
    string field3;
};
