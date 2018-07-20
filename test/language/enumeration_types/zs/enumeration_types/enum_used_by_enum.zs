package enumeration_types.enum_used_by_enum;

enum bit:7 Color
{
    NONE,

    LIGHT_RED   = LightColor.LIGHT_RED,
    LIGHT_GREEN = LightColor.LIGHT_GREEN,
    LIGHT_BLUE  = LightColor.LIGHT_BLUE,
    LIGHT_PINK,

    DARK_RED    = DarkColor.DARK_RED,
    DARK_GREEN  = DarkColor.DARK_GREEN,
    DARK_BLUE   = DarkColor.DARK_BLUE,
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
