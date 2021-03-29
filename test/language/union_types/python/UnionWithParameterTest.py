import unittest
import zserio

from testutils import getZserioApi

class UnionWithParameterTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_parameter

    def testParamConstructor(self):
        testUnion = self.api.TestUnion(True)
        self.assertTrue(testUnion.case1allowed)

        testUnion.case1field = 11
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.case1allowed, readTestUnion.case1allowed)
        self.assertEqual(testUnion.case1field, readTestUnion.case1field)

    def testParamConstructorCase1Forbidden(self):
        testUnion = self.api.TestUnion(False)
        self.assertFalse(testUnion.case1allowed)

        testUnion.case1field = 11
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            testUnion.write(writer) # raises exception

    def testFromReader(self):
        testUnion = self.api.TestUnion(True, case3field_=-1)
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer, True)
        self.assertEqual(testUnion.choice_tag, readTestUnion.choice_tag)
        self.assertEqual(testUnion.case3field, readTestUnion.case3field)
        self.assertEqual(-1, readTestUnion.case3field)
