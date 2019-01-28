package explicit_parameters.explicit_simple_param;

struct TestBlob(uint32 count)
{
    bit:3   values[count];
};

sql_table SimpleParamTable
{
    uint32                     id          sql "PRIMARY KEY";
    string                     name;
    TestBlob(explicit count1)  blob1;
    TestBlob(explicit count2)  blob2;
    TestBlob(explicit count1)  blob3; // count1 is reused here
};
