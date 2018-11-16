package wrong_lengthof_syntax_error;

struct WrongLengthOfSyntax
{
    uint8          fixedArray[10];

    function uint8 getLengthOfFixedArray()
    {
        return lengthof fixedArray;
    }
};
