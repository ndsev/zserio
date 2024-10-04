import unittest

from testutils import getZserioApi


class FunctionsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "functions_warning.zs", expectedWarnings=2, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
