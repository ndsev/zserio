package dot_expression_in_array_expression_error;

struct Compound
{
    uint32 id;
};

struct DotExpressionInArrayExpressionError
{
    Compound compound;
    uint32 offsets[];
offsets[compound.id]:
    string fields[];
};
