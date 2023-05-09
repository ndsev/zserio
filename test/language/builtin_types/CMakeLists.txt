set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/AllBuiltInTypes.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/AllBuiltInTypes.h

    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/ExternalStructure.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/ExternalStructure.h

    ${ZSERIO_GEN_DIR}/builtin_types/all_builtin_types/NUM_BITS.h

    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_function_length/Container.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_function_length/Container.h

    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_uint64_length/Container.cpp
    ${ZSERIO_GEN_DIR}/builtin_types/bitfield_uint64_length/Container.h
)

zserio_add_library(builtin_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE builtin_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(builtin_types
    DEPENDS
        builtin_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AllBuiltInTypesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitFieldFunctionLengthTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitFieldUInt64LengthTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
