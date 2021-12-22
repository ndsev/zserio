package with_type_info_code.type_info;

struct SimpleStruct
{
align(8):
    uint32 fieldU32 = 0;
fieldU32:
    string fieldString = "My" + "String";
    bool fieldBool = false;
    float16 fieldFloat16 = 1.0f;
    float32 fieldFloat32;
    float64 fieldFloat64 = 2.0;
};

struct ComplexStruct
{
    optional SimpleStruct simpleStruct;
    uint32 array[] : lengthof(array) > 0;
    int:5 arrayWithLen[array[0]] if array[0] > 0;
    optional ParameterizedStruct(simpleStruct) paramStructArray[];
    bit<simpleStruct.fieldU32> dynamicBitField;
    packed bit<dynamicBitField * 2> dynamicBitFieldArray[];

    function uint32 firstArrayElement()
    {
        return lengthof(array) > 0 ? array[0] : 0;
    }
};

struct ParameterizedStruct(SimpleStruct simple)
{
    uint8 array[simple.fieldU32];
};

struct RecursiveStruct
{
    uint32 fieldU32;
    optional RecursiveStruct fieldRecursion;
    RecursiveStruct arrayRecursion[];
};

union RecursiveUnion
{
    uint32 fieldU32;
    RecursiveUnion recursive[];
};

choice RecursiveChoice(bool param1, bool param2) on param1
{
    case true:
        RecursiveChoice(param2, false) recursive[];
    case false:
        uint32 fieldU32;
};

subtype uint16 EnumUnderlyingType;

enum EnumUnderlyingType TestEnum
{
    One,
    TWO = 5,
    ItemThree
};

bitmask bit<10> TestBitmask
{
    RED,
    Green,
    ColorBlue
};

union SimpleUnion
{
    TestBitmask testBitmask;
    SimpleStruct simpleStruct;

    function uint32 simpleStructFieldU32()
    {
        return simpleStruct.fieldU32;
    }
};

choice SimpleChoice(TestEnum selector) on selector
{
    case One:
        ; // empty
    case TWO:
        SimpleUnion fieldTwo;
    default:
        string fieldDefault;

    function uint32 fieldTwoFuncCall()
    {
        return fieldTwo.simpleStructFieldU32();
    }
};

struct TemplatedStruct<T>
{
    T field;
};

instantiate TemplatedStruct<uint32> TS32;

struct TemplatedParameterizedStruct<T>(T param)
{
    uint32 array[param.field];
};

subtype SimpleStruct SubtypedSimpleStruct;

struct WithTypeInfoCode
{
    SubtypedSimpleStruct simpleStruct;
    ComplexStruct complexStruct;
    ParameterizedStruct(simpleStruct) parameterizedStruct;
    RecursiveStruct recursiveStruct;
    RecursiveUnion recursiveUnion;
    RecursiveChoice(true, false) recursiveChoice;
    TestEnum selector;
    SimpleChoice(selector) simpleChoice;
    TemplatedStruct<uint32> templatedStruct;
    TemplatedParameterizedStruct<TemplatedStruct<uint32>>(templatedStruct) templatedParameterizedStruct;
    extern externData;
    implicit uint32 implicitArray[];
};

sql_table SqlTable
{
    uint32 pk sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_table TemplatedSqlTable<T>
{
    T pk sql "NOT NULL";
    WithTypeInfoCode withTypeInfoCode;

    sql "PRIMARY KEY(pk)";
};

sql_table Fts4Table using fts4
{
    sql_virtual int64 docId;
    string searchTags;
};

sql_table WithoutRowIdTable
{
    uint32 pk1 sql "NOT NULL";
    uint32 pk2 sql "NOT NULL";

    sql "PRIMARY KEY(pk1, pk2)";
    sql_without_rowid;
};

instantiate TemplatedSqlTable<uint8> TemplatedSqlTableU8;

sql_database SqlDatabase
{
    SqlTable sqlTable;
    TemplatedSqlTable<uint32> templatedSqlTableU32;
    TemplatedSqlTableU8 templatedSqlTableU8;
    Fts4Table fts4Table;
    WithoutRowIdTable withoutRowIdTable;
};

pubsub SimplePubsub
{
    publish topic("simpleStruct") SimpleStruct pubSimpleStruct;
    subscribe topic("simpleStruct") SimpleStruct subSimpleStruct;
};

service SimpleService
{
    SimpleStruct getSimpleStruct(SimpleUnion);
};
