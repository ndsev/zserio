add_library(extended_members_zs STATIC ${TEST_ZS_ROOT}/extended_members.zs)
zserio_generate_cpp(
    TARGET extended_members_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(extended_members_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(extended_members
    DEPENDS
        extended_members_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedChoiceFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedCompoundFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedEmptyStructureTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedFieldInTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedIndexedOffsetsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedOptionalParameterizedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedSimpleFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedUnalignedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExtendedFieldsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExtendedFieldsVariousTypesTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
