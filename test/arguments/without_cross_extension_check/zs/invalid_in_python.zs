package invalid_in_python;

struct Test
{
    // clashing properties
    uint32 someField;
    uint32 some_field;

    // Python keyword
    string def;
};
