package valueof_operators.integer_type_error;

struct IntegerTypeError
{
    uint32  value;
    int32   field if valueof(value) == 0;
};
