package valueof_operators.array_type_error;

struct ArrayTypeError
{
    uint32  offsets[10];
    int32   field if valueof(offsets) == 0;
};
