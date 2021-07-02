package expressions.float_type;

struct FloatTypeExpression
{
    float16 floatValue;

    function bool result()
    {
        return (floatValue * 2.0f + 1.0f / 0.5f > 1.0f);
    }
};
