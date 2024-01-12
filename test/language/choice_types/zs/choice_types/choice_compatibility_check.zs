package choice_types.choice_compatibility_check;

struct CoordXY
{
    uint32 coordX;
    uint32 coordY;
};

enum uint8 EnumVersion1
{
    COORD_XY,
    TEXT = 5,
};

choice ChoiceVersion1(EnumVersion1 selector) on selector
{
    case COORD_XY:
        CoordXY coordXY;
    case TEXT:
        string text;
};

enum uint8 EnumVersion2
{
    COORD_XY = valueof(EnumVersion1.COORD_XY),
    TEXT = valueof(EnumVersion1.TEXT),
    COORD_XYZ
};

struct CoordXYZ
{
    uint32 coordX;
    uint32 coordY;
    float64 coordZ;
};

choice ChoiceVersion2(EnumVersion2 selector) on selector
{
    case COORD_XY:
        CoordXY coordXY;
    case TEXT:
        string text;
    case COORD_XYZ:
        CoordXYZ coordXYZ;
};

struct Holder<ENUM, CHOICE>
{
    ENUM selector;
    CHOICE(selector) choiceField;
};

// In this test we need to be able to:
// 1. write version 1 and read it using version 1
// 2. write version 1 and read it using version 2!
// 3. write version 2 without using added enum items (COORD_XYZ) and read it using version 1!
// 4. write version 2 using any features and read it using version 2 :-)
struct ChoiceCompatibilityCheck<HOLDER>
{
    HOLDER array[];
    packed HOLDER packedArray[];
};

instantiate Holder<EnumVersion1, ChoiceVersion1> HolderVersion1;
instantiate Holder<EnumVersion2, ChoiceVersion2> HolderVersion2;
instantiate ChoiceCompatibilityCheck<HolderVersion1> ChoiceCompatibilityCheckVersion1;
instantiate ChoiceCompatibilityCheck<HolderVersion2> ChoiceCompatibilityCheckVersion2;
