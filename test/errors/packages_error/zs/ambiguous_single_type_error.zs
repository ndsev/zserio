package ambiguous_single_type_error;

import simple_database.SimpleTable;
import complex_database.SimpleTable;

sql_database MyDatabase
{
    simple_database.SimpleTable     simpleTable1;
    complex_database.SimpleTable    simpleTable2;

    // This is an ambiguous error because SimpleTable is defined in two different single type imports.
    SimpleTable simpleTable3;
};
