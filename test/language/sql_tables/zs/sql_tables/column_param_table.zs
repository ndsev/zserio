package sql_tables.column_param_table;

struct ParameterizedBlob(uint32 param)
{
    uint32  value;

    function uint32 getDoubledParam()
    {
        return param + param;
    }
};

sql_table ColumnParamTable
{
    uint32                          id          sql "PRIMARY KEY";
    string                          name;
    ParameterizedBlob(id / 2)       blob;
};
