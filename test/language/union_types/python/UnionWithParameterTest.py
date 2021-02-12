import unittest
import zserio

from testutils import getZserioApi

class UnionWithParameterTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_parameter

    def testParamConstructor(self):
        testUnion = self.api.TestUnion(True)
        self.assertTrue(testUnion.getCase1Allowed())

        testUnion.setCase1Field(11)
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.getCase1Allowed(), readTestUnion.getCase1Allowed())
        self.assertEqual(testUnion.getCase1Field(), readTestUnion.getCase1Field())

    def testParamConstructorCase1Forbidden(self):
        testUnion = self.api.TestUnion(False)
        self.assertFalse(testUnion.getCase1Allowed())

        testUnion.setCase1Field(11)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            testUnion.write(writer) # raises exception

    def testFromReader(self):
        testUnion = self.api.TestUnion(True, case3Field_=-1)
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.choiceTag(), readTestUnion.choiceTag())
        self.assertEqual(testUnion.getCase3Field(), readTestUnion.getCase3Field())
        self.assertEqual(-1, readTestUnion.getCase3Field())
