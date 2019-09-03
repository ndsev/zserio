#include <iostream>

#include "templates/Coordinate2D.h"
#include "templates/Line.h"
#include "templates/Line2D.h"
#include "templates/SmallLine2D.h"
#include "templates/U32.h"

using namespace templates;

int main()
{
    Coordinate2D<U32> coord2D;

    Line<Coordinate2D<U32>, U32> line;

    Line2D line2D;

    SmallLine2D smallLine2D;

    return 0;
}
