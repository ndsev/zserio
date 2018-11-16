package ambiguous_single_type_error;

// To check ambiguous error more deeply, the first one is package import and the second one is type import.
import simple_database.*;
import complex_database.SimpleTable;

sql_database MyDatabase
{
    simple_database.SimpleTable     simpleTable1;
    complex_database.SimpleTable    simpleTable2;

    // This is an ambiguous error because SimpleTable is defined in two different single type imports.
    SimpleTable simpleTable3;
};
