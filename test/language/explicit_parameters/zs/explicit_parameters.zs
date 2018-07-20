package explicit_parameters;

struct TestBlob(uint32 count)
{
    bit:3   values[count];
};

sql_table TestTable
{
    uint32                      id          sql "PRIMARY KEY";
    string                      name;
    TestBlob(explicit count1)   blob1;
    TestBlob(explicit count2)   blob2;
};

sql_database TestDb
{
    TestTable   testTable;
};
