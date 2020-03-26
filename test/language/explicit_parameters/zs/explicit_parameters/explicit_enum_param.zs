package explicit_parameters.explicit_enum_param;

enum bit:5 TestEnum
{
    TEN = 10,
    ELEVEN,
};

struct TestBlob(TestEnum count)
{
    bit:3 values[valueof(count)];
};

sql_table EnumParamTable
{
    uint32                     id          sql "PRIMARY KEY NOT NULL";
    string                     name;
    TestBlob(explicit count1)  blob1;
    TestBlob(explicit count2)  blob2;
    TestBlob(explicit count1)  blob3; // count1 is reused here
};
