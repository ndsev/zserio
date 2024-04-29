import unittest

from testutils import getZserioApi


class SetTopLevelPackageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(
            __file__,
            "set_top_level_package.zs",
            extraArgs=["-setTopLevelPackage", "company.appl"],
            topLevelPackage="company",
        )

    def testEmptyConstructor(self):
        simpleStructure = self.api.appl.SimpleStructure()
        simpleStructure.simple_choice = self.api.appl.SimpleChoice(simpleStructure.value)
        simpleStructure.simple_template = self.api.appl.SimpleTemplate_Enumeration(True, 0)
        self.assertEqual(32, simpleStructure.bitsizeof())
