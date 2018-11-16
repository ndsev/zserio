package expressions.valueof_operator;

struct ValueOfFunctions
{
    Color  color;

    function uint8 getValueOfColor()
    {
        return valueof(color);
    }

    function uint8 getValueOfWhiteColor()
    {
        return valueof(Color.WHITE);
    }

    function uint8 getValueOfBlackColor()
    {
        return valueof(Color.BLACK);
    }
};

enum uint8 Color
{
    WHITE = 1,
    BLACK = 2
};
