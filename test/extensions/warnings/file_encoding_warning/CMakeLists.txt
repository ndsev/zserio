add_library(file_encoding_warning_zs INTERFACE ${TEST_ZS_ROOT}/file_encoding_warning.zs)
zserio_generate_cpp(
    TARGET file_encoding_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(file_encoding_warning_zs INTERFACE ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 3)
endif ()

add_custom_test(file_encoding_warning
    DEPENDS
        file_encoding_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FileEncodingWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
