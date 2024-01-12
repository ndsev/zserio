package templates.instantiate_type_on_parameterized_template;

struct Test<T>(uint32 param)
{
    T(param) value;
};

struct Parameterized(uint32 param)
{
    uint32 array[param];
};

struct InstantiateTypeOnParameterizedTemplate
{
    uint32 param;
    TestP(param) value;
};

// define at the end to check correct instantiate type resolution
instantiate Test<Parameterized> TestP;
