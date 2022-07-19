package parameterized_types.parameterized_param;

struct ParameterizedParamHolder
{
    uint16 parameter = 11;
    Param(parameter) param;
    ParameterizedParam(param) parameterizedParam;
};

struct Param(uint16 parameter)
{
    uint16  value;
    uint32  extraValue if parameter == 11;
};

struct ParameterizedParam(Param param)
{
    uint16  value;
    uint32  extraValue if param.parameter == 11;
};
