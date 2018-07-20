package functions_warning.unconditional_auto_optional_fields_warning;

struct SingleAutoOptionalFieldFunction
{
    uint32              length;
    optional uint32     additionalValue;

    function uint32 suspicionFunction()
    {
        return additionalValue / 2 + 1;
    }
};

// The following just disable "unused type" warning for SingleAutoOptionalFieldFunction type.
sql_table SingleAutoOptionalFieldFunctionTable
{
    int32                                   id sql "PRIMARY KEY";
    SingleAutoOptionalFieldFunction         singleAutoOptionalFieldFunction;
};

sql_database SingleAutoOptionalFieldFunctionDatabase
{
    SingleAutoOptionalFieldFunctionTable    singleAutoOptionalFieldFunctionTable;
};
