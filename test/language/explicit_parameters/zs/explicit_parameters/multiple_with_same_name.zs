package explicit_parameters.multiple_with_same_name;

// both parameterized types has single parameters with the same name, but different type

struct Parameterized1(uint32 param)
{
    uint32 field : field < param;
};

struct Parameterized2(float32 param)
{
    float32 field : field < param;
};

sql_table MultipleWithSameNameTable
{
    uint32                          id  sql "PRIMARY KEY NOT NULL";
    string                          name;
    Parameterized1(explicit param1) parameterized1;
    Parameterized2(explicit param2) parameterized2;
};
