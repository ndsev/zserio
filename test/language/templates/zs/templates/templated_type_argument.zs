package templates.templated_type_argument;

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

struct TemplatedTypeArgument
{
    ParamHolder<uint32> paramHolder;
    Parameterized<uint32>(paramHolder) parameterized;
};
