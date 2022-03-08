package expressions.string_type;

const string STRING_CONSTANT = "CONSTANT";

const bool CHOOSER = true;

const string CHOOSED_STRING_CONSTANT = CHOOSER ? "chosen" + " " + STRING_CONSTANT : "";

struct StringTypeExpression
{
    bool hasValue;
    string value;
    string defaultValue = CHOOSER ? STRING_CONSTANT : "false" + " " + STRING_CONSTANT;
    string defaultChosen = CHOOSED_STRING_CONSTANT;

    function string returnValue()
    {
        return value;
    }

    function string returnDefaultValue()
    {
        return defaultValue;
    }

    function string returnDefaultChosen()
    {
        return defaultChosen;
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

    function string valueOrLiteral()
    {
        return hasValue ? value : "literal";
    }

    function string valueOrLiteralExpression()
    {
        return hasValue ? value : "literal" + " " + "expression";
    }

    function string valueOrConst()
    {
        return hasValue ? value : STRING_CONSTANT;
    }

    function string valueOrConstExpression()
    {
        return hasValue ? value : STRING_CONSTANT + " " + "expression";
    }
};
