package expressions.full_enumeration_type;

enum uint8 Color
{
    RED,
    BLUE
};

struct FullEnumerationTypeExpression
{
    Color   color;
    bool    isColorRed if color == expressions.full_enumeration_type.Color.RED;
};
