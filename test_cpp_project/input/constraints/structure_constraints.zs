package constraints.structure_constraints;

enum uint8 BasicColor
{
    BLACK,
    WHITE,
    RED
};

struct StructureConstraints
{
    BasicColor      blackColor : blackColor == BasicColor.BLACK;
    BasicColor      whiteColor : whiteColor == BasicColor.WHITE;
    ExtendedColor   purpleColor : purpleColor == ExtendedColor.PURPLE; // enum defined later
};

enum uint16 ExtendedColor
{
    PURPLE,
    LIME
};
