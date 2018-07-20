package compound.ct_union;

enum bit:3 Color
{
    NONE = 000b,
    RED = 010b,
    BLUE,
    BLACK = 111b
};

union MyUnion(int16 type)
{
    int16   a : type < 10;
    int32   b : type >= 10;

    function uint16 multiply()
    {
        return type * 10;
    }
};

union ColorUnion
{
    Color   red : red == Color.RED;
    Color   blue : blue == Color.BLUE;
};

struct ParamStructInt32(int32 numElements)
{
    uint32  offsets[numElements];
offsets[@index]:
    int32   array[numElements];
};

struct ParamStructInt16(int32 numElements)
{
    uint32  offsets[numElements];
offsets[@index]:
    int16   array[numElements];
};

union ParamUnion(int32 numElements)
{
    ParamStructInt32(numElements) structInt32;
    ParamStructInt16(numElements) structInt16;
};
