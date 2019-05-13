set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/comments/documentation/enum_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/enum_comments/Direction.h

    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/Db.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/Db.h
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/TableRow.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_database_comments/TableRow.h

    ${ZSERIO_GEN_DIR}/comments/documentation/sql_table_comments/VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_table_comments/VirtualTable.h
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_table_comments/VirtualTableRow.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/sql_table_comments/VirtualTableRow.h

    ${ZSERIO_GEN_DIR}/comments/documentation/struct_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/struct_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/documentation/struct_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/struct_comments/DirectionStructure.h

    ${ZSERIO_GEN_DIR}/comments/documentation/unknown_tag/Test.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/unknown_tag/Test.h

    ${ZSERIO_GEN_DIR}/comments/documentation/unknown_tag_matching_prefix/Test.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation/unknown_tag_matching_prefix/Test.h
    ${ZSERIO_GEN_DIR}/comments/documentation/unknown_tag_matching_prefix/VALUE.h
)

zserio_add_library(TARGET comments_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(comments_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
)

add_test_dependencies(comments_zs)
