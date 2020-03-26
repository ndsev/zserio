package sql_tables.blob_field_with_children_initialization_table;

struct ParameterizedArray(uint32 length)
{
    uint32 array[length];
};

struct BlobWithChildrenInitialization
{
    uint32                          arrayLength;
    ParameterizedArray(arrayLength) parameterizedArray;
};

sql_table BlobFieldWithChildrenInitializationTable
{
    uint32                          id      sql "PRIMARY KEY NOT NULL";
    BlobWithChildrenInitialization  blob;
};
