package structure_types.field_constructor_clashing;

struct Field
{
    uint32 value;
};

// field constructor clashes with read constructor
// - field constructor is chosen because the allocator_type parameter is optional in both field constructor
//   and read constructor and compiler choose field constructor with two required parameters rather than
//   exactly matching read constructor
struct CompoundRead
{
    Field field1;
    Field field2;
};

// field constructor clashes with packing read constructor
// - field constructor is chosen because the allocator_type parameter is optional in both field constructor
//   and read constructor and compiler choose field constructor with two required parameters rather than
//   exactly matching packing read constructor
struct CompoundPackingRead
{
    Field field1;
    Field field2;
    Field field3;
};

struct FieldConstructorClashing
{
    CompoundRead compoundReadArray[];
    packed CompoundPackingRead compoundPackingArray[];
};
