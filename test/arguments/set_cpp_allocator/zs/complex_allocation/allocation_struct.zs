package complex_allocation.allocation_struct;

struct AllocationStruct
{
    bit:7          bit7Array[];
    string         stringField;
    string         defaultStringField = "Structure Default String Field Must Be Longer Than 32 Bytes";
    packed uint16  packedUInt16Array[];
    packed Element packedElementArray[];
};

// used as a packed array element, it must have several fields since we need to ensure that children vector in
// PackingContextNode will allocate
struct Element
{
    uint32 valueX;
    uint32 valueY;
    uint32 valueZ;
};

