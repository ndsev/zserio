package expressions.string_type;

struct StringTypeExpression
{
    string firstValue;
    string secondValue;

    function string appendix()
    {
        // This is intended to check concatenation of two string literals.
        return "append" + "ix";
    }

    function string append()
    {
        return firstValue + secondValue + "_" + appendix();
    }
};
