package complex_allocation.allocation_struct;

struct AllocationStruct
{
    bit:7         bit7Array[];
    string        stringField;
    string        defaultStringField = "Structure Default String Field Must Be Longer Than 32 Bytes";
    packed uint16 packedUInt16Array[];
};
