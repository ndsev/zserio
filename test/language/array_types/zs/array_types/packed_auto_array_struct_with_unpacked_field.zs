package array_types.packed_auto_array_struct_with_unpacked_field;

struct TestStructure
{
    uint8 uint8Field;
    varuint unpackedField; // depending on data stored by particular tests - this checks the "smart packing"
};

struct PackedAutoArray
{
    packed TestStructure array[];
};
