package cyclic_imports.bottom;

import cyclic_imports.top.*;   // test for parsing cyclic depending imports

struct BottomStructure
{
    uint8   type : type == 3;
    int32   data;
};

enum uint8 BottomColour
{
    RED = 1,
    GREEN = 2,
    BLUE = 3
};
