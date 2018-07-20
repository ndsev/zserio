package expressions.negation_operator;

struct NegationOperatorExpression
{
    bool    value;

    function bool negatedValue()
    {
        return !value;
    }
};
