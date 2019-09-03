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
*/
struct U32
{
    uint32 value;
};

struct U8
{
    uint8 value;
};

subtype Line<Coordinate2D<U32>, U32> Line2D;
subtype Line<Coordinate2D<U8>, U8> SmallLine2D;
