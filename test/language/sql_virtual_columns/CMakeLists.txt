set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsDb.h
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsTableRow.h

    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsDb.h
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsTableRow.h
)

zserio_add_library(TARGET sql_virtual_columns_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_virtual_columns.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(sql_virtual_columns_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/HiddenVirtualColumnsTest.cpp
    cpp/SimpleVirtualColumnsTest.cpp
)

add_test_dependencies(sql_virtual_columns_zs)
