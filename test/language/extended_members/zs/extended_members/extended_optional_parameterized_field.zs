package extended_members.extended_optional_parameterized_field;

struct Original
{
    bit:11 value;
};

struct Parameterized(bit:11 param)
{
    string array[param];
};

struct Extended
{
    bit:11 value;
    extend optional Parameterized(value) extendedValue;
};
