package valueof_in_array_expression_error;

enum uint8 Enum
{
    ONE,
    TWO
};

struct ValueOfInArrayExpressionError
{
    uint32 offsets[];
offsets[valueof(Enum.TWO)]:
    string fields[];
};
