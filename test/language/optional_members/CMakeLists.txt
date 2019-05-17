set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/optional_members/auto_optional/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members/auto_optional/Container.h

    ${ZSERIO_GEN_DIR}/optional_members/optional_array_recursion/Employee.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_array_recursion/Employee.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_array_recursion/Title.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_array_recursion/Title.h

    ${ZSERIO_GEN_DIR}/optional_members/optional_array/TestStruct.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_array/TestStruct.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_array/Data8.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_array/Data8.h

    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BasicColor.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BasicColor.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BlackColor.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/BlackColor.h
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_expression/Container.h

    ${ZSERIO_GEN_DIR}/optional_members/optional_recursion/Block.cpp
    ${ZSERIO_GEN_DIR}/optional_members/optional_recursion/Block.h
)

zserio_add_library(TARGET optional_members_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE optional_members.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/AutoOptionalTest.cpp
    cpp/OptionalArrayRecursionTest.cpp
    cpp/OptionalArrayTest.cpp
    cpp/OptionalMembersTest.cpp
    cpp/OptionalRecursionTest.cpp
)

add_test_dependencies(optional_members_zs)
