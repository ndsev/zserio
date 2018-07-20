package expressions.const_type;

const bit:7 INVALID_VALUE = 0x00;

struct ConstTypeExpression
{
    bit:7   value;
    bit:3   additionalValue if value != INVALID_VALUE;
};
