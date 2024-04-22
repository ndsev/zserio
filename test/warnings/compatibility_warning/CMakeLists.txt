add_library(root_with_diff_compatibility_warning_zs STATIC
    zs/compatibility_warning/root_with_diff_compatibility_warning.zs
)
zserio_generate_cpp(
    TARGET root_with_diff_compatibility_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_root_with
    EXTRA_ARGS -withoutCrossExtensionCheck -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_ROOT_WITH
    OUTPUT_VAR ZSERIO_LOG_ROOT_WITH
    ERROR_VAR ZSERIO_LOG_ROOT_WITH
)
target_link_libraries(root_with_diff_compatibility_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_ROOT_WITH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_root_with.txt "${ZSERIO_LOG_ROOT_WITH}")
    check_zserio_warnings("${ZSERIO_LOG_ROOT_WITH}" 1)
endif ()

add_library(root_without_compatibility_warning_zs STATIC
    zs/compatibility_warning/root_without_compatibility_warning.zs
)
zserio_generate_cpp(
    TARGET root_without_compatibility_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_root_without
    EXTRA_ARGS -withoutCrossExtensionCheck -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_ROOT_WITHOUT
    OUTPUT_VAR ZSERIO_LOG_ROOT_WITHOUT
    ERROR_VAR ZSERIO_LOG_ROOT_WITHOUT
)
target_link_libraries(root_without_compatibility_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_ROOT_WITHOUT)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_root_without.txt "${ZSERIO_LOG_ROOT_WITHOUT}")
    check_zserio_warnings("${ZSERIO_LOG_ROOT_WITHOUT}" 1)
endif ()

add_custom_test(compatibility_warning
    DEPENDS
        root_with_diff_compatibility_warning_zs
        root_without_compatibility_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CompatibilityWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_ROOT_WITH}
        ${GENERATED_SOURCES_ROOT_WITHOUT}
)
