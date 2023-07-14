import unittest
import zserio

from testutils import getZserioApi

class ExtendedChoiceFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "extended_members.zs").extended_choice_field

    def testConstructor(self):
        extended = self.api.Extended()

        # always present when not read from stream
        self.assertTrue(extended.is_extended_value_present())

        # default initialized
        self.assertEqual(0, extended.num_elements)

        self.assertIsNone(extended.extended_value)

        extended = self.api.Extended(1, self.api.Choice(1))
        extended.extended_value.value = 42
        self.assertTrue(extended.is_extended_value_present())
        self.assertEqual(1, extended.num_elements)
        self.assertEqual(self.api.Choice.CHOICE_VALUE, extended.extended_value.choice_tag)
        self.assertEqual(42, extended.extended_value.value)

    def testEq(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(extended1, extended2)

        # do not re-initialize children until the choice is properly set in setExtendedValue
        extended1.num_elements = 1
        self.assertNotEqual(extended1, extended2)
        extended2.num_elements = 1
        self.assertEqual(extended1, extended2)

        extendedValue = self.api.Choice(1, value_=42)
        extended2.extended_value = extendedValue
        self.assertNotEqual(extended1, extended2)
        extended1.extended_value = extendedValue
        self.assertEqual(extended1, extended2)

    def testHash(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.num_elements = len(VALUES)
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.num_elements = len(VALUES)
        self.assertEqual(hash(extended1), hash(extended2))

        extendedValue = self.api.Choice(len(VALUES), values_=VALUES)
        extended2.extended_value = extendedValue
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.extended_value = extendedValue
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extendedEmpty = self.api.Extended(0, self.api.Choice(0))
        self.assertEqual(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.bitsizeof())

        extendedValue = self.api.Extended(1, self.api.Choice(1))
        extendedValue.extended_value.value = 42
        self.assertEqual(EXTENDED_BIT_SIZE_VALUE, extendedValue.bitsizeof())

        extendedValues = self.api.Extended(len(VALUES), self.api.Choice(len(VALUES)))
        extendedValues.extended_value.values = VALUES
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, extendedValues.bitsizeof())

    def testInitializeOffsets(self):
        extendedEmpty = self.api.Extended(0, self.api.Choice(0))
        self.assertEqual(EXTENDED_BIT_SIZE_EMPTY, extendedEmpty.initialize_offsets(0))

        extendedValue = self.api.Extended(1, self.api.Choice(1))
        extendedValue.extended_value.value = 42
        self.assertEqual(EXTENDED_BIT_SIZE_VALUE, extendedValue.initialize_offsets(0))

        extendedValues = self.api.Extended(len(VALUES), self.api.Choice(len(VALUES)))
        extendedValues.extended_value.values = VALUES
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, extendedValues.initialize_offsets(0))

    def testWriteReadExtendedEmpty(self):
        extended = self.api.Extended(0, self.api.Choice(0))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())
        self.assertNotEqual(extended, readExtended)

    def testWriteReadExtendedValue(self):
        extended = self.api.Extended(1, self.api.Choice(1))
        extended.extended_value.value = 42
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_VALUE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteReadExtendedValues(self):
        extended = self.api.Extended(len(VALUES), self.api.Choice(len(VALUES)))
        extended.extended_value.values = VALUES
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_extended_value_present())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtended(self):
        original = self.api.Original(len(VALUES))
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertFalse(readExtended.is_extended_value_present())

        # extended value is default constructed
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
        extendedValue = self.api.Choice(len(VALUES))
        extendedValue.values = VALUES
        readExtended.extended_value = extendedValue
        self.assertTrue(readExtended.is_extended_value_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, readExtended.initialize_offsets(0))

        # write as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, bitBuffer.bitsize)

    def testWriteExtendedEmptyReadOriginal(self):
        extended = self.api.Extended(0, self.api.Choice(0))
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_EMPTY, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.num_elements, readOriginal.num_elements)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

    def testWriteExtendedValueReadOriginal(self):
        extended = self.api.Extended(1, self.api.Choice(1))
        extended.extended_value.value = 42
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_VALUE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.num_elements, readOriginal.num_elements)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

    def testWriteExtendedValuesReadOriginal(self):
        extended = self.api.Extended(len(VALUES), self.api.Choice(len(VALUES)))
        extended.extended_value.values = VALUES
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE_VALUES, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.num_elements, readOriginal.num_elements)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

ORIGINAL_BIT_SIZE = 4 * 8
EXTENDED_BIT_SIZE_EMPTY = ORIGINAL_BIT_SIZE
EXTENDED_BIT_SIZE_VALUE = ORIGINAL_BIT_SIZE + 4 * 8
VALUES = [ 0, 1, 2, 3, 4 ]
EXTENDED_BIT_SIZE_VALUES = ORIGINAL_BIT_SIZE + len(VALUES) * 4 * 8
