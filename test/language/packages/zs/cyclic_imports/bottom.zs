package cyclic_imports.bottom;

import cyclic_imports.top.*; // test for parsing cyclic depending imports

struct BottomStructure
{
    uint8           type : type == 3;
    int32           data;
    BottomColour    bottomColour;
    BottomColour    anotherColour if isBottomColourRed();

    function bool isBottomColourRed()
    {
        // test correct full path with multiple evaluation (one evaluation is from top.zs)
        return bottomColour == cyclic_imports.bottom.BottomColour.RED;
    }
};

enum uint8 BottomColour
{
    RED = 1,
    GREEN = cyclic_imports.top.GREEN, // test correct cyclic full path import
    BLUE = 3
};
