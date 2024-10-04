import unittest

from testutils import getZserioApi


class TemplatesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(
            __file__,
            "templates_warning.zs",
            extraArgs=["-withWarnings", "default-instantiation"],
            expectedWarnings=5,
            errorOutputDict=cls.warnings,
        )

    def testDummy(self):
        pass
