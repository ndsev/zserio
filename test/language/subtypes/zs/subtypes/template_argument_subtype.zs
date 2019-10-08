package subtypes.template_argument_subtype;

struct Compound
{
    uint32  value;
};

struct Field<T>
{
    T value;
};

subtype uint32 UInt32Type;
subtype UInt32Type AnotherUInt32Type;

subtype Compound CompoundType;
subtype CompoundType AnotherCompoundType;

struct TemplateArgumentStructure
{
    Field<AnotherUInt32Type>    anotherUint32TypeField;
    Field<UInt32Type>           uint32TypeField;
    Field<uint32>               uint32Field;

    Field<AnotherCompoundType>  anotherCompoundTypeField;
    Field<CompoundType>         compoundTypeField;
    Field<Compound>             compoundField;
};
