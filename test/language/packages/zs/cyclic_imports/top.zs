package cyclic_imports.top;

import cyclic_imports.middle.*;
import cyclic_imports.bottom.*;

struct TopStructure
{
    uint8            type : type == 1;
    int32            data;
    MiddleStructure  middleStructure;
    BottomStructure  bottomStructure;
    BottomColour     bottomColour : bottomColour == BottomColour.RED;
    bool             isBottomColourRed if bottomStructure.isBottomColourRed();
};

const uint8 GREEN = 2;
