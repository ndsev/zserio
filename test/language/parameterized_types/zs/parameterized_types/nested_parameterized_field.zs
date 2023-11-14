package parameterized_types.nested_parameterized_field;

struct TopLevel
{
    ParamHolder paramHolder;
};

struct ParamHolder
{
    uint16 parameter = 11;
    Param(parameter) param;
};

struct Param(uint16 parameter)
{
    uint16  value;
    uint32  extraValue if parameter == 11;
};
