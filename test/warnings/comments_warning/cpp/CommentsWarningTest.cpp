#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class CommentsWarningTest : public ::testing::Test
{
protected:
    CommentsWarningTest()
    :   zserioWarnings("warnings/comments_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(CommentsWarningTest, markdownCommentWithWrongTerminator)
{
    ASSERT_TRUE(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:3:1: "
            "Markdown documentation comment should be terminated by '!*/'!"));

    ASSERT_TRUE(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:7:1: "
            "Markdown documentation comment should be terminated by '!*/'!"));

    ASSERT_TRUE(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:21:1: "
            "Markdown documentation comment should be terminated by '!*/'!"));

    ASSERT_TRUE(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:26:5: "
            "Markdown documentation comment should be terminated by '!*/'!"));

    ASSERT_TRUE(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:38:5: "
            "Markdown documentation comment should be terminated by '!*/'!"));
}

TEST_F(CommentsWarningTest, unresolvedSeeTagInTemplatedStruct)
{
    ASSERT_TRUE(zserioWarnings.isPresent("unresolved_see_tag_in_templated_struct.zs:3:5: "
            "Documentation: Unresolved referenced symbol 'unknown' for type 'TemplatedStruct'!"));
}

TEST_F(CommentsWarningTest, unresolvedSeeTagReference)
{
    ASSERT_TRUE(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:8:4: "
            "Documentation: Unresolved referenced symbol 'Unexisting'!"));

    ASSERT_TRUE(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:15:4: "
            "Documentation: Unresolved referenced symbol 'Unexisting' for type 'Table'!"));
}

TEST_F(CommentsWarningTest, unusedFieldComments)
{
    ASSERT_TRUE(zserioWarnings.isPresent(
        "unused_field_comments.zs:11:11: Documentation comment is not used!"));

    ASSERT_TRUE(zserioWarnings.isPresent(
        "unused_field_comments.zs:55:45: Documentation comment is not used!"));

    ASSERT_TRUE(zserioWarnings.isPresent(
        "unused_field_comments.zs:61:45: Documentation comment is not used!"));
}

TEST_F(CommentsWarningTest, unusedStructCommentById)
{
    const std::string warning = "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used!";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(CommentsWarningTest, unusedStructCommentMultipleComments)
{
    const std::string warning = "unused_struct_comment_multiple_comments.zs:5:9: "
            "Documentation comment is not used!";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
