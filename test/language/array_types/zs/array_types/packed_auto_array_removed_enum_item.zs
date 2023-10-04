package array_types.packed_auto_array_removed_enum_item;

enum uint8 Traffic
{
    NONE = 1,
    @removed HEAVY,
    LIGHT,
    MID
};

struct PackedAutoArrayRemovedEnumItem
{
    packed Traffic packedArray[];
};
