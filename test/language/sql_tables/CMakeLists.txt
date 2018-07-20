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

    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTable.h
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/without_pk_table/WithoutPkTableRow.h

    ${ZSERIO_GEN_DIR}/sql_tables/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/sql_tables/MasterDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/MasterDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_tables/TestDb.h
)

zserio_add_library(TARGET sql_tables_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
    cpp/ColumnParamTableTest.cpp
    cpp/ComplexTableTest.cpp
    cpp/ConstParamTableTest.cpp
    cpp/MultiplePkTableTest.cpp
    cpp/WithoutPkTableTest.cpp
)

add_test_dependencies(sql_tables_zs)
