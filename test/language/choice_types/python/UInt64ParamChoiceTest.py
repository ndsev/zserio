import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class UInt64ParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").uint64_param_choice

    def testConstructor(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(self.VARIANT_A_SELECTOR, uint64ParamChoice.selector)

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR, b_=1234)
        self.assertEqual(self.VARIANT_B_SELECTOR, uint64ParamChoice.selector)
        self.assertEqual(1234, uint64ParamChoice.b)

    def testFromReader(self):
        selector = self.VARIANT_B_SELECTOR
        value = 234
        writer = zserio.BitStreamWriter()
        UInt64ParamChoiceTest._writeUInt64ParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        uint64ParamChoice = self.api.UInt64ParamChoice.from_reader(reader, selector)
        self.assertEqual(selector, uint64ParamChoice.selector)
        self.assertEqual(value, uint64ParamChoice.b)

    def testEq(self):
        uint64ParamChoice1 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        uint64ParamChoice2 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertTrue(uint64ParamChoice1 == uint64ParamChoice2)

        value = 99
        uint64ParamChoice1.a = value
        self.assertFalse(uint64ParamChoice1 == uint64ParamChoice2)

        uint64ParamChoice2.a = value
        self.assertTrue(uint64ParamChoice1 == uint64ParamChoice2)

        diffValue = value + 1
        uint64ParamChoice2.a = diffValue
        self.assertFalse(uint64ParamChoice1 == uint64ParamChoice2)

    def testHash(self):
        uint64ParamChoice1 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        uint64ParamChoice2 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(hash(uint64ParamChoice1), hash(uint64ParamChoice2))

        value = 99
        uint64ParamChoice1.a = value
        self.assertTrue(hash(uint64ParamChoice1) != hash(uint64ParamChoice2))

        uint64ParamChoice2.a = value
        self.assertEqual(hash(uint64ParamChoice1), hash(uint64ParamChoice2))

        diffValue = value + 1
        uint64ParamChoice2.a = diffValue
        self.assertTrue(hash(uint64ParamChoice1) != hash(uint64ParamChoice2))

    def testGetSelector(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(self.VARIANT_C_SELECTOR, uint64ParamChoice.selector)

    def testGetSetA(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        value = 99
        uint64ParamChoice.a = value
        self.assertEqual(value, uint64ParamChoice.a)

    def testGetSetB(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        value = 234
        uint64ParamChoice.b = value
        self.assertEqual(value, uint64ParamChoice.b)

    def testGetSetC(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_C_SELECTOR)
        value = 23456
        uint64ParamChoice.c = value
        self.assertEqual(value, uint64ParamChoice.c)

    def testChoiceTag(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(uint64ParamChoice.CHOICE_A, uint64ParamChoice.choice_tag)

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(uint64ParamChoice.CHOICE_B, uint64ParamChoice.choice_tag)

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(uint64ParamChoice.CHOICE_C, uint64ParamChoice.choice_tag)

        uint64ParamChoice = self.api.UInt64ParamChoice(self.EMPTY_SELECTOR)
        self.assertEqual(uint64ParamChoice.UNDEFINED_CHOICE, uint64ParamChoice.choice_tag)

    def testBitSizeOf(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(8, uint64ParamChoice.bitsizeof())

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(16, uint64ParamChoice.bitsizeof())

    def testInitializeOffsets(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        bitPosition = 1
        self.assertEqual(9, uint64ParamChoice.initialize_offsets(bitPosition))

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(17, uint64ParamChoice.initialize_offsets(bitPosition))

    def testWriteRead(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        byteValue = 99
        uint64ParamChoice.a = byteValue
        writer = zserio.BitStreamWriter()
        uint64ParamChoice.write(writer)
        readUInt64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUInt64ParamChoice.read(reader)
        self.assertEqual(byteValue, readUInt64ParamChoice.a)
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

        shortValue = 234
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR, b_=shortValue)
        writer = zserio.BitStreamWriter()
        uint64ParamChoice.write(writer)
        readUInt64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUInt64ParamChoice.read(reader)
        self.assertEqual(shortValue, readUInt64ParamChoice.b)
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

    def testWriteReadFile(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        byteValue = 99
        uint64ParamChoice.a = byteValue
        filenameA = self.BLOB_NAME_BASE + "a.blob"
        zserio.serialize_to_file(uint64ParamChoice, filenameA)

        readUInt64ParamChoice = zserio.deserialize_from_file(self.api.UInt64ParamChoice, filenameA,
                                                             self.VARIANT_A_SELECTOR)
        self.assertEqual(byteValue, readUInt64ParamChoice.a)
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

        shortValue = 234
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR, b_=shortValue)
        filenameB = self.BLOB_NAME_BASE + "b.blob"
        zserio.serialize_to_file(uint64ParamChoice, filenameB)

        readUInt64ParamChoice = zserio.deserialize_from_file(self.api.UInt64ParamChoice, filenameB,
                                                             self.VARIANT_B_SELECTOR)
        self.assertEqual(shortValue, readUInt64ParamChoice.b)
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

    @staticmethod
    def _writeUInt64ParamChoiceToStream(writer, selector, value):
        if selector == 1:
            writer.write_signed_bits(value, 8)
        elif selector in (2, 3, 4):
            writer.write_signed_bits(value, 16)
        elif selector in (5, 6):
            pass
        else:
            writer.write_signed_bits(value, 32)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "uint64_param_choice_")
    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR = 2
    VARIANT_C_SELECTOR = 7
    EMPTY_SELECTOR = 5
