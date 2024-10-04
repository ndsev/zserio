package wrong_compound_argument_type_error;

struct Param1
{
    varsize len;
};

struct Param2
{
    string text;
};

struct Parameterized(Param1 param)
{
    uint32 array[param.len];
};

struct WrongCompoundArgumentTypeError
{
    Param1 param1;
    Param2 param2;
    Parameterized(param1) parameterizedOk;
    Parameterized(param2) parameterizedError;
};
