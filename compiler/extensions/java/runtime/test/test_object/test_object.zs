/*
 * Compile from the 'test' directory using the following command line options:
 *
 * -src test_object test_object.zs -java . -withTypeInfoCode
 */

package test_object;

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
    bit:3 value;
};
