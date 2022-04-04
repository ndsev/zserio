package offsets.ternary_operator_offset;

struct TernaryOffset
{
    bool   isFirstOffsetUsed;
    uint32 offsets[2];
offsets[isFirstOffsetUsed ? firstIndex() : secondIndex()]:
    int32   value;

    function varsize firstIndex()
    {
        return 0;
    }

    function varsize secondIndex()
    {
        return 1;
    }
};
