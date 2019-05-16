set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Block.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Block.h
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/BlockHeader.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/BlockHeader.h
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Database.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Database.h

    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/GrandChildParam.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/GrandChildParam.h
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/Item.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/Item.h
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoice.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoice.h
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoiceHolder.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoiceHolder.h

    ${ZSERIO_GEN_DIR}/parameterized_types/simple_param/Item.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/simple_param/Item.h

    ${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/ParamType.h
    ${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParam.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParam.h
    ${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParamHolder.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParamHolder.h
)

zserio_add_library(TARGET parameterized_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE parameterized_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ArrayElementParamTest.cpp
    cpp/GrandChildParamTest.cpp
    cpp/SimpleParamTest.cpp
    cpp/SubtypedBitfieldParamTest.cpp
)

add_test_dependencies(parameterized_types_zs)
