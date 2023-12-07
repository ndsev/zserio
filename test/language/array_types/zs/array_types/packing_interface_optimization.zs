package array_types.packing_interface_optimization;

// the test is designed to check that generated user types do not contain methods
// for packing since they are not needed

bitmask bit:3 UnpackedColorBitmask
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 4,
    WHITE = 7
};

enum uint8 UnpackedColorEnum
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 3,
    WHITE = 4
};

union UnpackedColorUnion
{
    UnpackedColorBitmask colorBitmask;
    UnpackedColorEnum colorEnum;
};

choice UnpackedColorChoice(bool selector) on selector
{
    case true:
        string colorName;
    default:
        UnpackedColorUnion colorUnion;
};

struct UnpackedColorStruct
{
    bool hasColorName;
    UnpackedColorChoice(hasColorName) colorChoice;
};

bitmask bit:3 MixedColorBitmask
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 4,
    WHITE = 7
};

enum uint8 MixedColorEnum
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 3,
    WHITE = 4
};

union MixedColorUnion
{
    MixedColorBitmask colorBitmask;
    MixedColorEnum colorEnum;
};

choice MixedColorChoice(bool selector) on selector
{
    case true:
        string colorName;
    default:
        MixedColorUnion colorUnion;
};

struct MixedColorStruct
{
    bool hasColorName;
    MixedColorChoice(hasColorName) colorChoice;
};

bitmask bit:3 PackedColorBitmask
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 4,
    WHITE = 7
};

enum uint8 PackedColorEnum
{
    BLACK = 0,
    BLUE = 1,
    GREEN = 2,
    RED = 3,
    WHITE = 4
};

union PackedColorUnion
{
    PackedColorBitmask colorBitmask;
    PackedColorEnum colorEnum;
};

choice PackedColorChoice(bool selector) on selector
{
    case true:
        string colorName;
    default:
        PackedColorUnion colorUnion;
};

struct PackedColorStruct
{
    bool hasColorName;
    PackedColorChoice(hasColorName) colorChoice;
};

struct UnpackedColorsHolder
{
    UnpackedColorStruct unpackedColors[];
    MixedColorStruct mixedColors[];
};

struct PackedColorsHolder
{
    packed MixedColorStruct mixedColors[];
    packed PackedColorStruct packedColors[];
};

struct PackingInterfaceOptimization
{
    UnpackedColorsHolder unpackedColorsHolder;
    PackedColorsHolder packedColorsHolder;
};
