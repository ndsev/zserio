package templates.union_templated_field;

union TemplatedUnion<T1, T2>
{
    T1 field1;
    T2 field2;
    Compound<T1> field3;
};

struct Compound<T>
{
    T value;
};

struct UnionTemplatedField
{
    TemplatedUnion<uint16, uint32> uintUnion;
    TemplatedUnion<float32, float64> floatUnion;
    TemplatedUnion<Compound<uint16>, Compound<uint32>> compoundUnion;
};
