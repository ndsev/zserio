import unittest

from testutils import getZserioApi


class DefaultPackageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "default_package.zs", hasPackage=False)

    def testDefaultPackageStructure(self):
        # just test that DefaultPackageStructure is available in default package
        structure = self.api.DefaultPackageStructure(4)
        structure.value = 10
        structure.top_structure = self.api.default_package_import.top.TopStructure(1, 1234)
        structure.child_structure = self.api.Child(0xDEADBEEF)
        self.assertEqual(10, structure.value)
        self.assertEqual(1, structure.top_structure.type)
        self.assertEqual(1234, structure.top_structure.data)
        self.assertEqual(0xDEADBEEF, structure.child_structure.value)
