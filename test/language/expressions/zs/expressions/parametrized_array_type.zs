package expressions.parametrized_array_type;

struct ParametrizedArrayTypeExpression
{
    ParametrizedArrayHolder(false)  holder;
    bool                            isValue1Zero if holder.array[0].value1 == 0;
};

struct ParametrizedArrayHolder(bool needsExtra)
{
    ParametrizedArrayElement(needsExtra)    array[2];
};

struct ParametrizedArrayElement(bool needsExtra)
{
    uint16  value1;
    uint16  value2 if needsExtra == true;
};
