package structure_types.structure_inner_classes_clashing;

// check that inner classes are correctly renamed in case of possible clashing with structure name

struct ArrayType_array
{
    uint32 array[];
};

struct OffsetChecker_array
{
    uint32 offsets[];
offsets[@index]:
    uint32 array[];
};

struct OffsetInitializer_array
{
    uint32 offsets[];
offsets[@index]:
    uint32 array[];
};
