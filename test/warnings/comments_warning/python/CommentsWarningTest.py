import unittest

from testutils import getZserioApi, assertWarningsPresent

class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "comments_warning.zs", extraArgs=["-withWarnings", "unused"],
                               expectedWarnings=24, errorOutputDict=cls.warnings)

    def testDocCommentFormat(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "doc_comment_format.zs:4:4: "
                "Documentation: no viable alternative at input" # '\\n * /**' won't work under Windows
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

    def testUnresolvedMarkdownSeeTagReference(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:7:5: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:14:41: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:22:34: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:27:23: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:33:6: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:38:20: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"
            ]
        )

        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_markdown_see_tag_reference.zs:42:46: "
                "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"
            ]
        )

    def testUnresolvedSeeTagInTemplatedStruct(self):
        assertWarningsPresent(self,
            "comments_warning.zs",
            [
                "unresolved_see_tag_in_templated_struct.zs:3:5: "
                "Documentation: Unresolved referenced symbol 'unknown'!"
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
