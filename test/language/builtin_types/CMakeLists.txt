set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/AllBuiltInTypes.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/AllBuiltInTypes.h

    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_uint64_length/Container.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_uint64_length/Container.h
)

zserio_add_library(TARGET builtin_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE builtin_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/AllBuiltInTypesTest.cpp
    ${ZSERIO_CPP_DIR}/BitFieldUInt64LengthTest.cpp
)

add_test_dependencies(builtin_types_zs)
