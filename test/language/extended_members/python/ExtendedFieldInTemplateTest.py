import unittest
import zserio

from testutils import getZserioApi

class ExtendedFieldInTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "extended_members.zs").extended_field_in_template

    def testConstructorSimple(self):
        extended = self.api.ExtendedSimple()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.value)
        self.assertEqual(0, extended.extended_value)

        extended = self.api.ExtendedSimple(42, zserio.limits.UINT32_MAX)
        self.assertTrue(extended.is_extended_value_present())
        self.assertEqual(42, extended.value)
        self.assertEqual(zserio.limits.UINT32_MAX, extended.extended_value)

    def testConstructorCompound(self):
        extended = self.api.ExtendedCompound()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.value)
        self.assertIsNone(extended.extended_value)

        extended = self.api.ExtendedCompound(42, self.api.Compound(zserio.limits.UINT32_MAX))
        self.assertTrue(extended.is_extended_value_present())
        self.assertEqual(42, extended.value)
        self.assertEqual(zserio.limits.UINT32_MAX, extended.extended_value.field)

    def testEqSimple(self):
        extended1 = self.api.ExtendedSimple()
        extended2 = self.api.ExtendedSimple()
        self.assertEqual(extended1, extended2)

        extended1.value = 13
        self.assertNotEqual(extended1, extended2)
        extended2.value = 13
        self.assertEqual(extended1, extended2)

        extended2.extended_value = zserio.limits.UINT32_MAX
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = zserio.limits.UINT32_MAX
        self.assertEqual(extended1, extended2)

    def testEqCompound(self):
        extended1 = self.api.ExtendedCompound()
        extended2 = self.api.ExtendedCompound()
        self.assertEqual(extended1, extended2)

        extended1.value = 13
        self.assertNotEqual(extended1, extended2)
        extended2.value = 13
        self.assertEqual(extended1, extended2)

        extended2.extended_value = self.api.Compound(zserio.limits.UINT32_MAX)
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = self.api.Compound(zserio.limits.UINT32_MAX)
        self.assertEqual(extended1, extended2)

    def testHashSimple(self):
        extended1 = self.api.ExtendedSimple()
        extended2 = self.api.ExtendedSimple()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.extended_value = 42
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = 42
        self.assertEqual(hash(extended1), hash(extended2))

    def testHashCompound(self):
        extended1 = self.api.ExtendedCompound()
        extended2 = self.api.ExtendedCompound()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.extended_value = self.api.Compound(42)
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = self.api.Compound(42)
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOfSimple(self):
        extended = self.api.ExtendedSimple()
        self.assertEqual(EXTENDED_BIT_SIZE, extended.bitsizeof())

    def testBitSizeOfCompound(self):
        extended = self.api.ExtendedCompound(42, self.api.Compound())
        self.assertEqual(EXTENDED_BIT_SIZE, extended.bitsizeof())

    def testInitializeOffsetsSimple(self):
        extended = self.api.ExtendedSimple()
        self.assertEqual(EXTENDED_BIT_SIZE, extended.initialize_offsets(0))

    def testInitializeOffsetsCompound(self):
        extended = self.api.ExtendedCompound(42, self.api.Compound())
        self.assertEqual(EXTENDED_BIT_SIZE, extended.initialize_offsets(0))

    def testWriteReadExtendedSimple(self):
        extended = self.api.ExtendedSimple(42, zserio.limits.UINT32_MAX)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.ExtendedSimple, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteReadExtendedCompound(self):
        extended = self.api.ExtendedCompound(42, self.api.Compound(zserio.limits.UINT32_MAX))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.ExtendedCompound, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtendedSimple(self):
        original = self.api.Original(42)
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.ExtendedSimple, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())

        # extended value is default constructed
        self.assertEqual(0, readExtended.extended_value)

        # bit size as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.initialize_offsets(0))

        # write as original
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(ORIGINAL_BIT_SIZE, bitBuffer.bitsize)

        # read original again
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(original, readOriginal)

        # setter makes the value present!
        readExtended.extended_value = zserio.limits.UINT32_MAX
        self.assertTrue(readExtended.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.initialize_offsets(0))

        # writes as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

    def testWriteOriginalReadExtendedCompound(self):
        original = self.api.Original(42)
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.ExtendedCompound, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())

        # extended value is None
        self.assertIsNone(readExtended.extended_value)

        # bit size as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.initialize_offsets(0))

        # write as original
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(ORIGINAL_BIT_SIZE, bitBuffer.bitsize)

        # read original again
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(original, readOriginal)

        # setter makes the value present!
        readExtended.extended_value = self.api.Compound(zserio.limits.UINT32_MAX)
        self.assertTrue(readExtended.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.initialize_offsets(0))

        # writes as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

    def testWriteExtendedSimpleReadOriginal(self):
        extended = self.api.ExtendedSimple(42, zserio.limits.UINT32_MAX)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

    def testWriteExtendedCompoundReadOriginal(self):
        extended = self.api.ExtendedCompound(42, self.api.Compound(zserio.limits.UINT32_MAX))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

ORIGINAL_BIT_SIZE = 4 * 8
EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 4 * 8
