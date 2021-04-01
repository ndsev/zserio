import unittest
import zserio

from testutils import getZserioApi

class UnionWithParameterTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_parameter

    def testParamConstructor(self):
        testUnion = self.api.TestUnion(True)
        self.assertTrue(testUnion.case1_allowed)

        testUnion.case1_field = 11
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.case1_allowed, readTestUnion.case1_allowed)
        self.assertEqual(testUnion.case1_field, readTestUnion.case1_field)

    def testParamConstructorCase1Forbidden(self):
        testUnion = self.api.TestUnion(False)
        self.assertFalse(testUnion.case1_allowed)

        testUnion.case1_field = 11
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            testUnion.write(writer) # raises exception

    def testFromReader(self):
        testUnion = self.api.TestUnion(True, case3_field_=-1)
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.choice_tag, readTestUnion.choice_tag)
        self.assertEqual(testUnion.case3_field, readTestUnion.case3_field)
        self.assertEqual(-1, readTestUnion.case3_field)
