package with_validation_code.column_type_validation;

struct Blob
{
    uint32 id;
};

sql_table ColumnTypeTable
{
    int32 id sql "PRIMARY KEY NOT NULL";
    int8 int8Value;
    int16 int16Value;
    int32 int32Value;
    int64 int64Value;
    uint8 uint8Value;
    uint16 uint16Value;
    uint32 uint32Value;
    uint64 uint64Value;
    float16 float16Value;
    float32 float32Value;
    float64 float64Value;
    string stringValue;
    Blob blobValue;
};

sql_database ColumnTypeDb
{
    ColumnTypeTable columnTypeTable;
};
