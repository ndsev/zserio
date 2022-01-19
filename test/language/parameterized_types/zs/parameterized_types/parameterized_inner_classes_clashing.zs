package parameterized_types.parameterized_inner_classes_clashing;

// check that inner classes are correctly renamed in case of possible clashing with parameterized type name

struct Compound(uint32 param)
{
    uint32 field : field < param;
};

struct ElementInitializer_array
{
    uint32 param;
    Compound(param) array[];
};

struct ElementFactory_array
{
    uint32 param;
    Compound(param) array[];
};

struct Parent
{
    uint32 param;
    Compound(param) compound;
};

struct ElementChildrenInitializer_array
{
    Parent array[];
};
