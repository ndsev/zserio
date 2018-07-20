package with_inspector_code.parametrized_structure;

sql_database TestDatabase
{
    TestTable   testTable;
};

// Please note that this table has multiple instances of parametrized type using different parameters. Such
// test is necessary to check generation of ParameterProvider for Blob Inspector.
sql_table TestTable
{
    uint16                                  id sql "PRIMARY KEY";
    RootStructure(id, explicit rootArg)     rootStructure;
    uint16                                  extraId;
    uint8                                   extraRootArg;
    RootStructure(extraId, extraRootArg)    extraRootStructure;
};

struct RootStructure(uint16 id, uint8 rootArg)
{
    string              name;
    SimpleEnum          simpleEnum;

    uint8               extra if id == 0x00;
    optional bit:7      autoOptional;

    TestAlign           testAlign;
    TestArray           testArray;
    TestUnion           testUnion;
    TestChoice(rootArg) testChoice;

    function bool isIdValid()
    {
        return id != 0x00;
    }
};

struct TestAlign
{
align(32):
    uint8       aligned32;
    uint8       extraOffset;

extraOffset:
    uint8       offsetField;
};

struct TestArray
{
    uint8           arraySize;
    int16           arrayInt16[arraySize];
    SimpleStructure arraySimpleStructure[arraySize];
    SimpleEnum      arrayEnum[arraySize];
};

union TestUnion
{
    uint8       arg8;
    uint16      arg16;
};

choice TestChoice(uint8 arg) on arg
{
    case 0:
        uint8   arg8;

    default:
        uint16  arg16;
};

struct SimpleStructure
{
    uint8 a;
    uint8 b;
};

enum uint8 SimpleEnum
{
    EnumFoo,
    EnumBar,
    EnumBaz
};
