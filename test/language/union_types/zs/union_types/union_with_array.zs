package union_types.union_with_array;

struct Data8
{
    int8 data;
};

union TestUnion
{
    Data8 array8[]; // ObjectArray needs @SuppressWarning("unchecked")
    int16 array16[];
};
