package parameterized_types.param_with_optional;

struct Holder
{
    Param param;
    Value(param) value;
};

struct Param
{
    bool hasExtra;
    bit:7 extraParam if hasExtra;
};

struct Value(Param param)
{
    uint32 normalValue if !param.hasExtra;
    ExtraValue(param.extraParam) extraValue if param.hasExtra; // optional field stored in parameter
};

struct ExtraValue(bit:7 extraParam)
{
    uint64 value if extraParam == 0;
};
