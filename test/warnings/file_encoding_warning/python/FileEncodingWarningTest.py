import unittest

from testutils import getZserioApi, assertWarningsPresent


class FileEncodingWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "file_encoding_warning.zs", expectedWarnings=3, errorOutputDict=cls.warnings)

    def testNonUtf8Characters(self):
        assertWarningsPresent(
            self,
            "file_encoding_warning.zs",
            ["file_encoding_warning.zs:1:1: Found non-UTF8 encoded characters."],
        )

    def testTabCharacters(self):
        assertWarningsPresent(
            self, "file_encoding_warning.zs", ["file_encoding_warning.zs:1:1: Found tab characters."]
        )

    def testNonPrintableAsciiCharacters(self):
        assertWarningsPresent(
            self,
            "file_encoding_warning.zs",
            ["file_encoding_warning.zs:1:1: Found non-printable ASCII characters."],
        )
