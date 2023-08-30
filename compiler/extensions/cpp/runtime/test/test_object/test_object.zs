/*
 * Compiled twice from the 'test' directory using the following command line options:
 *
 * 1. -src test_object test_object.zs -cpp . -withTypeInfoCode -withReflectionCode -withoutSourcesAmalgamation
 *    -setCppAllocator std -setTopLevelPackage test_object.std_allocator
 * 2. -src test_object test_object.zs -cpp . -withTypeInfoCode -withReflectionCode -withoutSourcesAmalgamation
 *    -setCppAllocator polymorphic -setTopLevelPackage test_object.polymorphic_allocator
 */

enum int8 CreatorEnum
{
    ONE,
    TWO,
    MinusOne = -1
};

enum uint8 CreatorUnsignedEnum
{
    ONE,
    TWO
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

choice WalkerChoice(uint8 selector) on selector
{
    case 8:
        uint8 value8;

    case 16:
        uint16 value16;

    case 32:
        uint32 value32;

    case 64:
        uint64 value64;

    default:
        ;
};

struct WalkerObject
{
    uint32 identifier;
    WalkerNested nested if identifier != 0;
    string text;
    WalkerUnion unionArray[];
    optional WalkerUnion optionalUnionArray[];
    uint8 choiceSelector;
    WalkerChoice(choiceSelector) choiceField;
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

bitmask uint8 ReflectableUtilBitmask
{
    READ,
    WRITE
};

enum int8 ReflectableUtilEnum
{
    ONE,
    TWO
};

choice ReflectableUtilChoice(uint8 param) on param
{
    case 1:
    case 2:
        uint32 array[];
    default:
        ;
};

struct ReflectableUtilObject
{
    uint8 choiceParam;
    ReflectableUtilChoice(choiceParam) reflectableUtilChoice;
};

union ReflectableUtilUnion
{
    ReflectableUtilEnum reflectableUtilEnum;
    ReflectableUtilBitmask reflectableUtilBitmask;
    ReflectableUtilObject reflectableUtilObject;
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

struct DebugStringObject
{
    string text = "test";
};

struct DebugStringParamObject(int32 param)
{
    string text = "test";
};

bitmask uint8 ArrayBitmask
{
    CREATE,
    READ,
    WRITE
};

enum int8 ArrayEnum
{
    VALUE1,
    VALUE2,
    VALUE3
};

struct ArrayObject
{
    bit:31 value;
};
