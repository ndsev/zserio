package packages_warning.package_import_overwrite_warning;

import packages_warning.simple_database.SimpleTable;

// This import should produce warning because it overwrites the previous direct type import.
import packages_warning.simple_database.*;

sql_database TopDatabase
{
    SimpleTable simpleTable1; // check that SimpleTable isn't ambiguous
};
