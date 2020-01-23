package expressions.bitmask_type;

bitmask uint8 Colors
{
    RED,
    GREEN,
    BLUE,
};

struct BitmaskTypeExpression
{
    Colors  colors;
    bool    hasColorRed if (colors & Colors.RED) == Colors.RED;
    bool    hasColorGreen if (colors & Colors.GREEN) == Colors.GREEN;
    bool    hasColorBlue if (colors & Colors.BLUE) == expressions.bitmask_type.Colors.BLUE;
    bool    hasAllColors if colors == (Colors.RED | expressions.bitmask_type.Colors.GREEN | Colors.BLUE);
    bool    hasNotColorRed if (colors & Colors.RED) != Colors.RED;
    bool    hasOtherColorThanRed if valueof(colors & ~Colors.RED) != 0;
};
