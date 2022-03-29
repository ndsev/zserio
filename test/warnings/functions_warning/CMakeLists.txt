set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/functions_warning/optional_references_in_function/SingleOptionalFieldFunction.cpp
    ${ZSERIO_GEN_DIR}/functions_warning/optional_references_in_function/SingleOptionalFieldFunction.h
)

zserio_add_library(TARGET functions_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE functions_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 2
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_test_sources(
    cpp/FunctionsWarningTest.cpp
)

add_test_dependencies(functions_warning_zs)
