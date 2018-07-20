package expressions.string_type;

struct StringTypeExpression
{
    string firstValue;
    string secondValue;

    function string append()
    {
        return firstValue + secondValue + "_append" + "ix";
    }
};
