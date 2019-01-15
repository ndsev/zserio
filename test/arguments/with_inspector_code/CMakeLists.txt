set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_inspector_code/IInspectorParameterProvider.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorZserioNames.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorZserioNames.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorZserioTypeNames.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorZserioTypeNames.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorParameterProvider.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/InspectorParameterProvider.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/ISqlDatabaseInspector.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/ISqlTableInspector.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/MasterDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/MasterDatabase.h

    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalDatabase.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalStructure.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalStructure.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalTable.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalTable.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalTableRow.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_auto_optional_field/LastAutoOptionalTableRow.h

    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/AutoOptionalStructure.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/AutoOptionalStructure.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnion.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnion.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionDatabase.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionTable.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionTable.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionTableRow.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/last_union_with_auto_optional/LastUnionTableRow.h

    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/RootStructure.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/RootStructure.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/SimpleEnum.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/SimpleEnum.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/SimpleStructure.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestAlign.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestAlign.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestArray.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestArray.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestChoice.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestChoice.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestDatabase.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestTable.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestTable.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestTableRow.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestTableRow.h
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/with_inspector_code/parameterized_structure/TestUnion.h
)

zserio_add_library(TARGET with_inspector_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_inspector_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withInspectorCode -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/LastAutoOptionalFieldTest.cpp
    cpp/LastUnionWithAutoOptionalTest.cpp
    cpp/ParameterizedStructureTest.cpp
)

add_test_dependencies(with_inspector_code_zs)
