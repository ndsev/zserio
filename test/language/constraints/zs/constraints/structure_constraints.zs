package constraints.structure_constraints;

enum uint8 BasicColor
{
    BLACK,
    WHITE,
    RED
};

struct StructureConstraints
{
    BasicColor          blackColor : blackColor == BasicColor.BLACK;
    optional BasicColor whiteColor : whiteColor == BasicColor.WHITE; // auto optional constraint
    bool                hasPurple;
    ExtendedColor       purpleColor if hasPurple : purpleColor == ExtendedColor.PURPLE; // enum defined later
};

enum uint16 ExtendedColor
{
    PURPLE,
    LIME
};
