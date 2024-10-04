package ternary_operator_error;

struct OffsetHolder
{
    bool    isFirstOffsetUsed;
    bit:6   firstOffset;
    bit:6   secondOffset;
isFirstOffsetUsed ? firstOffset : secondOffset:
    bit<isFirstOffsetUsed ? firstOffset : secondOffset> field;
};
