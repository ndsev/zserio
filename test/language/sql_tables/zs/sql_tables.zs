package sql_tables;

import sql_tables.column_param_table.*;
import sql_tables.complex_table.*;
import sql_tables.const_param_table.*;
import sql_tables.multiple_pk_table.*;
import sql_tables.subtyped_table.*;
import sql_tables.without_pk_table.*;

sql_database TestDb
{
    ColumnParamTable    columnParamTable;
    ComplexTable        complexTable;
    ConstParamTable     constParamTable;
    MultiplePkTable     multiplePkTable;
    SubtypedTable       subtypedTable;
    WithoutPkTable      withoutPkTable;
};
