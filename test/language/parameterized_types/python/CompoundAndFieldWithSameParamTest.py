import unittest
import zserio

from testutils import getZserioApi

# this test is mainly for C++, so just check that it is ok
class CompoundAndFieldWithSameParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").compound_and_field_with_same_param

    def testCompoundReadFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeCompoundReadToStream(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        compoundRead = self.api.CompoundRead.from_reader(reader, self.PARAM)
        self.assertEqual(self.FIELD1, compoundRead.field1.value)
        self.assertEqual(self.FIELD2, compoundRead.field2.value)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        compoundReadTest = self.api.CompoundReadTest.from_reader(reader)
        self.assertEqual(self.PARAM, compoundReadTest.compound_read.param)
        self.assertEqual(self.FIELD1, compoundReadTest.compound_read.field1.value)
        self.assertEqual(self.FIELD2, compoundReadTest.compound_read.field2.value)

    def testCompoundPackingFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeCompoundPackingToStream(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        compoundPacking = self.api.CompoundPacking.from_reader(reader, self.PARAM)
        self.assertEqual(self.FIELD1, compoundPacking.field1.value)
        self.assertEqual(self.FIELD2, compoundPacking.field2.value)
        self.assertEqual(self.FIELD3, compoundPacking.field3.value)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        compoundPackingTest = self.api.CompoundPackingTest.from_reader(reader)
        self.assertEqual(self.PARAM, compoundPackingTest.compound_packing.param)
        self.assertEqual(self.FIELD1, compoundPackingTest.compound_packing.field1.value)
        self.assertEqual(self.FIELD2, compoundPackingTest.compound_packing.field2.value)
        self.assertEqual(self.FIELD3, compoundPackingTest.compound_packing.field3.value)

    def _writeCompoundReadToStream(self, writer):
        writer.write_bits(self.FIELD1, 32)
        writer.write_bits(self.FIELD2, 32)

    def _writeCompoundPackingToStream(self, writer):
        writer.write_bits(self.FIELD1, 32)
        writer.write_bits(self.FIELD2, 32)
        writer.write_bits(self.FIELD3, 32)

    PARAM = 10
    FIELD1 = 1
    FIELD2 = 9
    FIELD3 = 5
