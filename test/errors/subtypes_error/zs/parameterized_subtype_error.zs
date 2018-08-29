package parameterized_subtype_error;

struct Parameterized(uint32 length)
{
    int32 array[length];
};

subtype Parameterized Subtype;

struct Test
{
    Subtype field; // ERROR: Subtype is a parameterized type and instantiation is expected.
};
