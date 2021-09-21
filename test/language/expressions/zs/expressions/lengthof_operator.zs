package expressions.lengthof_operator;

struct LengthOfFunctions
{
    uint8           fixedArray[10];

    uint8           numElements;
    uint8           variableArray[numElements];

    function uint8 getLengthOfFixedArray()
    {
        return lengthof(fixedArray);
    }

    function uint8 getLengthOfVariableArray()
    {
        return lengthof(variableArray);
    }
};
