package isset_in_array_expression_error;

bitmask uint8 Bitmask
{
    FIRST,
    SECOND
};

struct IsSetInArrayExpressionError
{
    Bitmask bm;
    uint32 offsets[];
offsets[isset(bm, Bitmask.SECOND)]:
    string fields[];
};
