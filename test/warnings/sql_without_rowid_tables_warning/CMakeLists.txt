set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables_warning/integer_primary_key_warning/WithoutRowIdDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables_warning/integer_primary_key_warning/WithoutRowIdDatabase.h
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables_warning/integer_primary_key_warning/WithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/sql_without_rowid_tables_warning/integer_primary_key_warning/WithoutRowIdTable.h
)

zserio_add_library(TARGET sql_without_rowid_tables_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_without_rowid_tables_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME zserio_log.txt
)

target_include_directories(sql_without_rowid_tables_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/SqlWithoutRowIdTablesWarningTest.cpp
)

add_test_dependencies(sql_without_rowid_tables_warning_zs)
