import zserio

import ExtendedMembers

class MultipleExtendedFieldsVariousTypesTest(ExtendedMembers.TestCase):
    def testConstructor(self):
        extended2 = self.api.Extended2()

        # always present when not read from stream
        self._checkAllExtendedFieldsPresent(extended2, True)

        # default constructed
        self.assertFalse(extended2.is_extended_value1_set())
        self.assertFalse(extended2.is_extended_value1_used())
        self.assertIsNone(extended2.extended_value2)
        self.assertEqual(0, len(extended2.extended_value3))
        self.assertFalse(extended2.is_extended_value4_set())
        self.assertFalse(extended2.is_extended_value4_used())
        self.assertEqual(0, extended2.extended_value5)
        self.assertEqual(0, len(extended2.extended_value6))
        self.assertIsNone(extended2.extended_value7)
        self.assertFalse(extended2.is_extended_value8_set())
        self.assertFalse(extended2.is_extended_value8_used())
        self.assertFalse(extended2.is_extended_value9_set())
        self.assertFalse(extended2.is_extended_value9_used())

        extendedValue7 = self.api.Union(EXTENDED_VALUE5)
        extended2 = self.api.Extended2(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3, None,
                                       EXTENDED_VALUE5, EXTENDED_VALUE6, extendedValue7, None, EXTENDED_VALUE9)
        self._checkAllExtendedFieldsPresent(extended2, True)
        self.assertTrue(extended2.is_extended_value1_set())
        self.assertTrue(extended2.is_extended_value1_used())
        self.assertEqual(EXTENDED_VALUE1, extended2.extended_value1)
        self.assertEqual(EXTENDED_VALUE2, extended2.extended_value2)
        self.assertEqual(EXTENDED_VALUE3, extended2.extended_value3)
        self.assertFalse(extended2.is_extended_value4_set())
        self.assertFalse(extended2.is_extended_value4_used())
        self.assertEqual(EXTENDED_VALUE5, extended2.extended_value5)
        self.assertEqual(EXTENDED_VALUE6, extended2.extended_value6)
        self.assertEqual(extendedValue7, extended2.extended_value7)
        self.assertFalse(extended2.is_extended_value8_set())
        self.assertFalse(extended2.is_extended_value8_used())
        self.assertEqual(EXTENDED_VALUE9, extended2.extended_value9)

    def testEq(self):
        extended1 = self.api.Extended2()
        extended2 = self.api.Extended2()
        extended3 = self._createExtended2()
        extended4 = self._createExtended2()

        self.assertEqual(extended1, extended2)
        self.assertNotEqual(extended1, extended3)
        self.assertEqual(extended3, extended4)

        extended3.extended_value9 = 0
        self.assertNotEqual(extended3, extended4)

    def testHash(self):
        extended1 = self.api.Extended2()
        extended2 = self.api.Extended2()
        extended3 = self._createExtended2()
        extended4 = self._createExtended2()

        self.assertEqual(hash(extended1), hash(extended2))
        self.assertNotEqual(hash(extended1), hash(extended3))
        self.assertEqual(hash(extended3), hash(extended4))

        extended3.extended_value9 = 0
        self.assertNotEqual(hash(extended3), hash(extended4))

    def testBitSizeOf(self):
        extended1 = self._createExtended1()
        self.assertEqual(EXTENDED1_BIT_SIZE, extended1.bitsizeof())

        extended2 = self._createExtended2()
        self.assertEqual(EXTENDED2_BIT_SIZE, extended2.bitsizeof())

    def testInitializeOffsets(self):
        extended1 = self._createExtended1()
        self.assertEqual(EXTENDED1_BIT_SIZE, extended1.initialize_offsets(0))

        extended2 = self._createExtended2()
        self.assertEqual(EXTENDED2_BIT_SIZE, extended2.initialize_offsets(0))

    def testWriteReadExtended2(self):
        extended2 = self._createExtended2()
        bitBuffer = zserio.serialize(extended2)
        self.assertEqual(EXTENDED2_BIT_SIZE, bitBuffer.bitsize)

        readExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self._checkAllExtendedFieldsPresent(extended2, True)
        self.assertEqual(extended2, readExtended2)

    def testWriteOriginalReadExtended2(self):
        original = self.api.Original(VALUE)
        bitBuffer = zserio.serialize(original)
        readExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self._checkAllExtendedFieldsPresent(readExtended2, False)

        # extended fields are default constructed
        self.assertFalse(readExtended2.is_extended_value1_set())
        self.assertFalse(readExtended2.is_extended_value1_used())
        self.assertIsNone(readExtended2.extended_value2)
        self.assertEqual(0, len(readExtended2.extended_value3))
        self.assertFalse(readExtended2.is_extended_value4_set())
        self.assertFalse(readExtended2.is_extended_value4_used())
        self.assertEqual(0, readExtended2.extended_value5)
        self.assertEqual(0, len(readExtended2.extended_value6))
        self.assertIsNone(readExtended2.extended_value7)
        self.assertFalse(readExtended2.is_extended_value8_set())
        self.assertFalse(readExtended2.is_extended_value8_used())
        self.assertFalse(readExtended2.is_extended_value9_set())
        self.assertFalse(readExtended2.is_extended_value9_used())

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
        readExtended2.extended_value2 = EXTENDED_VALUE2
        self._checkAllExtendedFieldsPresent(readExtended2, True)

    def testWriteExtended1ReadExtended2(self):
        extended1 = self._createExtended1()
        bitBuffer = zserio.serialize(extended1)
        readExtended2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self._checkExtended1FieldsPresent(readExtended2, True)
        self._checkExtended2FieldsPresent(readExtended2, False)

        # extended1 fields are read from the stream
        self.assertTrue(readExtended2.is_extended_value1_set())
        self.assertTrue(readExtended2.is_extended_value1_used())
        self.assertEqual(EXTENDED_VALUE1, readExtended2.extended_value1)
        self.assertEqual(EXTENDED_VALUE2, readExtended2.extended_value2)
        self.assertEqual(EXTENDED_VALUE3, readExtended2.extended_value3)

        # extended2 fields are default constructed
        self.assertFalse(readExtended2.is_extended_value4_set())
        self.assertFalse(readExtended2.is_extended_value4_used())
        self.assertEqual(0, readExtended2.extended_value5)
        self.assertEqual(0, len(readExtended2.extended_value6))
        self.assertIsNone(readExtended2.extended_value7)
        self.assertFalse(readExtended2.is_extended_value8_set())
        self.assertFalse(readExtended2.is_extended_value8_used())
        self.assertFalse(readExtended2.is_extended_value9_set())
        self.assertFalse(readExtended2.is_extended_value9_used())

        # bit size as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2.bitsizeof())

        # initialize offsets as extended1
        self.assertEqual(EXTENDED1_BIT_SIZE, readExtended2.initialize_offsets(0))

        # writes as extended1
        bitBuffer = zserio.serialize(readExtended2)
        self.assertEqual(EXTENDED1_BIT_SIZE, bitBuffer.bitsize)

        # read extended1 again
        readExtended1 = zserio.deserialize(self.api.Extended1, bitBuffer)
        self.assertEqual(extended1, readExtended1)

        # read original
        readOriginal = zserio.deserialize(self.api.Original, bitBuffer)
        self.assertEqual(VALUE, readOriginal.value)

        # resetter of actually present optional field will not make all fields present
        readExtended2Setter1 = zserio.deserialize(self.api.Extended2, bitBuffer)
        self.assertTrue(readExtended2Setter1.is_extended_value1_set())
        readExtended2Setter1.reset_extended_value1() # reset value from Extended1
        self.assertFalse(readExtended2Setter1.is_extended_value1_set())
        self._checkExtended1FieldsPresent(readExtended2Setter1, True)
        self._checkExtended2FieldsPresent(readExtended2Setter1, False)

        # setter of actually present field will not make all fields present
        readExtended2Setter2 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter2.extended_value2 = EXTENDED_VALUE2 # set value from Extended1
        self._checkExtended1FieldsPresent(readExtended2Setter2, True)
        self._checkExtended2FieldsPresent(readExtended2Setter2, False)

        # setter of non-present field will make all fields present
        readExtended2Setter5 = zserio.deserialize(self.api.Extended2, bitBuffer)
        readExtended2Setter5.extended_value5 = EXTENDED_VALUE5 # set value from Extended2
        self._checkAllExtendedFieldsPresent(readExtended2Setter5, True)

    def _checkExtended1FieldsPresent(self, extended2, expectedExtended1FieldsPresent):
        self.assertEqual(expectedExtended1FieldsPresent, extended2.is_extended_value1_present())
        self.assertEqual(expectedExtended1FieldsPresent, extended2.is_extended_value2_present())
        self.assertEqual(expectedExtended1FieldsPresent, extended2.is_extended_value3_present())

    def _checkExtended2FieldsPresent(self, extended2, expectedExtended2FieldsPresent):
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value4_present())
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value5_present())
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value6_present())
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value7_present())
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value8_present())
        self.assertEqual(expectedExtended2FieldsPresent, extended2.is_extended_value9_present())

    def _checkAllExtendedFieldsPresent(self, extended2, expectedPresent):
        self._checkExtended1FieldsPresent(extended2, expectedPresent)
        self._checkExtended2FieldsPresent(extended2, expectedPresent)

    def _createExtended1(self):
        return self.api.Extended1(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3)

    def _createExtended2(self):
        return self.api.Extended2(VALUE, EXTENDED_VALUE1, EXTENDED_VALUE2, EXTENDED_VALUE3,
                                  None, EXTENDED_VALUE5, EXTENDED_VALUE6,
                                  self.api.Union(EXTENDED_VALUE5, value_u32_ = zserio.limits.UINT32_MAX),
                                  None, EXTENDED_VALUE9)

    @staticmethod
    def _calcExtended1BitSize():
        bitSize = ORIGINAL_BIT_SIZE
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += 1 + 4 * 8 # optional extendedValue1
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += zserio.bitsizeof.bitsizeof_bitbuffer(EXTENDED_VALUE2)
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += zserio.bitsizeof.bitsizeof_bytes(EXTENDED_VALUE3)
        return bitSize

    @staticmethod
    def _calcExtended2BitSize():
        bitSize = MultipleExtendedFieldsVariousTypesTest._calcExtended1BitSize()
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += 1 # unset optional extendedValue4
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += zserio.bitsizeof.bitsizeof_varsize(EXTENDED_VALUE5)
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += sum(zserio.bitsizeof.bitsizeof_string(element) for element in EXTENDED_VALUE6)
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += 8 + 4 * 8 # extendedValue7 (choiceTag + valueU32)
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += 1 # unset optional extendedValue8
        bitSize = zserio.bitposition.alignto(8, bitSize)
        bitSize += EXTENDED_VALUE5 # used non-optional dynamic bit field extendedValue9
        return bitSize

VALUE = -13
EXTENDED_VALUE1 = 42
EXTENDED_VALUE2 = zserio.BitBuffer(bytes([0xCA, 0xFE]), 16)
EXTENDED_VALUE3 = bytearray([0xDE, 0xAD])
EXTENDED_VALUE5 = 3
EXTENDED_VALUE6 = [ "this", "is", "test" ]
EXTENDED_VALUE9 = 7 # bit<EXTENDED_VALUE5> == bit<3>

ORIGINAL_BIT_SIZE = 7
EXTENDED1_BIT_SIZE = MultipleExtendedFieldsVariousTypesTest._calcExtended1BitSize()
EXTENDED2_BIT_SIZE = MultipleExtendedFieldsVariousTypesTest._calcExtended2BitSize()
