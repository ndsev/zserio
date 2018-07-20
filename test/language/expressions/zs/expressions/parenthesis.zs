package expressions.parenthesis;

struct ParenthesisExpression
{
    int32   firstValue;
    int32   secondValue;

    function int32 result()
    {
        return firstValue * (secondValue + 1);
    }
};
