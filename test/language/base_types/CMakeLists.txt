set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/base_types/BaseTypes.cpp
    ${ZSERIO_GEN_DIR}/base_types/BaseTypes.h
)

zserio_add_library(TARGET base_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE base_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
    cpp/BaseTypesTest.cpp
)

add_test_dependencies(base_types_zs)
