set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTable.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTableRow.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTable.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTableRow.h

    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTable.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTableRow.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/IParameterProvider.h
)

zserio_add_library(TARGET sql_without_rowid_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_without_rowid_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/RowIdAndWithoutRowIdTablesTest.cpp
    cpp/SimpleWithoutRowIdTableTest.cpp
)

add_test_dependencies(sql_without_rowid_zs)
