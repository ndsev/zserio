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
