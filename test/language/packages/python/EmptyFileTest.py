import unittest

from testutils import getZserioApi

class EmptyFileTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "empty_file.zs", hasPackage=False, hasApi=False)

    def testEmptyFile(self):
        self.assertIsNone(self.api)
