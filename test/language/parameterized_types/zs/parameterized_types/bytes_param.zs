package parameterized_types.bytes_param;

// currently just check that we generate compilable code
struct Parameterized(bytes param)
{
    uint32 field;
};

struct BytesParam
{
    bytes bytesField;
    Parameterized(bytesField) parameterizedField;
};
