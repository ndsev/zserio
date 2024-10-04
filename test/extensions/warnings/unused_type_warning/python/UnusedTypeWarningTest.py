import unittest

from testutils import getZserioApi


class UnusedTypeWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__,
            "unused_type_warning.zs",
            extraArgs=["-withWarnings", "unused"],
            expectedWarnings=6,
            errorOutputDict=cls.warnings,
        )

    def testDummy(self):
        pass
