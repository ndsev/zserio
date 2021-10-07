package array_types.packed_auto_array_struct_with_extern;

struct TestStructure
{
    uint32 uint32Field;
    extern externField;
    uint8 uint8Field;
};

struct PackedAutoArray
{
    packed TestStructure array[];
};
