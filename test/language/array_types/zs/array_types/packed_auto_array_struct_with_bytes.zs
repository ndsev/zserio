package array_types.packed_auto_array_struct_with_bytes;

struct TestStructure
{
    uint32 uint32Field;
    bytes bytesField;
    uint8 uint8Field;
};

struct PackedAutoArray
{
    packed TestStructure array[];
};
