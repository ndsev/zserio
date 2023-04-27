set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/StructureDefaultValues.cpp
    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/StructureDefaultValues.h

    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/BasicColor.cpp
    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/BasicColor.h

    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/Permission.cpp
    ${ZSERIO_GEN_DIR}/default_values/structure_default_values/Permission.h
)

zserio_add_library(default_values_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE default_values.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(default_values
    DEPENDS
        default_values_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureDefaultValuesTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
