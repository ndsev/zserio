set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Db.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Db.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/DirectionStructure.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/FieldComments.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/FieldComments.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/TableRow.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/TableRow.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/VirtualTable.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/VirtualTableRow.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/VirtualTableRow.h
    ${ZSERIO_GEN_DIR}/comments/unknown_tag/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/unknown_tag/Table.h
    ${ZSERIO_GEN_DIR}/comments/uknown_tag_matching_prefix/Max.cpp
    ${ZSERIO_GEN_DIR}/comments/uknown_tag_matching_prefix/Max.h
    ${ZSERIO_GEN_DIR}/comments/uknown_tag_matching_prefix/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/uknown_tag_matching_prefix/Table.cpp
)

zserio_add_library(TARGET comments_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -doc ${DOC_GEN_DIR} -withoutSourcesAmalgamation
)

target_include_directories(comments_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
)

add_test_dependencies(comments_zs)
