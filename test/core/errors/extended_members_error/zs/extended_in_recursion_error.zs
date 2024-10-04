package extended_in_recursion_error;

struct Extended
{
    uint32 value;
    optional Extended recursive;
    extend string extendedValue;
};
