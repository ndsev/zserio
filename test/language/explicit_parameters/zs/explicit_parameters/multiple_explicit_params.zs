package explicit_parameters.multiple_explicit_params;

struct TestBlob(uint32 count8, uint32 count16)
{
    uint8    values8[count8];
    uint16   values16[count16];
};

sql_table MultipleParamsTable
{
    uint32                                      id          sql "PRIMARY KEY";
    string                                      name;
    TestBlob(explicit count1, explicit count2)  blob1;
    TestBlob(explicit count, explicit count)    blob2;
    TestBlob(explicit count1, explicit count1)  blob3; // count1 is reused here
};
