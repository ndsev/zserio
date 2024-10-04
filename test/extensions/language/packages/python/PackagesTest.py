import unittest

from testutils import getZserioApi


class PackagesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "packages.zs")

    def testPackages(self):
        self.assertIsNotNone(self.api)
