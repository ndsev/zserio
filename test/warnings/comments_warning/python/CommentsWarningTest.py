import unittest

from testutils import getZserioApi, assertWarningsPresent

class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "comments_warning.zs",
                               expectedWarnings=16, errorOutputDict=cls.warnings)

    def testMarkdownCommentWithWrongTerminator(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:3:1: "
                "Markdown documentation comment should be terminated by '!*/'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:7:1: "
                "Markdown documentation comment should be terminated by '!*/'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:21:1: "
                "Markdown documentation comment should be terminated by '!*/'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:26:5: "
                "Markdown documentation comment should be terminated by '!*/'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:38:5: "
                "Markdown documentation comment should be terminated by '!*/'!"
            ]
        )

    def testUnresolvedMarkdownSeeTagReference(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:7:5: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"
            ]
        )

    def testUnresolvedSeeTagInTemplatedStruct(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_in_templated_struct.zs:3:5: "
                "Documentation: Unresolved referenced symbol 'unknown' for type 'TemplatedStruct'!"
            ]
        )

    def testUnresolvedSeeTagReference(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_reference.zs:8:4: "
                "Documentation: Unresolved referenced symbol 'Unexisting'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_reference.zs:9:4: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unexisting_package'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_reference.zs:16:4: "
                "Documentation: Unresolved referenced symbol 'unexisting' for type 'Table'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_reference.zs:17:4: "
                "Documentation: Unresolved referenced symbol 'unexisting' for type 'Table'!"
            ]
        )

    def testUnusedFieldComments(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:11:11: Documentation comment is not used!"
            ]
        )
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:55:45: Documentation comment is not used!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:61:45: Documentation comment is not used!"
            ]
        )

    def testUnusedStructCommentById(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used!"
            ]
        )

    def testUnusedStructCommentMultipleComments(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_struct_comment_multiple_comments.zs:5:9: "
                "Documentation comment is not used!"
            ]
        )
