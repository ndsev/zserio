package expressions.full_const_type;

const bit:7 FULL_INVALID_VALUE = 0x00;

struct FullConstTypeExpression
{
    bit:7   value;
    bit:3   additionalValue if value != expressions.full_const_type.FULL_INVALID_VALUE;
};
