package subtyped_compound_type_constant_error;

struct Compound
{
    int32 field;
};

subtype int32 Int32Subtype;
subtype Compound CompoundSubtype;

const Int32Subtype Int32SubtypeConst = 0; // valid
const CompoundSubtype CompoundSubtypeConst = 0;
