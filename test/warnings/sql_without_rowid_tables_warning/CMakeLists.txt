add_library(sql_without_rowid_tables_warning_zs STATIC ${TEST_ZS_ROOT}/sql_without_rowid_tables_warning.zs)
zserio_generate_cpp(
    TARGET sql_without_rowid_tables_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(sql_without_rowid_tables_warning_zs PUBLIC ZserioCppRuntime)
target_include_directories(sql_without_rowid_tables_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 1)
endif ()

add_custom_test(sql_without_row_id_tables_warning
    DEPENDS
        sql_without_rowid_tables_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SqlWithoutRowIdTablesWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
