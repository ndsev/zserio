set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/union_types/simple_union/SimpleUnion.h
    ${ZSERIO_GEN_DIR}/union_types/simple_union/SimpleUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameter/TestUnion.h
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameter/TestUnion.cpp
)

zserio_add_library(TARGET union_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE union_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/SimpleUnionTest.cpp
    cpp/UnionWithParameterTest.cpp
)

add_test_dependencies(union_types_zs)
