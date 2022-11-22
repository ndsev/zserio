package test_object;

enum int8 DummyEnum
{
    ONE,
    TWO,
    MinusOne = -1
};

bitmask uint8 DummyBitmask
{
    READ = 1,
    WRITE = 2
};

struct DummyNested(uint32 param)
{
    uint32 value;
    string text;
    extern externData;
    bytes bytesData;
    DummyEnum dummyEnum;
    DummyBitmask dummyBitmask;
};

struct DummyObject
{
    uint32 value;
    DummyNested(value) nested;
    string text;
    DummyNested(value) nestedArray[];
    string textArray[];
    optional extern externArray[];
    optional bytes bytesArray[];
    optional bool optionalBool;
    optional DummyNested(value) optionalNested;
};

