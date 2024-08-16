package implicit_array_with_indexed_offsets_error;

struct SingleOffsetIsOk
{
    string description;
    uint32 offset;
offset:
    implicit uint32 array[];
};

struct ImplicitArrayWithOffsetsError
{
    uint32 offsets[];
offsets[@index]:
    implicit uint32 array[];
};
