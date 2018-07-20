package with_inspector_code.last_auto_optional_field;

sql_database LastAutoOptionalDatabase
{
    LastAutoOptionalTable   lastAutoOptionalTable;
};

sql_table LastAutoOptionalTable
{
    uint16                      id sql "PRIMARY KEY";
    LastAutoOptionalStructure   lastAutoOptionalStructure;
};

// This checks the auto optional field which is at the end of whole blob.
struct LastAutoOptionalStructure
{
    int32           value;
    optional uint32 autoOptionalField;
};
