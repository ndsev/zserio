package expressions.sum_operator;

struct SumFunction
{
    uint8   fixedArray[10];

    function uint16 getSumFixedArray()
    {
        return sum(fixedArray);
    }
};
