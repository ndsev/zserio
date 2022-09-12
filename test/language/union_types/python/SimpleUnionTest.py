import unittest
import zserio

from testutils import getZserioApi

class SimpleUnionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").simple_union

    def testConstructor(self):
        simpleUnion = self.api.SimpleUnion()
        self.assertEqual(self.api.SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choice_tag)

        simpleUnion = self.api.SimpleUnion(case2_field_=12)
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE2_FIELD, simpleUnion.choice_tag)
        self.assertEqual(12, simpleUnion.case2_field)

        simpleUnion = self.api.SimpleUnion(case3_field_="test")
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE3_FIELD, simpleUnion.choice_tag)
        self.assertEqual("test", simpleUnion.case3_field)

    def testAmbiguousConstructor(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.SimpleUnion(case2_field_=12, case3_field_="test")

    def testEmptyConstructorBitSizeof(self):
        simpleUnion = self.api.SimpleUnion()
        with self.assertRaises(zserio.PythonRuntimeException):
            simpleUnion.bitsizeof()

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE1_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion.from_reader(reader)
        self.assertEqual(self.CASE1_FIELD, simpleUnion.case1_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE2_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion.from_reader(reader)
        self.assertEqual(self.CASE2_FIELD, simpleUnion.case2_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE3_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion.from_reader(reader)
        self.assertEqual(self.CASE3_FIELD, simpleUnion.case3_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE4_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion.from_reader(reader)
        self.assertEqual(self.CASE4_FIELD, simpleUnion.case4_field)

    def testEq(self):
        simpleUnion11 = self.api.SimpleUnion()
        simpleUnion12 = self.api.SimpleUnion()
        simpleUnion13 = self.api.SimpleUnion()
        self.assertTrue(simpleUnion11 == simpleUnion12)
        simpleUnion11.case1_field = self.CASE1_FIELD
        simpleUnion12.case1_field = self.CASE1_FIELD
        simpleUnion13.case1_field = self.CASE1_FIELD + 1
        self.assertTrue(simpleUnion11 == simpleUnion12)
        self.assertFalse(simpleUnion11 == simpleUnion13)

        simpleUnion21 = self.api.SimpleUnion()
        simpleUnion21.case2_field = self.CASE2_FIELD
        simpleUnion22 = self.api.SimpleUnion()
        simpleUnion22.case2_field = self.CASE2_FIELD
        simpleUnion23 = self.api.SimpleUnion()
        simpleUnion23.case2_field = self.CASE2_FIELD - 1
        self.assertTrue(simpleUnion21 == simpleUnion22)
        self.assertFalse(simpleUnion21 == simpleUnion23)
        self.assertFalse(simpleUnion21 == simpleUnion11)

        simpleUnion4 = self.api.SimpleUnion()
        simpleUnion4.case4_field = self.CASE1_FIELD # same value as simpleUnion11, but different choice
        self.assertFalse(simpleUnion11 == simpleUnion4)

    def testHash(self):
        simpleUnion1 = self.api.SimpleUnion()
        simpleUnion2 = self.api.SimpleUnion()
        self.assertEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion1.case1_field = self.CASE1_FIELD
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion2.case4_field = self.CASE4_FIELD
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))

        # use hardcoded values to check that the hash code is stable
        self.assertEqual(31500, hash(simpleUnion1))
        self.assertEqual(31640, hash(simpleUnion2))

        simpleUnion2.case4_field = self.CASE1_FIELD # same value as simpleUnion1
        self.assertNotEqual(hash(simpleUnion1), hash(simpleUnion2))
        simpleUnion1.case4_field = self.CASE1_FIELD # same value as simpleUnion2
        self.assertEqual(hash(simpleUnion1), hash(simpleUnion2))

    def testGetSetCase1Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case1_field = self.CASE1_FIELD
        self.assertEqual(self.CASE1_FIELD, simpleUnion.case1_field)

    def testGetSetCase2Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case2_field = self.CASE2_FIELD
        self.assertEqual(self.CASE2_FIELD, simpleUnion.case2_field)

    def testGetSetCase3Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case3_field = self.CASE3_FIELD
        self.assertEqual(self.CASE3_FIELD, simpleUnion.case3_field)

    def testGetSetCase4Field(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case4_field = self.CASE4_FIELD
        self.assertEqual(self.CASE4_FIELD, simpleUnion.case4_field)

    def testChoiceTag(self):
        simpleUnion = self.api.SimpleUnion()
        self.assertEqual(self.api.SimpleUnion.UNDEFINED_CHOICE, simpleUnion.choice_tag)
        simpleUnion.case1_field = self.CASE1_FIELD
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE1_FIELD, simpleUnion.choice_tag)
        simpleUnion.case2_field = self.CASE2_FIELD
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE2_FIELD, simpleUnion.choice_tag)
        simpleUnion.case3_field = self.CASE3_FIELD
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE3_FIELD, simpleUnion.choice_tag)
        simpleUnion.case4_field = self.CASE4_FIELD
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE4_FIELD, simpleUnion.choice_tag)

        simpleUnion = self.api.SimpleUnion(case2_field_=self.CASE2_FIELD)
        self.assertEqual(self.api.SimpleUnion.CHOICE_CASE2_FIELD, simpleUnion.choice_tag)

    def testBitSizeOf(self):
        simpleUnion = self.api.SimpleUnion()

        simpleUnion.case1_field = self.CASE1_FIELD
        self.assertEqual(self.UNION_CASE1_BIT_SIZE, simpleUnion.bitsizeof())

        simpleUnion.case2_field = self.CASE2_FIELD
        self.assertEqual(self.UNION_CASE2_BIT_SIZE, simpleUnion.bitsizeof())

        simpleUnion.case3_field = self.CASE3_FIELD
        self.assertEqual(self.UNION_CASE3_BIT_SIZE, simpleUnion.bitsizeof())

        simpleUnion.case4_field = self.CASE4_FIELD
        self.assertEqual(self.UNION_CASE4_BIT_SIZE, simpleUnion.bitsizeof())

    def testInitializeOffsets(self):
        bitPosition = 1
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case1_field = self.CASE1_FIELD
        self.assertEqual(bitPosition + self.UNION_CASE1_BIT_SIZE, simpleUnion.initialize_offsets(bitPosition))

        simpleUnion.case2_field = self.CASE2_FIELD
        self.assertEqual(bitPosition + self.UNION_CASE2_BIT_SIZE, simpleUnion.initialize_offsets(bitPosition))

        simpleUnion.case3_field = self.CASE3_FIELD
        self.assertEqual(bitPosition + self.UNION_CASE3_BIT_SIZE, simpleUnion.initialize_offsets(bitPosition))

        simpleUnion.case4_field = self.CASE4_FIELD
        self.assertEqual(bitPosition + self.UNION_CASE4_BIT_SIZE, simpleUnion.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE1_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE1_FIELD, simpleUnion.case1_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE2_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE2_FIELD, simpleUnion.case2_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE3_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE3_FIELD, simpleUnion.case3_field)

        writer = zserio.BitStreamWriter()
        self._writeSimpleUnionToStream(writer, self.api.SimpleUnion.CHOICE_CASE4_FIELD)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.read(reader)
        self.assertEqual(self.CASE4_FIELD, simpleUnion.case4_field)

    def testWrite(self):
        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case1_field = self.CASE1_FIELD
        bitBuffer = zserio.serialize(simpleUnion)
        readSimpleUnion = zserio.deserialize(self.api.SimpleUnion, bitBuffer)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case2_field = self.CASE2_FIELD
        bitBuffer = zserio.serialize(simpleUnion)
        readSimpleUnion = zserio.deserialize(self.api.SimpleUnion, bitBuffer)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion(case3_field_=self.CASE3_FIELD)
        bitBuffer = zserio.serialize(simpleUnion)
        readSimpleUnion = zserio.deserialize(self.api.SimpleUnion, bitBuffer)
        self.assertTrue(simpleUnion == readSimpleUnion)

        simpleUnion = self.api.SimpleUnion()
        simpleUnion.case4_field = self.CASE4_FIELD
        bitBuffer = zserio.serialize(simpleUnion)
        readSimpleUnion = zserio.deserialize(self.api.SimpleUnion, bitBuffer)
        self.assertTrue(simpleUnion == readSimpleUnion)

    def _writeSimpleUnionToStream(self, writer, choiceTag):
        writer.write_varsize(choiceTag)
        if choiceTag == self.api.SimpleUnion.CHOICE_CASE1_FIELD:
            writer.write_signed_bits(self.CASE1_FIELD, 8)
        elif choiceTag == self.api.SimpleUnion.CHOICE_CASE2_FIELD:
            writer.write_bits(self.CASE2_FIELD, 16)
        elif choiceTag == self.api.SimpleUnion.CHOICE_CASE3_FIELD:
            writer.write_string(self.CASE3_FIELD)
        elif choiceTag == self.api.SimpleUnion.CHOICE_CASE4_FIELD:
            writer.write_signed_bits(self.CASE4_FIELD, 8)
        else:
            raise zserio.PythonRuntimeException(f"_writeSimpleUnionToStream - unknown choiceTag {choiceTag}!")

    CASE1_FIELD = 13
    CASE2_FIELD = 65535
    CASE3_FIELD = "SimpleUnion"
    CASE4_FIELD = 42

    UNION_CASE1_BIT_SIZE = 8 + 8
    UNION_CASE2_BIT_SIZE = 8 + 16
    UNION_CASE3_BIT_SIZE = 8 + zserio.bitsizeof.bitsizeof_string(CASE3_FIELD)
    UNION_CASE4_BIT_SIZE = 8 + 8
