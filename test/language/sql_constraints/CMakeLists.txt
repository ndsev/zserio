add_library(sql_constraints_zs STATIC zs/sql_constraints.zs)
zserio_generate_cpp(
    TARGET sql_constraints_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(sql_constraints_zs PUBLIC ZserioCppRuntime)
target_include_directories(sql_constraints_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(sql_constraints
    DEPENDS
        sql_constraints_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FieldConstraintsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TableConstraintsTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
