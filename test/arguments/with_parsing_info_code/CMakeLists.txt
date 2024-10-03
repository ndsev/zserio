add_library(with_parsing_info_code_zs STATIC ${TEST_ZS_ROOT}/with_parsing_info_code.zs)
zserio_generate_cpp(
    TARGET with_parsing_info_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withParsingInfoCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_parsing_info_code_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(with_parsing_info_code
    DEPENDS
        with_parsing_info_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithParsingInfoCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
