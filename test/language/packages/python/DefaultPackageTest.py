import unittest

from testutils import getZserioApi

class DefaultPackageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "default_package.zs", hasPackage=False)

    def testDefaultPackageStructure(self):
        # just test that DefaultPackageStructure is available in default package
        structure = self.api.DefaultPackageStructure(4)
        structure.setValue(10)
        structure.setTopStructure(self.api.default_package_import.top.TopStructure(1, 1234))
        structure.setChildStructure(self.api.Child(0xdeadbeef))
        self.assertEqual(10, structure.getValue())
        self.assertEqual(1, structure.getTopStructure().getType())
        self.assertEqual(1234, structure.getTopStructure().getData())
        self.assertEqual(0xdeadbeef, structure.getChildStructure().getValue())
