import unittest

from testutils import getZserioApi


class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__,
            "doc_comment_missing_warning.zs",
            extraArgs=["-withWarnings", "doc-comment-missing"],
            expectedWarnings=32,
            errorOutputDict=cls.warnings,
        )

    def testDummy(self):
        pass
