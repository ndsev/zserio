package expressions.isset_operator;

struct IsSetOperator
{
    TestBitmask testBitmask;
    Parameterized(testBitmask) parameterized;

    function bool hasInt()
    {
        return isset(parameterized.param, INT);
    }

    function bool hasString()
    {
        return isset(testBitmask, STRING);
    }

    function bool hasBoth()
    {
        return isset(testBitmask, TestBitmask.BOTH);
    }
};

struct Parameterized(TestBitmask param)
{
    uint32 intField if isset(param, TestBitmask.INT);
    string stringField if isset(param, STRING);

    function bool hasInt()
    {
        return isset(param, INT);
    }

    function bool hasString()
    {
        return isset(param, TestBitmask.STRING);
    }

    function bool hasBoth()
    {
        return isset(param, INT | STRING);
    }
};

// intentionally at the end to check proper expression evaluation
bitmask uint8 TestBitmask
{
    INT,
    STRING,
    BOTH = 0x1 | 0x2
};
