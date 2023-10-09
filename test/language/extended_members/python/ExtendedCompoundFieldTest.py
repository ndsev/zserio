import zserio

import ExtendedMembers

class ExtendedCompoundFieldTest(ExtendedMembers.TestCase):
    def testConstructor(self):
        extended = self.api.Extended()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.value)
        self.assertIsNone(extended.extended_value)

        extended = self.api.Extended(42, self.api.Compound(COMPOUND_ARRAY))
        self.assertTrue(extended.is_extended_value_present())

        self.assertEqual(42, extended.value)
        self.assertEqual(COMPOUND_ARRAY, extended.extended_value.array)

    def testEq(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(extended1, extended2)

        extended1.value = 13
        self.assertNotEqual(extended1, extended2)
        extended2.value = 13
        self.assertEqual(extended1, extended2)

        extended2.extended_value = self.api.Compound(COMPOUND_ARRAY)
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = self.api.Compound(COMPOUND_ARRAY)
        self.assertEqual(extended1, extended2)

    def testHash(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.extended_value = self.api.Compound(COMPOUND_ARRAY)
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = self.api.Compound(COMPOUND_ARRAY)
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extended = self.api.Extended(42, self.api.Compound(COMPOUND_ARRAY))
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, extended.bitsizeof())

    def testInitializeOffsets(self):
        extended = self.api.Extended(42, self.api.Compound(COMPOUND_ARRAY))
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, extended.initialize_offsets(0))

    def testWriteReadExtended(self):
        extended = self.api.Extended(42, self.api.Compound(COMPOUND_ARRAY))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtended(self):
        original = self.api.Original(42)
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())

        # extended value is None
        self.assertIsNone(readExtended.extended_value)

        # bit size as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended.initialize_offsets(0))

        # writes as original
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(ORIGINAL_BIT_SIZE, bitBuffer.bitsize)

        # read original again
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(original, readOriginal)

        # setter makes the value present!
        readExtended.extended_value = self.api.Compound(COMPOUND_ARRAY)
        self.assertTrue(readExtended.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, readExtended.initialize_offsets(0))

        # write as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.bitsize)

    def testWriteExtendedReadOriginal(self):
        extended = self.api.Extended(42, self.api.Compound(COMPOUND_ARRAY))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_ARRAY, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)


COMPOUND_ARRAY = [ 0, 1, 2, 3, 4 ]
ORIGINAL_BIT_SIZE = 4 * 8
EXTENDED_BIT_SIZE_DEFAULT = ORIGINAL_BIT_SIZE + zserio.bitsizeof.bitsizeof_varsize(0)
EXTENDED_BIT_SIZE_WITH_ARRAY = (ORIGINAL_BIT_SIZE +
                                zserio.bitsizeof.bitsizeof_varsize(len(COMPOUND_ARRAY)) +
                                len(COMPOUND_ARRAY) * 4 * 8)
