import unittest

from testutils import getZserioApi


class ChoiceTypesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "choice_types_warning.zs", expectedWarnings=1, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
