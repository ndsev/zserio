set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3VirtualTableRow.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts3_virtual_table/Fts3TestDb.h

    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTable.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5VirtualTableRow.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/fts5_virtual_table/Fts5TestDb.h

    ${ZSERIO_GEN_DIR}/sql_virtual_tables/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/MasterDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_virtual_tables/MasterDatabase.h
)

zserio_add_library(TARGET sql_virtual_tables_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_virtual_tables.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
    cpp/Fts3VirtualTableTest.cpp
    cpp/Fts5VirtualTableTest.cpp
)

add_test_dependencies(sql_virtual_tables_zs)
