set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/optional_members/auto_optional/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members/auto_optional/Container.h

    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BasicColor.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BasicColor.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BlackColor.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BlackColor.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/Container.h
)

zserio_add_library(TARGET optional_members_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE optional_members.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
    cpp/AutoOptionalTest.cpp
    cpp/OptionalMembersTest.cpp
)

add_test_dependencies(optional_members_zs)
