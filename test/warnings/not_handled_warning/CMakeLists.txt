add_library(not_handled_warning_zs STATIC ${TEST_ZS_ROOT}/not_handled_warning.zs)
zserio_generate_cpp(
    TARGET not_handled_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(not_handled_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 2)
endif ()

add_custom_test(not_handled_warning
    DEPENDS
        not_handled_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/NotHandledWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
