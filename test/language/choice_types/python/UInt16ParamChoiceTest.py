import unittest
import zserio

from testutils import getZserioApi

class UInt16ParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").uint16_param_choice

    def testSelectorConstructor(self):
        tag = self.VARIANT_A_SELECTOR
        uint16ParamChoice = self.api.UInt16ParamChoice(tag)
        self.assertEqual(tag, uint16ParamChoice.getTag())

    def testFromReader(self):
        tag = self.VARIANT_B_SELECTOR1
        value = 234
        writer = zserio.BitStreamWriter()
        UInt16ParamChoiceTest._writeUInt16ParamChoiceToStream(writer, tag, value)
        reader = zserio.BitStreamReader(writer.getByteArray())
        uint16ParamChoice = self.api.UInt16ParamChoice.fromReader(reader, tag)
        self.assertEqual(tag, uint16ParamChoice.getTag())
        self.assertEqual(value, uint16ParamChoice.getB())

    def testEq(self):
        uint16ParamChoice1 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        uint16ParamChoice2 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertTrue(uint16ParamChoice1 == uint16ParamChoice2)

        value = 99
        uint16ParamChoice1.setA(value)
        self.assertFalse(uint16ParamChoice1 == uint16ParamChoice2)

        uint16ParamChoice2.setA(value)
        self.assertTrue(uint16ParamChoice1 == uint16ParamChoice2)

        diffValue = value + 1
        uint16ParamChoice2.setA(diffValue)
        self.assertFalse(uint16ParamChoice1 == uint16ParamChoice2)

    def testHash(self):
        uint16ParamChoice1 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        uint16ParamChoice2 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(hash(uint16ParamChoice1), hash(uint16ParamChoice2))

        value = 99
        uint16ParamChoice1.setA(value)
        self.assertTrue(hash(uint16ParamChoice1) != hash(uint16ParamChoice2))

        uint16ParamChoice2.setA(value)
        self.assertEqual(hash(uint16ParamChoice1), hash(uint16ParamChoice2))

        diffValue = value + 1
        uint16ParamChoice2.setA(diffValue)
        self.assertTrue(hash(uint16ParamChoice1) != hash(uint16ParamChoice2))

    def testGetTag(self):
        tag = self.EMPTY_SELECTOR2
        uint16ParamChoice = self.api.UInt16ParamChoice(tag)
        self.assertEqual(tag, uint16ParamChoice.getTag())

    def testGetSetA(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        value = 99
        uint16ParamChoice.setA(value)
        self.assertEqual(value, uint16ParamChoice.getA())

    def testGetSetB(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR3)
        value = 234
        uint16ParamChoice.setB(value)
        self.assertEqual(value, uint16ParamChoice.getB())

    def testGetSetC(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        value = 65535
        uint16ParamChoice.setC(value)
        self.assertEqual(value, uint16ParamChoice.getC())

    def testBitSizeOf(self):
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint16ParamChoiceA.setA(byteValueA)
        self.assertEqual(8, uint16ParamChoiceA.bitSizeOf())

        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR2)
        shortValueB = 234
        uint16ParamChoiceB.setB(shortValueB)
        self.assertEqual(16, uint16ParamChoiceB.bitSizeOf())

        uint16ParamChoiceEmpty = self.api.UInt16ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(0, uint16ParamChoiceEmpty.bitSizeOf())

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint16ParamChoiceC.setC(intValueC)
        self.assertEqual(32, uint16ParamChoiceC.bitSizeOf())

    def testInitializeOffsets(self):
        bitPosition = 1
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(9, uint16ParamChoiceA.initializeOffsets(bitPosition))

        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR2)
        self.assertEqual(17, uint16ParamChoiceB.initializeOffsets(bitPosition))

        uint16ParamChoiceEmpty = self.api.UInt16ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(1, uint16ParamChoiceEmpty.initializeOffsets(bitPosition))

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(33, uint16ParamChoiceC.initializeOffsets(bitPosition))

    def testReadWrite(self):
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint16ParamChoiceA.setA(byteValueA)
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceA.write(writer)
        readUInt16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt16ParamChoiceA.read(reader)
        self.assertEqual(byteValueA, readUInt16ParamChoiceA.getA())
        self.assertEqual(uint16ParamChoiceA, readUInt16ParamChoiceA)

        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR1)
        shortValueB = 234
        uint16ParamChoiceB.setB(shortValueB)
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceB.write(writer)
        readUInt16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR1)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt16ParamChoiceB.read(reader)
        self.assertEqual(shortValueB, readUInt16ParamChoiceB.getB())
        self.assertEqual(uint16ParamChoiceB, readUInt16ParamChoiceB)

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint16ParamChoiceC.setC(intValueC)
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceC.write(writer)
        readUInt16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt16ParamChoiceC.read(reader)
        self.assertEqual(intValueC, readUInt16ParamChoiceC.getC())
        self.assertEqual(uint16ParamChoiceC, readUInt16ParamChoiceC)

    @staticmethod
    def _writeUInt16ParamChoiceToStream(writer, tag, value):
        if tag == 1:
            writer.writeSignedBits(value, 8)
        elif tag in (2, 3, 4):
            writer.writeSignedBits(value, 16)
        elif tag in (5, 6):
            pass
        else:
            writer.writeSignedBits(value, 32)

    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR1 = 2
    VARIANT_B_SELECTOR2 = 3
    VARIANT_B_SELECTOR3 = 4
    EMPTY_SELECTOR1 = 5
    EMPTY_SELECTOR2 = 6
    VARIANT_C_SELECTOR = 7
