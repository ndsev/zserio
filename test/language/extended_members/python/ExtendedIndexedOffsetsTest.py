import unittest
import zserio

from testutils import getZserioApi

class ExtendedIndexedOffsetsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "extended_members.zs").extended_indexed_offsets

    def testDefaultConstructor(self):
        extended = self.api.Extended()

        # always present when not read from stream
        self.assertTrue(extended.is_array_present())

        # default initialized
        self.assertEqual(0, len(extended.offsets))
        self.assertEqual(0, len(extended.array))

        extended = self.api.Extended(OFFSETS, ARRAY)
        self.assertTrue(extended.is_array_present())

        self.assertEqual(OFFSETS, extended.offsets)
        self.assertEqual(ARRAY, extended.array)

    def testEq(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(extended1, extended2)

        extended1.offsets = OFFSETS
        self.assertNotEqual(extended1, extended2)
        extended2.offsets = OFFSETS
        self.assertEqual(extended1, extended2)

        extended2.array = ARRAY
        self.assertNotEqual(extended1, extended2)
        extended1.array = ARRAY
        self.assertEqual(extended1, extended2)

    def testHash(self):
        extended1 = self.api.Extended()
        extended2 = self.api.Extended()
        self.assertEqual(hash(extended1), hash(extended2))

        extended1.offsets = OFFSETS
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended2.offsets = OFFSETS
        self.assertEqual(hash(extended1), hash(extended2))

        extended2.array = ARRAY
        self.assertNotEqual(hash(extended1), hash(extended2))
        extended1.array = ARRAY
        self.assertEqual(hash(extended1), hash(extended2))

    def testBitSizeOf(self):
        extended = self.api.Extended(OFFSETS, ARRAY)
        self.assertEqual(EXTENDED_BIT_SIZE, extended.bitsizeof())

    def testInitializeOffsets(self):
        extended = self.api.Extended(OFFSETS, ARRAY)
        self.assertEqual(EXTENDED_BIT_SIZE, extended.initialize_offsets(0))

    def testWriteReadExtended(self):
        extended = self.api.Extended(OFFSETS, ARRAY)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertTrue(readExtended.is_array_present())
        self.assertEqual(extended, readExtended)

    def testWriteOriginalReadExtended(self):
        original = self.api.Original(OFFSETS)
        bitBuffer = zserio.serialize(original)
        readExtended = zserio.deserialize(self.api.Extended, bitBuffer)
        self.assertFalse(readExtended.is_array_present())

        # extended value is default constructed
        self.assertEqual(0, len(readExtended.array))

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
        readExtended.array = ARRAY
        self.assertTrue(readExtended.is_array_present())

        # bit size as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.bitsizeof())

        # initialize offsets as extended
        self.assertEqual(EXTENDED_BIT_SIZE, readExtended.initialize_offsets(0))

        # writes as extended
        bitBuffer = zserio.serialize(readExtended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

    def testWriteExtendedReadOriginal(self):
        extended = self.api.Extended(OFFSETS, ARRAY)
        bitBuffer = zserio.serialize(extended)
        self.assertEqual(EXTENDED_BIT_SIZE, bitBuffer.bitsize)

        reader = zserio.BitStreamReader.from_bitbuffer(bitBuffer)
        readOriginal = self.api.Original.from_reader(reader)
        self.assertEqual(extended.offsets, readOriginal.offsets)
        self.assertEqual(ORIGINAL_BIT_SIZE, reader.bitposition)

OFFSETS = [ 0, 0, 0, 0, 0 ]
ARRAY = [ "extended", "indexed", "offsets", "test", "!" ]

ORIGINAL_BIT_SIZE = zserio.bitsizeof.bitsizeof_varsize(len(OFFSETS)) + len(OFFSETS) * 4 * 8
EXTENDED_BIT_SIZE = (ORIGINAL_BIT_SIZE +
                     zserio.bitsizeof.bitsizeof_varsize(len(ARRAY)) +
                     sum(zserio.bitsizeof.bitsizeof_string(element) for element in ARRAY))
