package array;

template <TYPE>
struct Array
{
    TYPE values[]; // not that the TYPE doesn't have any parameters - i.e. may be also builtin!
};

struct Data
{
    uint32 value;
};

subtype Array<uint32> U32Array;
subtype Array<string> StringArray;
subtype Array<Data> DataArray;
