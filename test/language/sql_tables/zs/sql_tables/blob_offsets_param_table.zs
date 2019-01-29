package sql_tables.blob_offsets_param_table;

struct OffsetsHolder
{
    uint32 offsets[];
};

struct ParameterizedBlob(OffsetsHolder offsetsHolder)
{
offsetsHolder.offsets[@index]:
    uint32  array[];
};

sql_table BlobOffsetsParamTable
{
    uint32                           blobId sql "PRIMARY KEY";
    string                           name;
    OffsetsHolder                    offsetsHolder;
    ParameterizedBlob(offsetsHolder) blob;
};
