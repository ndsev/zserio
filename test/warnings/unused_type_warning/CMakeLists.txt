add_library(unused_type_warning_zs STATIC zs/unused_type_warning.zs)
zserio_generate_cpp(
    TARGET unused_type_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withWarnings unused -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(unused_type_warning_zs PUBLIC ZserioCppRuntime)
target_include_directories(unused_type_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 6)
endif ()

add_custom_test(unused_type_warning
    DEPENDS
        unused_type_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnusedTypeWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
