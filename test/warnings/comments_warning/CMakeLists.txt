set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/OtherTestStruct.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/OtherTestStruct.h
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestDatabase.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestDatabase.h
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestStruct.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestStruct.h
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestTable.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/markdown_comment_with_wrong_terminator/TestTable.h

    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_markdown_see_tag_reference/Test.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_markdown_see_tag_reference/Test.h

    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructString.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructString.h
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructUInt32.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_in_templated_struct/TemplatedStructUInt32.h

    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Database.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Database.h
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Table.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Table.h
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Test.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/Test.h
    ${ZSERIO_GEN_DIR}/comments_warning/unresolved_see_tag_reference/VALUE.h

    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/BasicComment.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/BasicComment.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByAlignmentAndType.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByAlignmentAndType.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByIndexedOffsetAndType.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByIndexedOffsetAndType.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByOffsetAndType.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByOffsetAndType.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByOptionalAndType.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/ByOptionalAndType.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/Database.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/Database.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleComments.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleComments.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleFieldComments.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleFieldComments.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleUnmaskedComments.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/MultipleUnmaskedComments.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/UnusedCommentById.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_field_comments/UnusedCommentById.h

    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Database.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Database.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Table.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Table.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Test.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_by_id/Test.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Database.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Database.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Table.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Table.h
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Test.cpp
    ${ZSERIO_GEN_DIR}/comments_warning/unused_struct_comment_multiple_comments/Test.h
)

zserio_add_library(TARGET comments_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withWarnings unused -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 18
    ZSERIO_LOG_FILENAME zserio_log.txt
)

target_include_directories(comments_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/CommentsWarningTest.cpp
)

add_test_dependencies(comments_warning_zs)
