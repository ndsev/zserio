package complex_allocation.allocation_union;

struct UnionCompound
{
    uint16 value16;
    bool   isValid;
};

union AllocationUnion
{
    UnionCompound compound : compound.value16 > 1;
    UnionCompound array[];
};
