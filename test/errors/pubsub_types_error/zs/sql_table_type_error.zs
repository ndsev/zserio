package sql_table_type_error;

sql_table Data
{
    int32 id;
};

pubsub User
{
    pubsub("provider/data") Data data;
};
