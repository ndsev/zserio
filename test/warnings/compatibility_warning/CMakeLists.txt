set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)

zserio_add_library(TARGET root_with_diff_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_with_diff_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME zserio_log.txt
)

zserio_add_library(TARGET root_without_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_without_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME zserio_log.txt
    APPEND_TO_ZSERIO_LOG_FILE ON
)

add_test_dependencies(root_with_diff_compatibility_warning_zs)
add_test_dependencies(root_without_compatibility_warning_zs)

add_test_sources(
    cpp/CompatibilityWarningTest.cpp
)
