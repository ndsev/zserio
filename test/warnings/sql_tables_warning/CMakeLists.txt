set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_tables_warning/bad_ordered_primary_key_warning/BadOrderedPrimaryKeyDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/bad_ordered_primary_key_warning/BadOrderedPrimaryKeyDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/bad_ordered_primary_key_warning/BadOrderedPrimaryKeyTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/bad_ordered_primary_key_warning/BadOrderedPrimaryKeyTable.h

    ${ZSERIO_GEN_DIR}/sql_tables_warning/duplicated_primary_key_warning/DuplicatedPrimaryKeyDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/duplicated_primary_key_warning/DuplicatedPrimaryKeyDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/duplicated_primary_key_warning/DuplicatedPrimaryKeyTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/duplicated_primary_key_warning/DuplicatedPrimaryKeyTable.h

    ${ZSERIO_GEN_DIR}/sql_tables_warning/multiple_primary_keys_warning/MultiplePrimaryKeysDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/multiple_primary_keys_warning/MultiplePrimaryKeysDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/multiple_primary_keys_warning/MultiplePrimaryKeysTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/multiple_primary_keys_warning/MultiplePrimaryKeysTable.h

    ${ZSERIO_GEN_DIR}/sql_tables_warning/no_primary_key_warning/NoPrimaryKeyDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/no_primary_key_warning/NoPrimaryKeyDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/no_primary_key_warning/NoPrimaryKeyTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/no_primary_key_warning/NoPrimaryKeyTable.h

    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_first_primary_key_warning/NotFirstPrimaryKeyDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_first_primary_key_warning/NotFirstPrimaryKeyDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_first_primary_key_warning/NotFirstPrimaryKeyTable.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_first_primary_key_warning/NotFirstPrimaryKeyTable.h

    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyDatabase.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyTable1.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyTable1.h
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyTable2.cpp
    ${ZSERIO_GEN_DIR}/sql_tables_warning/not_null_primary_key_warning/NotNullPrimaryKeyTable2.h
)

zserio_add_library(TARGET sql_tables_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_tables_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 7
    ZSERIO_LOG_FILENAME zserio_log.txt
)

target_include_directories(sql_tables_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/SqlTablesWarningTest.cpp
)

add_test_dependencies(sql_tables_warning_zs)
