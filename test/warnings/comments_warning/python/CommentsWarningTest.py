import unittest

from testutils import getZserioApi, assertWarningsPresent

class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "comments_warning.zs", extraArgs=["-withWarnings", "unused"],
                               expectedWarnings=15, errorOutputDict=cls.warnings)

    def testDocCommentFormat(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "doc_comment_format.zs:4:4: "
                "Documentation: no viable alternative at input '\\n * /**'."
            ]
        )

    def testMarkdownCommentWithWrongTerminator(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:3:1: "
                "Markdown documentation comment should be terminated by '!*/'."
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:7:1: "
                "Markdown documentation comment should be terminated by '!*/'."
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:21:1: "
                "Markdown documentation comment should be terminated by '!*/'."
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:26:5: "
                "Markdown documentation comment should be terminated by '!*/'."
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "markdown_comment_with_wrong_terminator.zs:38:5: "
                "Markdown documentation comment should be terminated by '!*/'."
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
                "unresolved_see_tag_reference.zs:15:4: "
                "Documentation: Unresolved referenced symbol 'Unexisting' for type 'Table'!"
            ]
        )

    def testUnusedFieldComments(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:11:11: Documentation comment is not used."
            ]
        )
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:55:45: Documentation comment is not used."
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_field_comments.zs:61:45: Documentation comment is not used."
            ]
        )

    def testUnusedStructCommentById(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used."
            ]
        )

    def testUnusedStructCommentMultipleComments(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unused_struct_comment_multiple_comments.zs:5:9: "
                "Documentation comment is not used."
            ]
        )
