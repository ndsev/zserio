import unittest
import zserio

from testutils import getZserioApi

class UInt64ParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").uint64_param_choice

    def testSelectorConstructor(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(self.VARIANT_A_SELECTOR, uint64ParamChoice.getSelector())

    def testFromReader(self):
        selector = self.VARIANT_B_SELECTOR
        value = 234
        writer = zserio.BitStreamWriter()
        UInt64ParamChoiceTest._writeUInt64ParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray())
        uint64ParamChoice = self.api.UInt64ParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, uint64ParamChoice.getSelector())
        self.assertEqual(value, uint64ParamChoice.getB())

    def testEq(self):
        uint64ParamChoice1 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        uint64ParamChoice2 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertTrue(uint64ParamChoice1 == uint64ParamChoice2)

        value = 99
        uint64ParamChoice1.setA(value)
        self.assertFalse(uint64ParamChoice1 == uint64ParamChoice2)

        uint64ParamChoice2.setA(value)
        self.assertTrue(uint64ParamChoice1 == uint64ParamChoice2)

        diffValue = value + 1
        uint64ParamChoice2.setA(diffValue)
        self.assertFalse(uint64ParamChoice1 == uint64ParamChoice2)

    def testHash(self):
        uint64ParamChoice1 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        uint64ParamChoice2 = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(hash(uint64ParamChoice1), hash(uint64ParamChoice2))

        value = 99
        uint64ParamChoice1.setA(value)
        self.assertTrue(hash(uint64ParamChoice1) != hash(uint64ParamChoice2))

        uint64ParamChoice2.setA(value)
        self.assertEqual(hash(uint64ParamChoice1), hash(uint64ParamChoice2))

        diffValue = value + 1
        uint64ParamChoice2.setA(diffValue)
        self.assertTrue(hash(uint64ParamChoice1) != hash(uint64ParamChoice2))

    def testGetSelector(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(self.VARIANT_C_SELECTOR, uint64ParamChoice.getSelector())

    def testGetSetA(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        value = 99
        uint64ParamChoice.setA(value)
        self.assertEqual(value, uint64ParamChoice.getA())

    def testGetSetB(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        value = 234
        uint64ParamChoice.setB(value)
        self.assertEqual(value, uint64ParamChoice.getB())

    def testGetSetC(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_C_SELECTOR)
        value = 23456
        uint64ParamChoice.setC(value)
        self.assertEqual(value, uint64ParamChoice.getC())

    def testBitSizeOf(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(8, uint64ParamChoice.bitSizeOf())

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(16, uint64ParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        bitPosition = 1
        self.assertEqual(9, uint64ParamChoice.initializeOffsets(bitPosition))

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(17, uint64ParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        byteValue = 99
        uint64ParamChoice.setA(byteValue)
        writer = zserio.BitStreamWriter()
        uint64ParamChoice.write(writer)
        readUInt64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt64ParamChoice.read(reader)
        self.assertEqual(byteValue, readUInt64ParamChoice.getA())
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

        uint64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        shortValue = 234
        uint64ParamChoice.setB(shortValue)
        writer = zserio.BitStreamWriter()
        uint64ParamChoice.write(writer)
        readUInt64ParamChoice = self.api.UInt64ParamChoice(self.VARIANT_B_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt64ParamChoice.read(reader)
        self.assertEqual(shortValue, readUInt64ParamChoice.getB())
        self.assertEqual(uint64ParamChoice, readUInt64ParamChoice)

    @staticmethod
    def _writeUInt64ParamChoiceToStream(writer, selector, value):
        if selector == 1:
            writer.writeSignedBits(value, 8)
        elif selector in (2, 3, 4):
            writer.writeSignedBits(value, 16)
        elif selector in (5, 6):
            pass
        else:
            writer.writeSignedBits(value, 32)

    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR = 2
    VARIANT_C_SELECTOR = 7
