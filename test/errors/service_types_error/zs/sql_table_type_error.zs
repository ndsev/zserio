package sql_table_type_error;

struct Response
{
    uint64 value;
};

sql_table Request
{
    int32 id;
};

service Service
{
    rpc Response powerOfTwo(Request);
};
