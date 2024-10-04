add_library(choice_types_warning_zs STATIC ${TEST_ZS_ROOT}/choice_types_warning.zs)
zserio_generate_cpp(
    TARGET choice_types_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(choice_types_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 1)
endif ()

add_custom_test(choice_types_warning
    DEPENDS
        choice_types_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceTypesWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
