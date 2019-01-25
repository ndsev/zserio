set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ColumnParamTableRow.h
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/column_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/ComplexTableRow.h
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestBlob.h
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/complex_table/TestEnum.h

    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ConstParamTableRow.h
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/const_param_table/ParameterizedBlob.h

    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/multiple_pk_table/MultiplePkTableRow.h

    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/Student.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/Student.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/SubtypedTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/subtyped_table/TestTableRow.h

    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.h

    ${ZSERIO_GEN_DIR}/sql_tables/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.h
)

zserio_add_library(TARGET sql_tables_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    IGNORE_WARNINGS ON
)

add_test_sources(
    cpp/ColumnParamTableTest.cpp
    cpp/ComplexTableTest.cpp
    cpp/ConstParamTableTest.cpp
    cpp/MultiplePkTableTest.cpp
    cpp/SubtypedTableTest.cpp
    cpp/WithoutPkTableTest.cpp
)

add_test_dependencies(sql_tables_zs)
