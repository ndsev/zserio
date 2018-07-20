package expressions.float_type;

struct FloatTypeExpression
{
    float16 floatValue;

    function bool result()
    {
        return (floatValue * 2.0 + 1.0 / 0.5 > 1.0);
    }
};
