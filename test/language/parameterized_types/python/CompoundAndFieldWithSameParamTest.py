import unittest
import zserio

from testutils import getZserioApi

# this test is mainly for C++, so just check that it compiles ok
class CompoundAndFieldWithSameParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").compound_and_field_with_same_param

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeItemToStream(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        compound = self.api.Compound.from_reader(reader, self.PARAM)
        self.assertEqual(self.FIELD1, compound.field1.value)
        self.assertEqual(self.FIELD2, compound.field2.value)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        sameParamTest = self.api.SameParamTest.from_reader(reader)
        self.assertEqual(self.PARAM, sameParamTest.compound.param)
        self.assertEqual(self.FIELD1, sameParamTest.compound.field1.value)
        self.assertEqual(self.FIELD2, sameParamTest.compound.field2.value)

    def _writeItemToStream(self, writer):
        writer.write_bits(self.FIELD1, 32)
        writer.write_bits(self.FIELD2, 32)

    PARAM = 10
    FIELD1 = 1
    FIELD2 = 9
