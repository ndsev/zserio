package explicit_parameters.explicit_bitmask_param;

bitmask bit:5 TestBitmask
{
    TEN = 10,
    ELEVEN = 11,
};

struct TestBlob(TestBitmask count)
{
    bit:3 values[valueof(count)];
};

sql_table BitmaskParamTable
{
    uint32                     id          sql "PRIMARY KEY";
    string                     name;
    TestBlob(explicit count1)  blob1;
    TestBlob(explicit count2)  blob2;
    TestBlob(explicit count1)  blob3; // count1 is reused here
};
