package templates.instantiate_type_as_parameter;

struct Parameter<T>
{
    T value;
};

struct Parameterized<P>(P p)
{
    uint32 arr[p.value];
};

struct InstantiateTypeAsParameter
{
    P32 parameter;
    Parameterized<P32>(parameter) parameterized;
};

// define at the end to check correct template argument resolution
instantiate Parameter<uint32> P32;
