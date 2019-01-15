package no_arguments_error;

struct Parameterized(int32 param)
{
    int32 arr[param];
};

struct NoArguments
{
    Parameterized() parameterized;
};
