import unittest
import zserio

from testutils import getZserioApi

class ExtendedSimpleFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "extended_members.zs").extended_simple_field

    def testConstructor(self):
        extended = self.api.Extended()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.value)
        self.assertEqual(0, extended.extended_value)

        extended = self.api.Extended(42, zserio.limits.UINT64_MAX)
        self.assertTrue(extended.is_extended_value_present())
        self.assertEqual(42, extended.value)
        self.assertEqual(zserio.limits.UINT64_MAX, extended.extended_value)

    def testEq(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(extended1, extended2)

        extended1.value = 13
        self.assertNotEqual(extended1, extended2)
        extended2.value = 13
        self.assertEqual(extended1, extended2)

        extended2.extended_value = zserio.limits.UINT64_MAX
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = zserio.limits.UINT64_MAX
        self.assertEqual(extended1, extended2)

    def testHash(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.extended_value = 42
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = 42
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extended = self.api.Extended()
        self.assertEqual(EXTENDED_BIT_SIZE, extended.bitsizeof())

    def testInitializeOffsets(self):
        extended = self.api.Extended()
        self.assertEqual(EXTENDED_BIT_SIZE, extended.initialize_offsets(0))

    def testWriteReadExtended(self):
        extended = self.api.Extended(42, zserio.limits.UINT64_MAX)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtended(self):
        original = self.api.Original(42)
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
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
        readExtended.extended_value = zserio.limits.UINT64_MAX
        self.assertTrue(readExtended.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.initialize_offsets(0))

        # writes as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

    def testWriteExtendedReadOriginal(self):
        extended = self.api.Extended(42, zserio.limits.UINT64_MAX)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        original = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, original.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

ORIGINAL_BIT_SIZE = 4 * 8
EXTENDED_BIT_SIZE = ORIGINAL_BIT_SIZE + 8 * 8
