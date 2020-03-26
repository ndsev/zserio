package functions_warning.unconditional_optional_fields_warning;

struct SingleOptionalFieldFunction
{
    uint32  length;
    uint32  additionalValue if length != 0;

    function uint32 suspicionFunction()
    {
        return additionalValue / 2 + 1;
    }
};

// The following just disable "unused type" warning for SingleOptionalFieldFunction type.
sql_table SingleOptionalFieldFunctionTable
{
    int32                               id sql "PRIMARY KEY NOT NULL";
    SingleOptionalFieldFunction         singleOptionalFieldFunction;
};

sql_database SingleOptionalFieldFunctionDatabase
{
    SingleOptionalFieldFunctionTable    singleOptionalFieldFunctionTable;
};
