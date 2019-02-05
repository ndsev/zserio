set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/subtypes/enum_subtype/Color.cpp
    ${ZSERIO_GEN_DIR}/subtypes/enum_subtype/Color.h
    ${ZSERIO_GEN_DIR}/subtypes/enum_subtype/ColorSubtype.h
    ${ZSERIO_GEN_DIR}/subtypes/enum_subtype/CONST_BLACK.h

    ${ZSERIO_GEN_DIR}/subtypes/param_structure_subtype/ParameterizedStruct.cpp
    ${ZSERIO_GEN_DIR}/subtypes/param_structure_subtype/ParameterizedStruct.h
    ${ZSERIO_GEN_DIR}/subtypes/param_structure_subtype/ParameterizedSubtype.h
    ${ZSERIO_GEN_DIR}/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.cpp
    ${ZSERIO_GEN_DIR}/subtypes/param_structure_subtype/ParameterizedSubtypeStruct.h

    ${ZSERIO_GEN_DIR}/subtypes/structure_subtype/Student.h
    ${ZSERIO_GEN_DIR}/subtypes/structure_subtype/SubtypeStructure.cpp
    ${ZSERIO_GEN_DIR}/subtypes/structure_subtype/SubtypeStructure.h
    ${ZSERIO_GEN_DIR}/subtypes/structure_subtype/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/subtypes/structure_subtype/TestStructure.h

    ${ZSERIO_GEN_DIR}/subtypes/uint16_subtype/Identifier.h
    ${ZSERIO_GEN_DIR}/subtypes/uint16_subtype/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/subtypes/uint16_subtype/TestStructure.h
)

zserio_add_library(TARGET subtypes_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE subtypes.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/EnumSubtypeTest.cpp
    cpp/ParamStructureSubtypeTest.cpp
    cpp/StructureSubtypeTest.cpp
    cpp/UInt16SubtypeTest.cpp
)

add_test_dependencies(subtypes_zs)
