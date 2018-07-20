package expressions.array_type;

struct ArrayTypeExpression
{
    int8    array[2];
    bool    isZerosArrayValid if array[0] == 0 && array[1] == 0;
};
