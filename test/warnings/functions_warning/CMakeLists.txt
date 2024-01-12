add_library(functions_warning_zs STATIC zs/functions_warning.zs)
zserio_generate_cpp(
    TARGET functions_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(functions_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 2)
endif ()

add_custom_test(functions_warning
    DEPENDS
        functions_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FunctionsWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
