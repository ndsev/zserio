package sql_database_type_error;

sql_table Table
{
    int32 id;
};

sql_database Data
{
    Table table;
};

pubsub Provider
{
    publish("provider/data") Data data;
};
