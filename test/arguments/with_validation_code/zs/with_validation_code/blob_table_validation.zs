package with_validation_code.blob_table_validation;

// This can check binary compare during blob validation (if implemented) using blob which has none standard
// bits (bits which are not set by zserio writers).

struct Blob
{
    bool        hasNan;
    float16     nanValue;       // this does not have to be writer NaN (0x7E00 in Java)

align(8):                       // this skips 7 bits which do not have to be zero
    uint8       endValue;
};

sql_table BlobTable
{
    uint32      id sql "PRIMARY KEY NOT NULL";
    Blob        blob sql "NOT NULL";
    Blob        nullableBlob;
};

sql_database BlobTableValidationDb
{
    BlobTable   blobTable;
};
