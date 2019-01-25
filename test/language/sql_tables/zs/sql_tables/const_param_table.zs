package sql_tables.const_param_table;

struct ParameterizedBlob(uint32 param)
{
    uint32  value;

    function uint32 getDoubledParam()
    {
        return param + param;
    }
};

sql_table ConstParamTable
{
    uint32                  blobId  sql "PRIMARY KEY";
    string                  name;
    ParameterizedBlob(2)    blob;
};
