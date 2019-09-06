package templates;

template <TYPE>
struct Coordinate2D(TYPE xOffset, TYPE yOffset)
{
    TYPE x;
    TYPE y;

    function uint32 getX()
    {
        return x.value + xOffset.value;
    }

    function uint32 getY()
    {
        return y.value + yOffset.value;
    }
};

template <COORD, TYPE>
struct Line
{
    TYPE xOffset;
    TYPE yOffset;
    COORD(xOffset, yOffset) points[];
};

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
