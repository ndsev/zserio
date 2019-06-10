package sql_tables.blob_param_table;

struct Parameters
{
    uint32 count;
};

struct ParameterizedBlob(Parameters parameters)
{
    uint32  array[parameters.count];
};

sql_table BlobParamTable
{
    uint32                          blobId sql "PRIMARY KEY";
    string                          name sql "NULL";
    Parameters                      parameters sql "NULL";
    ParameterizedBlob(parameters)   blob sql "NULL";
};
