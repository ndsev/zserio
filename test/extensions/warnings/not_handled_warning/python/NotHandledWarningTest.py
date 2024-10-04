import unittest

from testutils import getZserioApi


class NotHandledWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "not_handled_warning.zs", expectedWarnings=2, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
