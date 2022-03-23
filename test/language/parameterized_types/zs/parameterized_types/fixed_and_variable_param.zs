package parameterized_types.fixed_and_variable_param;

struct LimitHolder
{
    uint8 limit;
};

enum bit:2 Color
{
    BLACK,
    WHITE
};

bitmask bit:4 Access
{
    READ,
    WRITE
};

struct ArrayHolder(varsize size, uint8 extraLimit, LimitHolder limitHolder, Color color, Access access,
        float16 floatValue)
{
    varuint array[size];
    bit:3 extraValue : extraValue <= extraLimit && extraValue <= limitHolder.limit;
    bool hasBlack : hasBlack == (color == Color.BLACK);
    bool hasRead : hasRead == ((access & Access.READ) == Access.READ);
    bool hasFloatBiggerThanOne : hasFloatBiggerThanOne == (floatValue > 1.0f);
};

struct FixedAndVariableParam
{
    uint8 extraLimit;
    LimitHolder limitHolder;
    Color color;
    Access access;
    float16 floatValue;
    ArrayHolder(1000, extraLimit, limitHolder, color, access, floatValue) arrayHolder;
};
