set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(LOG_FILENAME zserio_log.txt)

# clean old log file
file(REMOVE ${CMAKE_CURRENT_BINARY_DIR}/${LOG_FILENAME})

zserio_add_library(TARGET root_with_diff_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_with_diff_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    EXPECTED_WARNINGS 1
)

zserio_add_library(TARGET root_without_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_without_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    EXPECTED_WARNINGS 1
)

add_test_dependencies(root_with_diff_compatibility_warning_zs)
add_test_dependencies(root_without_compatibility_warning_zs)

add_test_sources(
    cpp/CompatibilityWarningTest.cpp
)
