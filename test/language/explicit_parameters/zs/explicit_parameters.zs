package explicit_parameters;

struct TestBlob(uint32 count)
{
    bit:3   values[count];
};

struct TestBlobMultiParam(uint32 countA, uint32 countB)
{
    uint8   valuesA[countA];
    uint16  valuesB[countB];
};

sql_table TestTable
{
    uint32                                                  id          sql "PRIMARY KEY";
    string                                                  name;
    TestBlob(explicit count1)                               blob1;
    TestBlob(explicit count2)                               blob2;
    TestBlob(explicit count1)                               blob3; // count1 is reused here
    TestBlobMultiParam(explicit count2, explicit count2)    blobMultiParam;
};

sql_database TestDb
{
    TestTable   testTable;
};
