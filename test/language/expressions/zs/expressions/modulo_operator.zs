package expressions.modulo_operator;

struct ModuloFunction
{
    function bool isValueDivBy4()
    {
        return (getValue() % 4) == 0;
    }

    function uint64 getValue()
    {
        return 8; // this fix number is done intentionally to check Java BigInteger casting
    }
};
