import unittest

from testutils import getZserioApi


class FileEncodingWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "file_encoding_warning.zs", expectedWarnings=3, errorOutputDict=cls.warnings)

    def testDummy(self):
        pass
