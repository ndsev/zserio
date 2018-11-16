package import_name_conflict.top;

import import_name_conflict.colour.*;

struct TopStructure
{
    uint8                               type : type == 1;
    int32                               data;
    import_name_conflict.colour.Colour  colour; // the same name as package
    bool                                isColourWhite if colour == import_name_conflict.colour.Colour.WHITE;
};
