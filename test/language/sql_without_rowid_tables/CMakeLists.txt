set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/OrdinaryRowIdTable.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/rowid_and_without_rowid_tables/WithoutRowIdTable.h

    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdTable.h
)

zserio_add_library(TARGET sql_without_rowid_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_without_rowid_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(sql_without_rowid_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    ${ZSERIO_CPP_DIR}/RowIdAndWithoutRowIdTablesTest.cpp
    ${ZSERIO_CPP_DIR}/SimpleWithoutRowIdTableTest.cpp
)

add_test_dependencies(sql_without_rowid_zs)
