package with_validation_code.blob_table_validation;

// This zserio checks blob validation (binary compare) using blob which has none standard bits (bits
// which are not set by Java writer).

struct Blob
{
    bool        hasNan;
    float16     nanValue;       // this does not have to be Java writer NaN (0x7E00)

align(8):                       // this skips 7 bits which do not have to be zero
    uint8       endValue;
};

sql_table BlobTable
{
    uint32      id  sql "PRIMARY KEY";
    Blob        blob;
};

sql_database BlobTableValidationDb
{
    BlobTable   blobTable;
};
