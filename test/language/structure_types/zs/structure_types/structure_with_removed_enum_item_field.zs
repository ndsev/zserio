package structure_types.structure_with_removed_enum_item_field;

enum uint8 Enumeration
{
    @removed REMOVED,
    VALID
};

struct StructureWithRemovedEnumItemField
{
    Enumeration enumeration;
};
