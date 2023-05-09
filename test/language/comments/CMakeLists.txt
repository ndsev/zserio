set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/comments/classic_doc/bitmask_comments/Permission.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/bitmask_comments/Permission.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/enum_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/enum_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_database_comments/Db.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_database_comments/Db.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_database_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_database_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_table_comments/VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/sql_table_comments/VirtualTable.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_comments/DirectionStructure.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Compound.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Compound.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_Compound.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_Compound.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_string.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_string.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_uint32.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/Field_uint32.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/StructTemplatedField.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/struct_template_comments/StructTemplatedField.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/unknown_tag/Test.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/unknown_tag/Test.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/unknown_tag_matching_prefix/Test.cpp
    ${ZSERIO_GEN_DIR}/comments/classic_doc/unknown_tag_matching_prefix/Test.h
    ${ZSERIO_GEN_DIR}/comments/classic_doc/unknown_tag_matching_prefix/VALUE.h

    ${ZSERIO_GEN_DIR}/comments/markdown_doc/bitmask_comments/Permission.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/bitmask_comments/Permission.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/enum_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/enum_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/indented_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/indented_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/links_in_comments/VALUE.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_database_comments/Db.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_database_comments/Db.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_database_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_database_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_table_comments/VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/sql_table_comments/VirtualTable.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_comments/DirectionStructure.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Compound.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Compound.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_Compound.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_Compound.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_string.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_string.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_uint32.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/Field_uint32.h
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/StructTemplatedField.cpp
    ${ZSERIO_GEN_DIR}/comments/markdown_doc/struct_template_comments/StructTemplatedField.h

    ${ZSERIO_GEN_DIR}/comments/mixed_doc/struct_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/mixed_doc/struct_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/mixed_doc/struct_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/mixed_doc/struct_comments/DirectionStructure.h
)

zserio_add_library(comments_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(comments_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(comments
    DEPENDS
        comments_zs
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
