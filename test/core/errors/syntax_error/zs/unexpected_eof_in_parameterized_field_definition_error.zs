package unexpected_eof_in_parameterized_field_definition_error;

struct Parameterized(int32 param)
{
    int32 value : value < param;
};

struct UnexpectedEofInFieldDefinition
{
    Parameterized(10
