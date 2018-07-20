package expressions.function_type;

enum uint8 Color
{
    RED,
    BLUE
};

struct FunctionTypeExpression
{
    Color   color;
    bool    isRedColorLight if getCurrentColor() == Color.RED;

    function Color getCurrentColor()
    {
        return color;
    }
};
