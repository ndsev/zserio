package sql_allocation;

struct DataBlob
{
    uint32 len;
    uint32 magicWithVeryLongNameAndYetLongerName : magicWithVeryLongNameAndYetLongerName != 0;
};

struct ParameterizedBlob(DataBlob dataBlob)
{
    uint32 arr[dataBlob.len];
};

enum uint8 Color
{
    RED,
    GREEN,
    BLUE
};

bitmask uint8 Role
{
    GUEST,
    MEMBER,
    ADMIN
};

sql_table SqlAllocationTable
{
    uint32 idWithVeryLongNameAndYetLongerName sql "PRIMARY KEY NOT NULL";
    string textWithVeryLongNameAndYetLongerName sql "NOT NULL";
    DataBlob dataBlobWithVeryLongNameAndYetLongerName sql "NOT NULL";
    ParameterizedBlob(dataBlobWithVeryLongNameAndYetLongerName)
            parameterizedBlobWithVeryLongNameAndYetLongerName;
    ParameterizedBlob(explicit dataBlob) parameterizedBlobExplicitWithVeryLongNameAndYetLongerName;
    Color colorWithVeryLongNameAndYetLongerName;
    Role roleWithVeryLongNameAndYetLongerName;
};

sql_database SqlAllocationDb
{
    SqlAllocationTable allocationTable;
};
