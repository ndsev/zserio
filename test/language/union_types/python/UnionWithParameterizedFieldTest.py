import unittest
import zserio

from testutils import getZserioApi

class UnionWithParameterizedFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").union_with_parameterized_field

    def testConstructor(self):
        testUnion = self.api.TestUnion()
        testUnion.array_holder = self.api.ArrayHolder(10)
        self.assertEqual(10, testUnion.array_holder.size)

        testUnion = self.api.TestUnion(field_=13)
        self.assertEqual(self.api.TestUnion.CHOICE_FIELD, testUnion.choice_tag)

        testUnion = self.api.TestUnion(array_holder_=self.api.ArrayHolder(10))
        self.assertEqual(10, testUnion.array_holder.size)

    def testFromReader(self):
        testUnion = self.api.TestUnion()
        testUnion.array_holder = self.api.ArrayHolder(10, list(range(10)))
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer)
        self.assertEqual(10, readTestUnion.array_holder.size)
