package identifiers.structure_name_clashing_with_java;

// note: optional is used intentionally to force Java generator to use "wrapper classes" for primitime types

struct Byte
{
    optional int8 value;
};

struct Short
{
    optional int16 value;
};

struct Integer
{
    optional int32 value;
};

struct Long
{
    optional int64 value;
};

struct BigInteger
{
    optional uint64 value;
};

struct Float
{
    optional float32 value;
};

struct Double
{
    optional float64 value;
};

struct String
{
    optional string value;
};

struct StructureNameClashingWithJava
{
    Byte        byteField;
    Short       shortField;
    Integer     integerField;
    Long        longField;
    BigInteger  bigIntegerField;
    Float       floatField;
    Double      doubleField;
    String      stringField;
};
