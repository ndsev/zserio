add_library(templates_zs STATIC zs/templates.zs)
zserio_generate_cpp(
    TARGET templates_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(templates_zs PUBLIC ZserioCppRuntime)
target_include_directories(templates_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(templates
    DEPENDS
        templates_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceTemplatedEnumSelectorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceTemplatedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceTemplatedSelectorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExpressionBitmaskTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExpressionEnumTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExpressionEnumTemplateArgumentConflictTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExpressionFullTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FunctionTemplatedReturnTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateClashOtherTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateImportedTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateNestedTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateNotImportedTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateOnlyNestedTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateSimpleTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateSqlTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsChoiceFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsParameterTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsSqlDatabaseFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsSqlTableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsStructArrayFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsStructFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeAsTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeImportedAsStructFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateTypeOnParameterizedTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateUnusedTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateViaImportTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateViaSingleImportTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateVsDefaultTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/InstantiateWithInstantiateTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SqlTableTemplatedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructFullAndShortTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructFullNameTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructLongTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructLongTemplateArgumentClashTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructRecursiveTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplateClashTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplateClashAcrossPackagesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplateClashOtherTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplateClashOtherTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplatedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplatedTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplatedTypeArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructTemplateInTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypeTemplateArgumentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypeTemplateWithBuiltinTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypeTemplateWithCompoundTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TemplatedStructRecursionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionTemplatedFieldTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
