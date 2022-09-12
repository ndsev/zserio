import unittest
import zserio

from testutils import getZserioApi

class UInt16ParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").uint16_param_choice

    def testConstructor(self):
        selector = self.VARIANT_A_SELECTOR
        uint16ParamChoice = self.api.UInt16ParamChoice(selector)
        self.assertEqual(selector, uint16ParamChoice.selector)

        selector = self.VARIANT_B_SELECTOR1
        uint16ParamChoice = self.api.UInt16ParamChoice(selector, b_=1234)
        self.assertEqual(selector, uint16ParamChoice.selector)
        self.assertEqual(1234, uint16ParamChoice.b)

    def testFromReader(self):
        selector = self.VARIANT_B_SELECTOR1
        value = 234
        writer = zserio.BitStreamWriter()
        UInt16ParamChoiceTest._writeUInt16ParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        uint16ParamChoice = self.api.UInt16ParamChoice.from_reader(reader, selector)
        self.assertEqual(selector, uint16ParamChoice.selector)
        self.assertEqual(value, uint16ParamChoice.b)

    def testEq(self):
        uint16ParamChoice1 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        uint16ParamChoice2 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertTrue(uint16ParamChoice1 == uint16ParamChoice2)

        value = 99
        uint16ParamChoice1.a = value
        self.assertFalse(uint16ParamChoice1 == uint16ParamChoice2)

        uint16ParamChoice2.a = value
        self.assertTrue(uint16ParamChoice1 == uint16ParamChoice2)

        diffValue = value + 1
        uint16ParamChoice2.a = diffValue
        self.assertFalse(uint16ParamChoice1 == uint16ParamChoice2)

    def testHash(self):
        uint16ParamChoice1 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        uint16ParamChoice2 = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(hash(uint16ParamChoice1), hash(uint16ParamChoice2))

        value = 99
        uint16ParamChoice1.a = value
        self.assertTrue(hash(uint16ParamChoice1) != hash(uint16ParamChoice2))

        uint16ParamChoice2.a = value
        self.assertEqual(hash(uint16ParamChoice1), hash(uint16ParamChoice2))

        diffValue = value + 1
        uint16ParamChoice2.a = diffValue
        self.assertTrue(hash(uint16ParamChoice1) != hash(uint16ParamChoice2))

        # use hardcoded values to check that the hash code is stable
        self.assertEqual(31623, hash(uint16ParamChoice1))
        self.assertEqual(31624, hash(uint16ParamChoice2))

    def testGetSelector(self):
        selector = self.EMPTY_SELECTOR2
        uint16ParamChoice = self.api.UInt16ParamChoice(selector)
        self.assertEqual(selector, uint16ParamChoice.selector)

    def testGetSetA(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        value = 99
        uint16ParamChoice.a = value
        self.assertEqual(value, uint16ParamChoice.a)

    def testGetSetB(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR3)
        value = 234
        uint16ParamChoice.b = value
        self.assertEqual(value, uint16ParamChoice.b)

    def testGetSetC(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        value = 65535
        uint16ParamChoice.c = value
        self.assertEqual(value, uint16ParamChoice.c)

    def testChoiceTag(self):
        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(uint16ParamChoice.CHOICE_A, uint16ParamChoice.choice_tag)

        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR1)
        self.assertEqual(uint16ParamChoice.CHOICE_B, uint16ParamChoice.choice_tag)

        uint16ParamChoice = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(uint16ParamChoice.CHOICE_C, uint16ParamChoice.choice_tag)

        uint16ParamChoice = self.api.UInt16ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(uint16ParamChoice.UNDEFINED_CHOICE, uint16ParamChoice.choice_tag)

    def testBitSizeOf(self):
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint16ParamChoiceA.a = byteValueA
        self.assertEqual(8, uint16ParamChoiceA.bitsizeof())

        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR2)
        shortValueB = 234
        uint16ParamChoiceB.b = shortValueB
        self.assertEqual(16, uint16ParamChoiceB.bitsizeof())

        uint16ParamChoiceEmpty = self.api.UInt16ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(0, uint16ParamChoiceEmpty.bitsizeof())

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint16ParamChoiceC.c = intValueC
        self.assertEqual(32, uint16ParamChoiceC.bitsizeof())

    def testInitializeOffsets(self):
        bitPosition = 1
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(9, uint16ParamChoiceA.initialize_offsets(bitPosition))

        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR2)
        self.assertEqual(17, uint16ParamChoiceB.initialize_offsets(bitPosition))

        uint16ParamChoiceEmpty = self.api.UInt16ParamChoice(self.EMPTY_SELECTOR1)
        self.assertEqual(1, uint16ParamChoiceEmpty.initialize_offsets(bitPosition))

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        self.assertEqual(33, uint16ParamChoiceC.initialize_offsets(bitPosition))

    def testReadWrite(self):
        uint16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        uint16ParamChoiceA.a = byteValueA
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceA.write(writer)
        readUInt16ParamChoiceA = self.api.UInt16ParamChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUInt16ParamChoiceA.read(reader)
        self.assertEqual(byteValueA, readUInt16ParamChoiceA.a)
        self.assertEqual(uint16ParamChoiceA, readUInt16ParamChoiceA)

        shortValueB = 234
        uint16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR1, b_=shortValueB)
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceB.write(writer)
        readUInt16ParamChoiceB = self.api.UInt16ParamChoice(self.VARIANT_B_SELECTOR1)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUInt16ParamChoiceB.read(reader)
        self.assertEqual(shortValueB, readUInt16ParamChoiceB.b)
        self.assertEqual(uint16ParamChoiceB, readUInt16ParamChoiceB)

        uint16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        intValueC = 65535
        uint16ParamChoiceC.c = intValueC
        writer = zserio.BitStreamWriter()
        uint16ParamChoiceC.write(writer)
        readUInt16ParamChoiceC = self.api.UInt16ParamChoice(self.VARIANT_C_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUInt16ParamChoiceC.read(reader)
        self.assertEqual(intValueC, readUInt16ParamChoiceC.c)
        self.assertEqual(uint16ParamChoiceC, readUInt16ParamChoiceC)

    @staticmethod
    def _writeUInt16ParamChoiceToStream(writer, selector, value):
        if selector == 1:
            writer.write_signed_bits(value, 8)
        elif selector in (2, 3, 4):
            writer.write_signed_bits(value, 16)
        elif selector in (5, 6):
            pass
        else:
            writer.write_signed_bits(value, 32)

    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR1 = 2
    VARIANT_B_SELECTOR2 = 3
    VARIANT_B_SELECTOR3 = 4
    EMPTY_SELECTOR1 = 5
    EMPTY_SELECTOR2 = 6
    VARIANT_C_SELECTOR = 7
