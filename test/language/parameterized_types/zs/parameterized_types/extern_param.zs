package parameterized_types.extern_param;

// currently just check that we generate compilable code
struct Parameterized(extern param)
{
    uint32 field;
};

struct ExternParam
{
    extern externField;
    Parameterized(externField) parameterizedField;
};
