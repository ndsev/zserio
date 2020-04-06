package parameter_no_array_error;

struct Parameterized(int32 id)
{
    int32 position : position == id;
};

struct ParameterNoArrayError
{
    Parameterized(@index) field;
};
