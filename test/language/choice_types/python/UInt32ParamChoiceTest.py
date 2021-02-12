import unittest
import zserio

from testutils import getZserioApi

class UInt32ParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").uint32_param_choice

    def testConstructor(self):
        selector = self.VARIANT_A_SELECTOR
        uint32ParamChoice = self.api.UInt32ParamChoice(selector)
        self.assertEqual(selector, uint32ParamChoice.getSelector())

        selector = self.VARIANT_B_SELECTOR1
        uint32ParamChoice = self.api.UInt32ParamChoice(selector, b_=1234)
        self.assertEqual(selector, uint32ParamChoice.getSelector())
        self.assertEqual(1234, uint32ParamChoice.getB())

    def testFromReader(self):
        selector = self.VARIANT_B_SELECTOR1
        value = 234
        writer = zserio.BitStreamWriter()
        UInt32ParamChoiceTest._writeUInt32ParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        uint32ParamChoice = self.api.UInt32ParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, uint32ParamChoice.getSelector())
        self.assertEqual(value, uint32ParamChoice.getB())

    def testEq(self):
        uint32ParamChoice1 = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        uint32ParamChoice2 = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertTrue(uint32ParamChoice1 == uint32ParamChoice2)

        value = 99
        uint32ParamChoice1.setA(value)
        self.assertFalse(uint32ParamChoice1 == uint32ParamChoice2)

        uint32ParamChoice2.setA(value)
        self.assertTrue(uint32ParamChoice1 == uint32ParamChoice2)

        diffValue = value + 1
        uint32ParamChoice2.setA(diffValue)
        self.assertFalse(uint32ParamChoice1 == uint32ParamChoice2)

    def testHash(self):
        uint32ParamChoice1 = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        uint32ParamChoice2 = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(hash(uint32ParamChoice1), hash(uint32ParamChoice2))

        value = 99
        uint32ParamChoice1.setA(value)
        self.assertTrue(hash(uint32ParamChoice1) != hash(uint32ParamChoice2))

        uint32ParamChoice2.setA(value)
        self.assertEqual(hash(uint32ParamChoice1), hash(uint32ParamChoice2))

        diffValue = value + 1
        uint32ParamChoice2.setA(diffValue)
        self.assertTrue(hash(uint32ParamChoice1) != hash(uint32ParamChoice2))

    def testGetSelector(self):
        selector = self.EMPTY_SELECTOR2
        uint32ParamChoice = self.api.UInt32ParamChoice(selector)
        self.assertEqual(selector, uint32ParamChoice.getSelector())

    def testGetSetA(self):
        uint32ParamChoice = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        value = 99
        uint32ParamChoice.setA(value)
        self.assertEqual(value, uint32ParamChoice.getA())

    def testGetSetB(self):
        uint32ParamChoice = self.api.UInt32ParamChoice(self.VARIANT_B_SELECTOR3)
        value = 234
        uint32ParamChoice.setB(value)
        self.assertEqual(value, uint32ParamChoice.getB())

    def testGetSetC(self):
        uint32ParamChoice = self.api.UInt32ParamChoice(self.VARIANT_C_SELECTOR)
        value = 65535
        uint32ParamChoice.setC(value)
        self.assertEqual(value, uint32ParamChoice.getC())

    def testBitSizeOf(self):
        uint32ParamChoiceA = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint32ParamChoiceA.setA(byteValueA)
        self.assertEqual(8, uint32ParamChoiceA.bitSizeOf())

        uint32ParamChoiceB = self.api.UInt32ParamChoice(self.VARIANT_B_SELECTOR2)
        shortValueB = 234
        uint32ParamChoiceB.setB(shortValueB)
        self.assertEqual(16, uint32ParamChoiceB.bitSizeOf())

        uint32ParamChoiceEmpty = self.api.UInt32ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(0, uint32ParamChoiceEmpty.bitSizeOf())

        uint32ParamChoiceC = self.api.UInt32ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint32ParamChoiceC.setC(intValueC)
        self.assertEqual(32, uint32ParamChoiceC.bitSizeOf())

    def testInitializeOffsets(self):
        bitPosition = 1
        uint32ParamChoiceA = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(9, uint32ParamChoiceA.initializeOffsets(bitPosition))

        uint32ParamChoiceB = self.api.UInt32ParamChoice(self.VARIANT_B_SELECTOR2)
        self.assertEqual(17, uint32ParamChoiceB.initializeOffsets(bitPosition))

        uint32ParamChoiceEmpty = self.api.UInt32ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(1, uint32ParamChoiceEmpty.initializeOffsets(bitPosition))

        uint32ParamChoiceC = self.api.UInt32ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(33, uint32ParamChoiceC.initializeOffsets(bitPosition))

    def testReadWrite(self):
        uint32ParamChoiceA = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint32ParamChoiceA.setA(byteValueA)
        writer = zserio.BitStreamWriter()
        uint32ParamChoiceA.write(writer)
        readUInt32ParamChoiceA = self.api.UInt32ParamChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readUInt32ParamChoiceA.read(reader)
        self.assertEqual(byteValueA, readUInt32ParamChoiceA.getA())
        self.assertEqual(uint32ParamChoiceA, readUInt32ParamChoiceA)

        shortValueB = 234
        uint32ParamChoiceB = self.api.UInt32ParamChoice(self.VARIANT_B_SELECTOR1, b_=shortValueB)
        writer = zserio.BitStreamWriter()
        uint32ParamChoiceB.write(writer)
        readUInt32ParamChoiceB = self.api.UInt32ParamChoice(self.VARIANT_B_SELECTOR1)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readUInt32ParamChoiceB.read(reader)
        self.assertEqual(shortValueB, readUInt32ParamChoiceB.getB())
        self.assertEqual(uint32ParamChoiceB, readUInt32ParamChoiceB)

        uint32ParamChoiceC = self.api.UInt32ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint32ParamChoiceC.setC(intValueC)
        writer = zserio.BitStreamWriter()
        uint32ParamChoiceC.write(writer)
        readUInt32ParamChoiceC = self.api.UInt32ParamChoice(self.VARIANT_C_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readUInt32ParamChoiceC.read(reader)
        self.assertEqual(intValueC, readUInt32ParamChoiceC.getC())
        self.assertEqual(uint32ParamChoiceC, readUInt32ParamChoiceC)

    @staticmethod
    def _writeUInt32ParamChoiceToStream(writer, selector, value):
        if selector == 1:
            writer.writeSignedBits(value, 8)
        elif selector in (2, 3, 4):
            writer.writeSignedBits(value, 16)
        elif selector in (5, 6):
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
