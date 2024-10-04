package extended_nested_in_union_error;

struct Extended
{
    uint32 field;
    extend string extendedField;
};

union Union
{
    uint32 field;
    Extended extended;
};
