package union_types.union_with_parameterized_field;

struct ArrayHolder(uint8 size)
{
    uint32 array[size];
};

union TestUnion
{
    uint32 field;
    ArrayHolder(10) arrayHolder;
};
