package wrong_enum_argument_type_error;

enum uint8 Enum1
{
    ONE,
    TWO
};

enum uint8 Enum2
{
    ONE,
    TWO
};

struct Parameterized(Enum1 param)
{
    uint32 array[valueof(param)];
};

struct WrongEnumArgumentTypeError
{
    Enum1 param1;
    Enum2 param2;
    Parameterized(param1) parameterizedOk;
    Parameterized(param2) parameterizedError;
};
