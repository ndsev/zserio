package constraint_expression_error;

struct ContraintExpressionError
{
    int32 field[10] : field < @index;
};
