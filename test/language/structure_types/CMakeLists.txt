set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/structure_types/empty_structure/EmptyStructure.cpp
    ${ZSERIO_GEN_DIR}/structure_types/empty_structure/EmptyStructure.h

    ${ZSERIO_GEN_DIR}/structure_types/empty_structure_with_parameter/EmptyStructureWithParameter.cpp
    ${ZSERIO_GEN_DIR}/structure_types/empty_structure_with_parameter/EmptyStructureWithParameter.h

    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/FieldConstructorClashing.cpp
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/FieldConstructorClashing.h
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/CompoundPackingRead.cpp
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/CompoundPackingRead.h
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/CompoundRead.cpp
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/CompoundRead.h
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/Field.cpp
    ${ZSERIO_GEN_DIR}/structure_types/field_constructor_clashing/Field.h

    ${ZSERIO_GEN_DIR}/structure_types/one_string_structure/OneStringStructure.cpp
    ${ZSERIO_GEN_DIR}/structure_types/one_string_structure/OneStringStructure.h

    ${ZSERIO_GEN_DIR}/structure_types/simple_structure/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR}/structure_types/simple_structure/SimpleStructure.h
)

zserio_add_library(TARGET structure_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE structure_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/EmptyStructureTest.cpp
    cpp/EmptyStructureWithParameterTest.cpp
    cpp/FieldConstructorClashingTest.cpp
    cpp/OneStringStructureTest.cpp
    cpp/SimpleStructureTest.cpp
)

add_test_dependencies(structure_types_zs)
