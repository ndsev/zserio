package optional_members.optional_array;

struct Data8
{
    int8 data;
};

// test optional arrays setters and getters especially for ObjectArray<?>
struct TestStruct
{
    bool            hasData8;
    Data8           data8[] if hasData8;
    optional Data8  autoData8[];
    int16           data16[] if !hasData8;
    optional int16  autoData16[];
};
