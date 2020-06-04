package expressions.string_type;

const string STRING_CONSTANT = "CONSTANT";

struct StringTypeExpression
{
    string value;

    function string returnValue()
    {
        return value;
    }

    function string appendix()
    {
        // This is intended to check concatenation of two string literals.
        return "append" + "ix";
    }

    function string appendToConst()
    {
        return STRING_CONSTANT + "_" + appendix();
    }
};
