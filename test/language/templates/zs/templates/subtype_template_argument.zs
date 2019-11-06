package templates.subtype_template_argument;

struct Compound
{
    uint32  value;
};

struct Field<T>
{
    T value;
};

subtype uint32 UInt32Type;
subtype Compound CompoundType;

struct SubtypeTemplateArgument
{
    Field<AnotherUInt32Type>    anotherUint32TypeField;
    Field<UInt32Type>           uint32TypeField;
    Field<uint32>               uint32Field;

    Field<AnotherCompoundType>  anotherCompoundTypeField;
    Field<CompoundType>         compoundTypeField;
    Field<Compound>             compoundField;
};

// define at the end to check correct template argument resolution in resolve phase!
subtype UInt32Type AnotherUInt32Type;
subtype CompoundType AnotherCompoundType;
