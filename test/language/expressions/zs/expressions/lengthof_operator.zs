package expressions.lengthof_operator;

struct LengthOfFunctions
{
    uint8           fixedArray[10];

    uint8           numElements;
    uint8           variableArray[numElements];

    implicit uint8  implicitArray[];

    function uint8 getLengthOfFixedArray()
    {
        return lengthof(fixedArray);
    }

    function uint8 getLengthOfVariableArray()
    {
        return lengthof(variableArray);
    }

    function uint8 getLengthOfImplicitArray()
    {
        return lengthof(implicitArray);
    }
};
