package expressions.parameter_type;

enum uint8 Color
{
    RED,
    BLUE
};

struct ParameterTypeExpression(Color parameter)
{
    int:7   value;
    bool    isParameterRed if parameter == Color.RED;
};
