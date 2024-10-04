package extended_nested_in_array_error;

struct Extended
{
    uint32 field;
    extend string extendedField;
};

struct TopLevel
{
    Extended array[];
};
