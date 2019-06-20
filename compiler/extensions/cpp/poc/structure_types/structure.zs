struct Coord
{
    uint32 x;
    uint32 y;
};

struct Array(uint32 size)
{
    int32 values[size];
};

struct String
{
    string str;
};

struct Structure
{
    uint32 size;
    Array(size) array;
    bool hasExtra;
    uint32 extraSize if hasExtra;
    Array(extraSize) extraArray if hasExtra;
    String str;
    Structure recursive if hasExtra;
};
