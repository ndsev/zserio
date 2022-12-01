/*
 * Compiled twice using the following command line options:
 *
 * 1. -withTypeInfoCode -withReflectionCode -withoutSourcesAmalgamation -setCppAllocator std
 *    -setTopLevelPackage test_object.std_allocator
 * 2. -withTypeInfoCode -withReflectionCode -withoutSourcesAmalgamation -setCppAllocator polymorphic
 *    -setTopLevelPackage test_object.polymorphic_allocator
 */
enum int8 CreatorEnum
{
    ONE,
    TWO,
    MinusOne = -1
};

bitmask uint8 CreatorBitmask
{
    READ = 1,
    WRITE = 2
};

struct CreatorNested(uint32 param)
{
    uint32 value;
    string text;
    extern externData;
    bytes bytesData;
    CreatorEnum creatorEnum;
    CreatorBitmask creatorBitmask;
};

struct CreatorObject
{
    uint32 value;
    CreatorNested(value) nested;
    string text;
    CreatorNested(value) nestedArray[];
    string textArray[];
    optional extern externArray[];
    optional bytes bytesArray[];
    optional bool optionalBool;
    optional CreatorNested(value) optionalNested;
};

bitmask uint32 WalkerBitmask
{
    ZERO
};

struct WalkerNested
{
    string text;
};

union WalkerUnion
{
    uint32 value;
    string text;
    WalkerNested nestedArray[];
};

struct WalkerObject
{
    uint32 identifier;
    WalkerNested nested if identifier != 0;
    string text;
    WalkerUnion unionArray[];
    optional WalkerUnion optionalUnionArray[];
};

enum int8 ReflectableEnum
{
    VALUE1 = -1,
    VALUE2,
    VALUE3
};

bitmask uint8 ReflectableBitmask
{
    CREATE,
    READ,
    WRITE
};

struct ReflectableNested(int:31 dummyParam, string stringParam)
{
    bit:31 value;

    function bit:31 getValue()
    {
        return value;
    }
};

struct ReflectableObject
{
    string stringField;
    ReflectableNested(13, stringField) reflectableNested;
};

enum uint8 SerializeEnum
{
    VALUE1,
    VALUE2,
    VALUE3
};

struct SerializeNested(int8 param)
{
    uint8 offset;
offset:
    uint32 optionalValue if param >= 0;
};

struct SerializeObject
{
    int8 param;
    SerializeNested(param) nested;
};
