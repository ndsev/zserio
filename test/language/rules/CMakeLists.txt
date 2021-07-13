set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/rules/pkg_rules/Test.cpp
    ${ZSERIO_GEN_DIR}/rules/pkg_rules/Test.h
)

zserio_add_library(TARGET rules_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE rules.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
)

add_test_dependencies(rules_zs)
