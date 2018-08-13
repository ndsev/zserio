set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_types/IParameterProvider.h

    ${ZSERIO_GEN_DIR}/sql_types/MasterDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_types/MasterDatabase.h
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesDb.cpp
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesDb.h
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesTable.cpp
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesTable.h
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_types/SqlTypesTableRow.h
    ${ZSERIO_GEN_DIR}/sql_types/TestChoice.cpp
    ${ZSERIO_GEN_DIR}/sql_types/TestChoice.h
    ${ZSERIO_GEN_DIR}/sql_types/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_types/TestEnum.h
    ${ZSERIO_GEN_DIR}/sql_types/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/sql_types/TestStructure.h
    ${ZSERIO_GEN_DIR}/sql_types/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/sql_types/TestUnion.h
)

zserio_add_library(TARGET sql_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/SqlTypesTest.cpp
)

add_test_dependencies(sql_types_zs)
