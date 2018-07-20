package index_operator.optional_expression_error;

struct OptionalExpressionError
{
    int32 field[10] if @index > 10;
};
