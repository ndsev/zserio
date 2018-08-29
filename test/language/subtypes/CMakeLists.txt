set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/subtypes/Identifier.h
    ${ZSERIO_GEN_DIR}/subtypes/Student.h
    ${ZSERIO_GEN_DIR}/subtypes/SubtypedTable.h

    ${ZSERIO_GEN_DIR}/subtypes/Database.h
    ${ZSERIO_GEN_DIR}/subtypes/Database.cpp
    ${ZSERIO_GEN_DIR}/subtypes/SubtypeStructure.h
    ${ZSERIO_GEN_DIR}/subtypes/SubtypeStructure.cpp
    ${ZSERIO_GEN_DIR}/subtypes/TestStructure.h
    ${ZSERIO_GEN_DIR}/subtypes/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/subtypes/TestTable.h
    ${ZSERIO_GEN_DIR}/subtypes/TestTable.cpp
    ${ZSERIO_GEN_DIR}/subtypes/TestTableRow.h
    ${ZSERIO_GEN_DIR}/subtypes/TestTableRow.cpp

    ${ZSERIO_GEN_DIR}/subtypes/ParameterizedStruct.cpp
    ${ZSERIO_GEN_DIR}/subtypes/ParameterizedStruct.h
    ${ZSERIO_GEN_DIR}/subtypes/ParameterizedSubtype.h
    ${ZSERIO_GEN_DIR}/subtypes/ParameterizedSubtypeStruct.cpp
    ${ZSERIO_GEN_DIR}/subtypes/ParameterizedSubtypeStruct.h
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
    cpp/SubtypesTest.cpp
)

add_test_dependencies(subtypes_zs)
