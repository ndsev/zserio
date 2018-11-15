package enumeration_types.enum_used_by_enum;

enum bit:7 Color
{
    NONE,

    LIGHT_RED   = valueof(LightColor.LIGHT_RED),
    LIGHT_GREEN = valueof(LightColor.LIGHT_GREEN),
    LIGHT_BLUE  = valueof(LightColor.LIGHT_BLUE),
    LIGHT_PINK,

    DARK_RED    = valueof(DarkColor.DARK_RED),
    DARK_GREEN  = valueof(DarkColor.DARK_GREEN),
    DARK_BLUE   = valueof(DarkColor.DARK_BLUE),
    DARK_PINK
};

// These enumerations are defined after Color intentionally to check (pre)evaluation of expressions.
enum bit:2 LightColor
{
    LIGHT_RED   = 0x01,
    LIGHT_GREEN = 0x02,
    // The value expression is defined so complicated intentionally to check (pre)evaluation of expression tree.
    LIGHT_BLUE  = 0x02 | 0x01
};

enum bit:5 DarkColor
{
    DARK_RED    = 0x11,
    DARK_GREEN  = 0x12,
    DARK_BLUE   = 0x13
};
