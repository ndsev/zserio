import zserio

import ExtendedMembers


class ExtendedOptionalParameterizedFieldTest(ExtendedMembers.TestCase):
    def testConstructor(self):
        extended = self.api.Extended()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.value)
        # optional is unset
        self.assertFalse(extended.is_extended_value_set())
        self.assertFalse(extended.is_extended_value_used())

        extended = self.api.Extended(len(ARRAY), self.api.Parameterized(len(ARRAY)))
        extended.extended_value.array = ARRAY
        self.assertTrue(extended.is_extended_value_present())
        self.assertEqual(len(ARRAY), extended.value)
        self.assertEqual(ARRAY, extended.extended_value.array)

    def testEq(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(extended1, extended2)

        extended1.value = len(ARRAY)
        self.assertNotEqual(extended1, extended2)
        extended2.value = len(ARRAY)
        self.assertEqual(extended1, extended2)

        extendedValue = self.api.Parameterized(len(ARRAY))
        extendedValue.array = ARRAY
        extended2.extended_value = extendedValue
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = extendedValue
        self.assertEqual(extended1, extended2)

    def testHash(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extendedValue = self.api.Parameterized(len(ARRAY))
        extendedValue.array = ARRAY
        extended2.extended_value = extendedValue
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = extendedValue
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extended = self.api.Extended()
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.bitsizeof())

        extendedValue = self.api.Parameterized(len(ARRAY))
        extendedValue.array = ARRAY
        extended.extended_value = extendedValue
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.bitsizeof())

    def testInitializeOffsets(self):
        extended = self.api.Extended()
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extended.initialize_offsets(0))

        extendedValue = self.api.Parameterized(len(ARRAY))
        extendedValue.array = ARRAY
        extended.extended_value = extendedValue
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extended.initialize_offsets(0))

    def testWriteReadExtendedWithoutOptional(self):
        extended = self.api.Extended(0, None)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertFalse(readExtended.is_extended_value_set())
        self.assertFalse(readExtended.is_extended_value_used())
        self.assertEqual(extended, readExtended)

    def testWriteReadExtendedWithOptional(self):
        extended = self.api.Extended(len(ARRAY), self.api.Parameterized(len(ARRAY)))
        extended.extended_value.array = ARRAY
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertTrue(readExtended.is_extended_value_set())
        self.assertTrue(readExtended.is_extended_value_used())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtended(self):
        original = self.api.Original(len(ARRAY))
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())

        # extended value is default constructed (NullOpt)
        self.assertFalse(readExtended.is_extended_value_set())
        self.assertFalse(readExtended.is_extended_value_used())

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

        # setter makes the value present! (or even resetter)
        extendedWithoutOptional = zserio.deserialize(self.api.Extended, bitBuffer)
        extendedWithoutOptional.reset_extended_value()
        self.assertTrue(extendedWithoutOptional.is_extended_value_present())

        extendedWithOptional = zserio.deserialize(self.api.Extended, bitBuffer)
        extendedValue = self.api.Parameterized(len(ARRAY), ARRAY)
        extendedWithOptional.extended_value = extendedValue
        self.assertTrue(extendedWithOptional.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.bitsizeof())
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, extendedWithoutOptional.initialize_offsets(0))
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, extendedWithOptional.initialize_offsets(0))

        # writes as extended
        bitBuffer = zserio.serialize(extendedWithoutOptional)
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.bitsize)
        bitBuffer = zserio.serialize(extendedWithOptional)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.bitsize)

    def testWriteExtendedWithoutOptionalReadOriginal(self):
        extended = self.api.Extended(0, None)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

    def testWriteExtendedWithOptionalReadOriginal(self):
        extended = self.api.Extended(len(ARRAY), self.api.Parameterized(len(ARRAY)))
        extended.extended_value.array = ARRAY
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_WITH_OPTIONAL, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)


ARRAY = ["this", "is", "test"]

ORIGINAL_BIT_SIZE = 11
EXTENDED_BIT_SIZE_WITHOUT_OPTIONAL = zserio.bitposition.alignto(8, ORIGINAL_BIT_SIZE) + 1
EXTENDED_BIT_SIZE_WITH_OPTIONAL = (
    zserio.bitposition.alignto(8, ORIGINAL_BIT_SIZE)
    + 1
    + sum(zserio.bitsizeof.bitsizeof_string(element) for element in ARRAY)
)
