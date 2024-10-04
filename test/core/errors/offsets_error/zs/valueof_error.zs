package valueof_error;

enum uint32 Enum
{
    ONE,
    TWO
};

struct ValueOfError
{
valueof(Enum.ONE):
    string field;
};
