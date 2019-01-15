package expressions.parameterized_array_type;

struct ParameterizedArrayTypeExpression
{
    ParameterizedArrayHolder(false)  holder;
    bool                            isValue1Zero if holder.array[0].value1 == 0;
};

struct ParameterizedArrayHolder(bool needsExtra)
{
    ParameterizedArrayElement(needsExtra)    array[2];
};

struct ParameterizedArrayElement(bool needsExtra)
{
    uint16  value1;
    uint16  value2 if needsExtra == true;
};
