package expressions.enumeration_type;

enum uint8 Color
{
    RED,
    BLUE
};

struct EnumerationTypeExpression
{
    Color   color;
    bool    isColorRed if color == Color.RED;
};
