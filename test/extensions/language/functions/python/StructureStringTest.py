import Functions


class StructureStringTest(Functions.TestCase):
    def testGetPoolConst(self):
        testStructure = self.api.TestStructure(self.api.StringPool())
        self.assertEqual("POOL_CONST", testStructure.get_pool_const())

    def testGetPoolField(self):
        testStructure = self.api.TestStructure(self.api.StringPool())
        self.assertEqual("POOL_FIELD", testStructure.get_pool_field())

    def testGetConst(self):
        testStructure = self.api.TestStructure(self.api.StringPool())
        self.assertEqual("CONST", testStructure.get_const())

    def testGetField(self):
        testStructure = self.api.TestStructure(self.api.StringPool())
        self.assertEqual("FIELD", testStructure.get_field())
