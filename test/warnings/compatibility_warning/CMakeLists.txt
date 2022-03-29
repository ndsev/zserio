set(ZSERIO_GEN_DIR_ROOT_WITH ${CMAKE_CURRENT_BINARY_DIR}/gen_root_with)
set(ZSERIO_GEN_DIR_ROOT_WITHOUT ${CMAKE_CURRENT_BINARY_DIR}/gen_root_without)
set(LOG_FILENAME zserio_log.txt)

# clean old log file
file(REMOVE ${CMAKE_CURRENT_BINARY_DIR}/${LOG_FILENAME})

set(ZSERIO_GENERATED_SOURCES_ROOT_WITH
    ${ZSERIO_GEN_DIR_ROOT_WITH}/compatibility_warning/subpackage/TestStruct.cpp
    ${ZSERIO_GEN_DIR_ROOT_WITH}/compatibility_warning/subpackage/TestStruct.h
)

set(ZSERIO_GENERATED_SOURCES_ROOT_WITHOUT
    ${ZSERIO_GEN_DIR_ROOT_WITHOUT}/compatibility_warning/subpackage/TestStruct.cpp
    ${ZSERIO_GEN_DIR_ROOT_WITHOUT}/compatibility_warning/subpackage/TestStruct.h
)

zserio_add_library(TARGET root_with_diff_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_with_diff_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR_ROOT_WITH}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_ROOT_WITH}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(TARGET root_without_compatibility_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE compatibility_warning/root_without_compatibility_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR_ROOT_WITHOUT}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_ROOT_WITHOUT}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

add_test_dependencies(root_with_diff_compatibility_warning_zs)
add_test_dependencies(root_without_compatibility_warning_zs)

add_test_sources(
    cpp/CompatibilityWarningTest.cpp
)
