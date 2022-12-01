set(ZSERIO_GEN_DIR_COMMENTS ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES_COMMENTS
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/OtherTestStruct.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/OtherTestStruct.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestDatabase.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestDatabase.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestStruct.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestStruct.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestTable.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/markdown_comment_with_wrong_terminator/TestTable.h

    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_markdown_see_tag_reference/Test.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_markdown_see_tag_reference/Test.h

    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructString.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructString.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructUInt32.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructUInt32.h

    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Database.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Database.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Table.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Table.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Test.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/Test.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unresolved_see_tag_reference/VALUE.h

    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/BasicComment.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/BasicComment.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByAlignmentAndType.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByAlignmentAndType.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByIndexedOffsetAndType.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByIndexedOffsetAndType.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByOffsetAndType.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByOffsetAndType.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByOptionalAndType.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/ByOptionalAndType.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/Database.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/Database.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleComments.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleComments.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleFieldComments.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleFieldComments.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleUnmaskedComments.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/MultipleUnmaskedComments.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/Table.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/Table.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/UnusedCommentById.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_field_comments/UnusedCommentById.h

    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Database.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Database.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Table.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Table.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Test.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_by_id/Test.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Database.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Database.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Table.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Table.h
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Test.cpp
    ${ZSERIO_GEN_DIR_COMMENTS}/comments_warning/unused_struct_comment_multiple_comments/Test.h
)

set(ZSERIO_GEN_DIR_DOC_COMMENT_MISSING ${CMAKE_CURRENT_BINARY_DIR}/gen_doc_comment_missing)
set(ZSERIO_GENERATED_SOURCES_DOC_COMMENT_MISSING
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/BitmaskType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/BitmaskType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/ChoiceType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/ChoiceType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/CONSTANT.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/EnumType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/EnumType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/PubsubType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/PubsubType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/ServiceType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/ServiceType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/SqlDatabaseType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/SqlDatabaseType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/SqlTableType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/SqlTableType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/StructureTypeSTR.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/StructureTypeSTR.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/StructureTypeU32.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/StructureTypeU32.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/Subtype.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/UnionType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/all_nodes/UnionType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentdServiceType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentdServiceType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedBitmaskType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedBitmaskType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedChoiceType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedChoiceType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DOCUMENTED_CONSTANT.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedEnumType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedEnumType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedPubsubType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedPubsubType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedSqlDatabaseType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedSqlDatabaseType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedSqlTableType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedSqlTableType.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedStructureTypeSTR.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedStructureTypeSTR.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedStructureTypeU32.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedStructureTypeU32.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedSubtype.h
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedUnionType.cpp
    ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}/doc_comment_missing_warning/documented/DocumentedUnionType.h
)

zserio_add_library(TARGET comments_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR_COMMENTS}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_COMMENTS}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withWarnings unused -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 24
    ZSERIO_LOG_FILENAME zserio_log_comments.txt
)

zserio_add_library(TARGET doc_comment_missing_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE doc_comment_missing_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR_DOC_COMMENT_MISSING}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_DOC_COMMENT_MISSING}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withWarnings "doc-comment-missing" -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 32
    ZSERIO_LOG_FILENAME zserio_log_doc_comment_missing.txt
)

target_include_directories(comments_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
target_include_directories(doc_comment_missing_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/CommentsWarningTest.cpp
    cpp/DocCommentMissingWarningTest.cpp
)

add_test_dependencies(comments_warning_zs)
add_test_dependencies(doc_comment_missing_warning_zs)
