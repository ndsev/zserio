set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3TestDb.h

    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5TestDb.h
)

zserio_add_library(sql_virtual_tables_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_virtual_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(sql_virtual_tables_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(sql_virtual_tables
    DEPENDS
        sql_virtual_tables_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Fts3VirtualTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Fts5VirtualTableTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
