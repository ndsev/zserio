add_library(comments_warning_zs STATIC ${TEST_ZS_ROOT}/comments_warning.zs)
zserio_generate_cpp(
    TARGET comments_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_comments
    EXTRA_ARGS -withWarnings unused -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_COMMENTS
    OUTPUT_VAR ZSERIO_LOG_COMMENTS
    ERROR_VAR ZSERIO_LOG_COMMENTS
)
target_link_libraries(comments_warning_zs PUBLIC ZserioCppRuntime)
target_include_directories(comments_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_COMMENTS)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_comments.txt "${ZSERIO_LOG_COMMENTS}")
    check_zserio_warnings("${ZSERIO_LOG_COMMENTS}" 24)
endif ()

add_library(doc_comment_missing_warning_zs STATIC ${TEST_ZS_ROOT}/doc_comment_missing_warning.zs)
zserio_generate_cpp(
    TARGET doc_comment_missing_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_doc_comment_missing
    EXTRA_ARGS -withWarnings "doc-comment-missing" -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_DOC_COMMENT_MISSING
    OUTPUT_VAR ZSERIO_LOG_DOC_COMMENT_MISSING
    ERROR_VAR ZSERIO_LOG_DOC_COMMENT_MISSING
)
target_link_libraries(doc_comment_missing_warning_zs PUBLIC ZserioCppRuntime)
target_include_directories(doc_comment_missing_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_DOC_COMMENT_MISSING)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_doc_comment_missing.txt
        "${ZSERIO_LOG_DOC_COMMENT_MISSING}")
    check_zserio_warnings("${ZSERIO_LOG_DOC_COMMENT_MISSING}" 32)
endif ()

add_custom_test(comments_warning
    DEPENDS
        comments_warning_zs
        doc_comment_missing_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CommentsWarningTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DocCommentMissingWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_COMMENTS}
        ${GENERATED_SOURCES_DOC_COMMENT_MISSING}
)
