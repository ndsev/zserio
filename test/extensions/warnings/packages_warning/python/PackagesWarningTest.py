import unittest

from testutils import getZserioApi


class PackagesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "packages_warning.zs", expectedWarnings=4, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
