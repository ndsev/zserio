package templates;

template <TYPE>
struct Coordinate2D(TYPE xOffset, TYPE yOffset)
{
    TYPE x;
    TYPE y;

    function TYPE getX()
    {
        return x + xOffset;
    }

    function TYPE getY()
    {
        return y + yOffset;
    }
};

template <COORD, TYPE>
struct Line
{
    TYPE xOffset;
    TYPE yOffset;
    COORD(xOffset, yOffset) points[];
};

subtype Line<Coordinate2D<uint32>, uint32> Line2D;
subtype Line<Coordinate2D<uint8>, uint8> SmallLine2D;
