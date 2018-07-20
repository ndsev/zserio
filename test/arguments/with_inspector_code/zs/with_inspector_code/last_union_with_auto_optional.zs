package with_inspector_code.last_union_with_auto_optional;

sql_database LastUnionDatabase
{
    LastUnionTable      lastUnionTable;
};

sql_table LastUnionTable
{
    uint16              id sql "PRIMARY KEY";
    LastUnion           lastUnion;
};

// This checks the union field which is at the end of whole blob.
union LastUnion
{
    AutoOptionalStructure   autoOptionalStructure;
    uint16                  value;
};

struct AutoOptionalStructure
{
    optional uint32 autoOptionalField;
};
