import zserio

import ExtendedMembers


class MultipleExtendedFieldsTest(ExtendedMembers.TestCase):
    def testDefaultConstructor(self):
        extended2 = self.api.Extended2()

        # always present when not read from stream
        self.assertTrue(extended2.is_extended_value1_present())
        self.assertTrue(extended2.is_extended_value2_present())

        # default initialized
        self.assertEqual(0, extended2.value)
        self.assertEqual(0, extended2.extended_value1)
        self.assertEqual(DEFAULT_EXTENDED_VALUE2, extended2.extended_value2)

    def testFieldConstructor(self):
        extended2 = self.api.Extended2(42, 2, "other")
        self.assertTrue(extended2.is_extended_value1_present())
        self.assertTrue(extended2.is_extended_value2_present())

        self.assertEqual(42, extended2.value)
        self.assertEqual(2, extended2.extended_value1)
        self.assertEqual("other", extended2.extended_value2)

    def testOperatorEquality(self):
        extended1 = self.api.Extended2()
        extended2 = self.api.Extended2()
        self.assertEqual(extended1, extended2)

        extended1.value = 13
        self.assertNotEqual(extended1, extended2)
        extended2.value = 13
        self.assertEqual(extended1, extended2)

        extended2.extended_value1 = 2
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value1 = 2
        self.assertEqual(extended1, extended2)

        extended1.extended_value2 = "value"
        self.assertNotEqual(extended1, extended2)
        extended2.extended_value2 = "value"
        self.assertEqual(extended1, extended2)

    def testHashCode(self):
        extended1 = self.api.Extended2()
        extended2 = self.api.Extended2()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.value = 13
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.value = 13
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.extended_value1 = 2
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value1 = 2
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.extended_value2 = "value"
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.extended_value2 = "value"
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extended2 = self.api.Extended2()
        self.assertEqual(EXTENDED2_BIT_SIZE, extended2.bitsizeof())

    def testInitializeOffsets(self):
        extended2 = self.api.Extended2()
        self.assertEqual(EXTENDED2_BIT_SIZE, extended2.initialize_offsets(0))

    def testWriteReadExtended2(self):
        extended2 = self.api.Extended2(42, 2, DEFAULT_EXTENDED_VALUE2)
        bitBuffer = zserio.serialize(extended2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended2, bitBuffer)
        self.assertTrue(readExtended.is_extended_value1_present())
        self.assertTrue(readExtended.is_extended_value2_present())
        self.assertEqual(extended2, readExtended)

    def testWriteOriginalReadExtended2(self):
        original = self.api.Original(42)
        bitBuffer = zserio.serialize(original)
        readExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self.assertFalse(readExtended2.is_extended_value1_present())
        self.assertFalse(readExtended2.is_extended_value2_present())

        # extended values are default constructed
        self.assertEqual(0, readExtended2.extended_value1)
        self.assertEqual(DEFAULT_EXTENDED_VALUE2, readExtended2.extended_value2)

        # bit size as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended2.bitsizeof())

        # initialize offsets as original
        self.assertEqual(ORIGINAL_BIT_SIZE, readExtended2.initialize_offsets(0))

        # writes as original
        bitBuffer = zserio.serialize(readExtended2)
        self.assertEqual(ORIGINAL_BIT_SIZE, bitBuffer.bitsize)

        # read original again
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(original, readOriginal)

        # any setter makes all values present!
        readExtended2Setter1 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter1.extended_value1 = 2
        self.assertTrue(readExtended2Setter1.is_extended_value1_present())
        self.assertTrue(readExtended2Setter1.is_extended_value2_present())

        readExtended2Setter2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter2.extended_value2 = DEFAULT_EXTENDED_VALUE2
        self.assertTrue(readExtended2Setter2.is_extended_value1_present())
        self.assertTrue(readExtended2Setter2.is_extended_value2_present())

        # bit size as extended2
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter1.bitsizeof())
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitsizeof())

        # initialize offsets as extended2
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter1.initialize_offsets(0))
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter2.initialize_offsets(0))

        # writes as extended2
        bitBuffer = zserio.serialize(readExtended2Setter1)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)
        bitBuffer = zserio.serialize(readExtended2Setter2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

    def testWriteExtended1ReadExtended2(self):
        extended1 = self.api.Extended1(42, 2)
        bitBuffer = zserio.serialize(extended1)
        readExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self.assertTrue(readExtended2.is_extended_value1_present())
        self.assertFalse(readExtended2.is_extended_value2_present())

        self.assertEqual(2, readExtended2.extended_value1)
        # extended value is default constructed
        self.assertEqual(DEFAULT_EXTENDED_VALUE2, readExtended2.extended_value2)

        # bit size as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2.bitsizeof())

        # initialize offsets as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2.initialize_offsets(0))

        # write as extended1
        bitBuffer = zserio.serialize(readExtended2)
        self.assertEqual(EXTENDED1_BIT_SIZE, bitBuffer.bitsize)

        # read extended1 again
        readExtended1 = zserio.deserialize(self.api.Extended1, bitBuffer)
        self.assertEqual(extended1, readExtended1)

        # read original
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(42, readOriginal.value)

        # setter of actually present field will not make all fields present
        readExtended2Setter1 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter1.extended_value1 = 2
        self.assertTrue(readExtended2Setter1.is_extended_value1_present())
        self.assertFalse(readExtended2Setter1.is_extended_value2_present())

        # setter of non-present field makes all fields present
        readExtended2Setter2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter2.extended_value2 = DEFAULT_EXTENDED_VALUE2
        self.assertTrue(readExtended2Setter2.is_extended_value1_present())
        self.assertTrue(readExtended2Setter2.is_extended_value2_present())

        # bit size as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2Setter1.bitsizeof())

        # bit size as extended2
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter2.bitsizeof())

        # initialize offsets as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2Setter1.initialize_offsets(0))

        # initialize offsets as extended2
        self.assertEqual(EXTENDED2_BIT_SIZE, readExtended2Setter2.initialize_offsets(0))

        # writes as extended1
        bitBuffer = zserio.serialize(readExtended2Setter1)
        self.assertEqual(EXTENDED1_BIT_SIZE, bitBuffer.bitsize)

        # writes as extended2
        bitBuffer = zserio.serialize(readExtended2Setter2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

    def testWriteExtended2ReadOriginal(self):
        extended2 = self.api.Extended2(42, 2, DEFAULT_EXTENDED_VALUE2)
        bitBuffer = zserio.serialize(extended2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended2.value, readOriginal.value)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

    def testWriteExtended2ReadExtended1(self):
        extended2 = self.api.Extended2(42, 2, DEFAULT_EXTENDED_VALUE2)
        bitBuffer = zserio.serialize(extended2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readExtended1 = self.api.Extended1.from_reader(reader)
        self.assertEqual(extended2.value, readExtended1.value)
        self.assertEqual(EXTENDED1_BIT_SIZE, reader.bitposition)


DEFAULT_EXTENDED_VALUE2 = "test"

ORIGINAL_BIT_SIZE = 4 * 8
EXTENDED1_BIT_SIZE = ORIGINAL_BIT_SIZE + 4
EXTENDED2_BIT_SIZE = zserio.bitposition.alignto(8, EXTENDED1_BIT_SIZE) + zserio.bitsizeof.bitsizeof_string(
    DEFAULT_EXTENDED_VALUE2
)
