package templates.instantiate_type_on_parameterized_template;

struct Test<T>(uint32 p)
{
    T(p) value;
};

struct Parameterized(uint32 p)
{
    uint32 array[p];
};

struct InstantiateTypeOnParameterizedTemplate
{
    uint32 param;
    TestP(param) value;
};

// define at the end to check correct instantiate type resolution
instantiate Test<Parameterized> TestP;
