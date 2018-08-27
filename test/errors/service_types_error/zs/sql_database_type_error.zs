package sql_database_type_error;

struct Response
{
    uint64 value;
};

sql_table Table
{
    int32 id;
};

sql_database Request
{
    Table table;
};

service Service
{
    rpc Response powerOfTwo(Request);
};
