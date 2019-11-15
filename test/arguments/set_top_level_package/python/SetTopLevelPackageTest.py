import unittest

from testutils import getZserioApi

class SetTopLevelPackageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "set_top_level_package.zs", extraArgs=[
            "-setTopLevelPackage", "company.appl"
        ], topLevelPackage="company")

    def testEmptyConstructor(self):
        simpleStructure = self.api.appl.set_top_level_package.SimpleStructure()
        simpleStructure.setSimpleChoice(
            self.api.appl.set_top_level_package.SimpleChoice(simpleStructure.getE())
        )
        self.assertEqual(26, simpleStructure.bitSizeOf())
