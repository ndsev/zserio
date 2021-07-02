package expressions.uint64_type;

struct UInt64TypeExpression
{
    uint32  uint32Value;
    uint64  uint64Value;
    bool    boolValue;
    bit:3   additionalValue if boolValue && (2 * uint64Value - 1) == (uint32Value / 2 - 1);
};
