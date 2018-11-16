package packages_warning.single_type_already_imported_warning;

import packages_warning.simple_database.*;

// This import should produce warning because this single type has been already imported by previous import.
import packages_warning.simple_database.SimpleTable;

sql_database TopDatabase
{
    SimpleTable simpleTable1; // check that SimpleTable isn't ambiguous
};
