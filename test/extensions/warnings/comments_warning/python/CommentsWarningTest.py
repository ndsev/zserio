import unittest

from testutils import getZserioApi


class CommentsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__,
            "comments_warning.zs",
            extraArgs=["-withWarnings", "unused"],
            expectedWarnings=24,
            errorOutputDict=cls.warnings,
        )

    def testDummy(self):
        pass
