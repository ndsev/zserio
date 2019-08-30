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
    COORD<TYPE>(xOffset, yOffset) points[];
};

subtype Line<Coordinate2D, uint32> Line2D;
subtype Line<Coordinate2D, uint8> SmallLine2D;

template <COORD>
struct Shape
{
    COORD(0, 0) points[];
};

subtype Shape<Coordinate2D<uint32>> Shape2D; /** Optionally we might need to use '> >' like in old c++. */
