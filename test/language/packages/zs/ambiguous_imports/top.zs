package ambiguous_imports.top;

import ambiguous_imports.first_colour.*;
import ambiguous_imports.second_colour.*;

struct TopStructure
{
    uint8                                   type : type == 1;
    int32                                   data;
    ambiguous_imports.first_colour.Colour   colour1;
    ambiguous_imports.second_colour.Colour  colour2;

    bool    isColour1White if colour1 == ambiguous_imports.first_colour.Colour.WHITE;
    bool    isColour2Red if colour2 == ambiguous_imports.second_colour.Colour.RED;
};
