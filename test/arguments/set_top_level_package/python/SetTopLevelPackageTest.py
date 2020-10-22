import unittest

from testutils import getZserioApi

class SetTopLevelPackageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "set_top_level_package.zs", extraArgs=[
            "-setTopLevelPackage", "company.appl"
        ], topLevelPackage="company")

    def testEmptyConstructor(self):
        simpleStructure = self.api.appl.SimpleStructure()
        simpleStructure.setSimpleChoice(self.api.appl.SimpleChoice(simpleStructure.getE()))
        self.assertEqual(26, simpleStructure.bitSizeOf())
