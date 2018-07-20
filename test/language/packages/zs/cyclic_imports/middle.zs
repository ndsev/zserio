package cyclic_imports.middle;

import cyclic_imports.bottom.*;

struct MiddleStructure
{
    uint8            type : type == 2;
    int32            data;
    BottomStructure  bottom;
};
