package index_operator.constraint_expression_error;

struct ContraintExpressionError
{
    int32 field[10] : field < @index;
};
