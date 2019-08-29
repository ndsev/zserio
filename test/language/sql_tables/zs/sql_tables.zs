package sql_tables;

import sql_tables.blob_param_table.*;
import sql_tables.blob_offsets_param_table.*;
import sql_tables.column_param_table.*;
import sql_tables.complex_table.*;
import sql_tables.const_param_table.*;
import sql_tables.multiple_pk_table.*;
import sql_tables.subtyped_bool_field_table.*;
import sql_tables.subtyped_enum_field_table.*;
import sql_tables.subtyped_table.*;
import sql_tables.without_pk_table.*;

sql_database TestDb
{
    BlobParamTable         blobParamTable;
    BlobOffsetsParamTable  blobOffsetsParamTable;
    ColumnParamTable       columnParamTable;
    ComplexTable           complexTable;
    ConstParamTable        constParamTable;
    MultiplePkTable        multiplePkTable;
    SubtypedBoolFieldTable subtypedBoolFieldTable;
    SubtypedEnumFieldTable subtypedEnumFieldTable;
    SubtypedTable          subtypedTable;
    WithoutPkTable         withoutPkTable;
};
