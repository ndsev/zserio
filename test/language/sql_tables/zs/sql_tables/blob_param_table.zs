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
    uint32                          blobId sql "PRIMARY KEY NOT NULL";
    string                          name;
    Parameters                      parameters;
    ParameterizedBlob(parameters)   blob;
};
