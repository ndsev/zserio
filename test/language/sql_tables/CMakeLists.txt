set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobFieldWithChildrenInitializationTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobFieldWithChildrenInitializationTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobWithChildrenInitialization.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobWithChildrenInitialization.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/ParameterizedArray.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/ParameterizedArray.h

    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/BlobParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/BlobParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/Parameters.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/Parameters.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/BlobOffsetsParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/BlobOffsetsParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/OffsetsHolder.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/OffsetsHolder.h
    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestBlob.h
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestEnum.h

    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTable.h

    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_bool_field_table/SubtypedBoolFieldTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_bool_field_table/SubtypedBoolFieldTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_bool_field_table/SubtypedBool.h

    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/SubtypedEnumFieldTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/SubtypedEnumFieldTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/SubtypedEnum.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/TestEnum.h

    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/Student.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/Student.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/SubtypedTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTable.h

    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.h

    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.h
)

if (NOT ${ZSERIO_CPP_STANDARD} STREQUAL c++11)
    set(ZSERIO_GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
        ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobFieldWithChildrenInitializationTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/blob_field_with_children_initialization_table/BlobFieldWithChildrenInitializationTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/BlobParamTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/blob_param_table/BlobParamTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/BlobOffsetsParamTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/blob_offsets_param_table/BlobOffsetsParamTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_bool_field_table/SubtypedBoolFieldTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_bool_field_table/SubtypedBoolFieldTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/SubtypedEnumFieldTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/subtyped_enum_field_table/SubtypedEnumFieldTableRow.h
        ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.h
    )
endif ()

zserio_add_library(TARGET sql_tables_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
    IGNORE_WARNINGS ON
)

target_include_directories(sql_tables_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    ${ZSERIO_CPP_DIR}/BlobFieldWithChildrenInitializationTableTest.cpp
    ${ZSERIO_CPP_DIR}/BlobParamTableTest.cpp
    ${ZSERIO_CPP_DIR}/BlobOffsetsParamTableTest.cpp
    ${ZSERIO_CPP_DIR}/ColumnParamTableTest.cpp
    ${ZSERIO_CPP_DIR}/ComplexTableTest.cpp
    ${ZSERIO_CPP_DIR}/ConstParamTableTest.cpp
    ${ZSERIO_CPP_DIR}/MultiplePkTableTest.cpp
    ${ZSERIO_CPP_DIR}/SubtypedBoolFieldTableTest.cpp
    ${ZSERIO_CPP_DIR}/SubtypedEnumFieldTableTest.cpp
    ${ZSERIO_CPP_DIR}/SubtypedTableTest.cpp
    ${ZSERIO_CPP_DIR}/WithoutPkTableTest.cpp
)

add_test_dependencies(sql_tables_zs)
