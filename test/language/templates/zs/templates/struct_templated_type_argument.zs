package templates.struct_templated_type_argument;

struct ParamHolder<T>
{
    T param;
};

struct Parameterized<T>(ParamHolder<T> paramHolder)
{
    string description;
paramHolder.param:
    uint32 id;
};

struct StructTemplatedTypeArgument
{
    ParamHolder<uint32> paramHolder;
    Parameterized<uint32>(paramHolder) parameterized;
};
