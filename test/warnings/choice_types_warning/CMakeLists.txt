set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/choice_types_warning/optional_references_in_selector/Selector.cpp
    ${ZSERIO_GEN_DIR}/choice_types_warning/optional_references_in_selector/Selector.h
    ${ZSERIO_GEN_DIR}/choice_types_warning/optional_references_in_selector/TestChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types_warning/optional_references_in_selector/TestChoice.h
)

zserio_add_library(TARGET choice_types_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE choice_types_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 1
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_test_sources(
    cpp/ChoiceTypesWarningTest.cpp
)

add_test_dependencies(choice_types_warning_zs)
