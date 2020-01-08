package sql_tables;

import sql_tables.blob_field_with_children_initialization_table.*;
import sql_tables.blob_param_table.*;
import sql_tables.blob_offsets_param_table.*;
import sql_tables.column_param_table.*;
import sql_tables.complex_table.*;
import sql_tables.const_param_table.*;
import sql_tables.dynamic_bit_field_enum_field_table.*;
import sql_tables.multiple_pk_table.*;
import sql_tables.subtyped_bitmask_field_table.*;
import sql_tables.subtyped_bool_field_table.*;
import sql_tables.subtyped_enum_field_table.*;
import sql_tables.subtyped_table.*;
import sql_tables.without_pk_table.*;

sql_database TestDb
{
    BlobFieldWithChildrenInitializationTable blobFieldWithChildrenInitializationTable;
    BlobParamTable                           blobParamTable;
    BlobOffsetsParamTable                    blobOffsetsParamTable;
    ColumnParamTable                         columnParamTable;
    ComplexTable                             complexTable;
    ConstParamTable                          constParamTable;
    DynamicBitFieldEnumFieldTable            dynamicBitFieldEnumFieldTable;
    MultiplePkTable                          multiplePkTable;
    SubtypedBitmaskFieldTable                subtypedBitmaskFieldTable;
    SubtypedBoolFieldTable                   subtypedBoolFieldTable;
    SubtypedEnumFieldTable                   subtypedEnumFieldTable;
    SubtypedTable                            subtypedTable;
    WithoutPkTable                           withoutPkTable;
};
