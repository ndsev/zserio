import unittest
import zserio

from testutils import getZserioApi

class EnumParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").enum_param_choice

    def testConstructor(self):
        selector = self.api.Selector.BLACK
        enumParamChoice = self.api.EnumParamChoice(selector)
        self.assertEqual(selector, enumParamChoice.selector)

        selector = self.api.Selector.GREY
        enumParamChoice = self.api.EnumParamChoice(selector, grey_=1234)
        self.assertEqual(selector, enumParamChoice.selector)
        self.assertEqual(1234, enumParamChoice.grey)

    def testFromReader(self):
        selector = self.api.Selector.GREY
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeEnumParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        enumParamChoice = self.api.EnumParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, enumParamChoice.selector)
        self.assertEqual(value, enumParamChoice.grey)

    def testEq(self):
        enumParamChoice1 = self.api.EnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.EnumParamChoice(self.api.Selector.BLACK)
        self.assertTrue(enumParamChoice1 == enumParamChoice2)

        value = 99
        enumParamChoice1.black = value
        self.assertFalse(enumParamChoice1 == enumParamChoice2)

        enumParamChoice2.black = value
        self.assertTrue(enumParamChoice1 == enumParamChoice2)

        diffValue = value + 1
        enumParamChoice2.black = diffValue
        self.assertFalse(enumParamChoice1 == enumParamChoice2)

    def testHash(self):
        enumParamChoice1 = self.api.EnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.EnumParamChoice(self.api.Selector.BLACK)
        self.assertEqual(hash(enumParamChoice1), hash(enumParamChoice2))

        value = 99
        enumParamChoice1.black = value
        self.assertTrue(hash(enumParamChoice1) != hash(enumParamChoice2))

        enumParamChoice2.black = value
        self.assertEqual(hash(enumParamChoice1), hash(enumParamChoice2))

        diffValue = value + 1
        enumParamChoice2.black = diffValue
        self.assertTrue(hash(enumParamChoice1) != hash(enumParamChoice2))

    def testGetSelector(self):
        selector = self.api.Selector.BLACK
        enumParamChoice = self.api.EnumParamChoice(selector)
        self.assertEqual(selector, enumParamChoice.selector)

    def testGetSetBlack(self):
        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.BLACK)
        value = 99
        enumParamChoice.black = value
        self.assertEqual(value, enumParamChoice.black)

    def testGetSetGrey(self):
        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.GREY)
        value = 234
        enumParamChoice.grey = value
        self.assertEqual(value, enumParamChoice.grey)

    def testGetSetWhite(self):
        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.WHITE)
        value = 65535
        enumParamChoice.white = value
        self.assertEqual(value, enumParamChoice.white)

    def testBitSizeOf(self):
        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.BLACK)
        self.assertEqual(8, enumParamChoice.bitSizeOf())

        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(16, enumParamChoice.bitSizeOf())

        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(32, enumParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.BLACK)
        bitPosition = 1
        self.assertEqual(9, enumParamChoice.initializeOffsets(bitPosition))

        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(17, enumParamChoice.initializeOffsets(bitPosition))

        enumParamChoice = self.api.EnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(33, enumParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.BLACK
        enumParamChoice = self.api.EnumParamChoice(selector)
        byteValue = 99
        enumParamChoice.black = byteValue
        writer = zserio.BitStreamWriter()
        enumParamChoice.write(writer)
        readEnumParamChoice = self.api.EnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEnumParamChoice.read(reader)
        self.assertEqual(byteValue, readEnumParamChoice.black)
        self.assertEqual(enumParamChoice, readEnumParamChoice)

        selector = self.api.Selector.GREY
        shortValue = 234
        enumParamChoice = self.api.EnumParamChoice(selector, grey_=shortValue)
        writer = zserio.BitStreamWriter()
        enumParamChoice.write(writer)
        readEnumParamChoice = self.api.EnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEnumParamChoice.read(reader)
        self.assertEqual(shortValue, readEnumParamChoice.grey)
        self.assertEqual(enumParamChoice, readEnumParamChoice)

        selector = self.api.Selector.WHITE
        enumParamChoice = self.api.EnumParamChoice(selector)
        intValue = 65535
        enumParamChoice.white = intValue
        writer = zserio.BitStreamWriter()
        enumParamChoice.write(writer)
        readEnumParamChoice = self.api.EnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEnumParamChoice.read(reader)
        self.assertEqual(intValue, readEnumParamChoice.white)
        self.assertEqual(enumParamChoice, readEnumParamChoice)

    def _writeEnumParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.BLACK:
            writer.write_signed_bits(value, 8)
        elif selector == self.api.Selector.GREY:
            writer.write_signed_bits(value, 16)
        elif selector == self.api.Selector.WHITE:
            writer.write_signed_bits(value, 32)
        else:
            self.fail("Invalid selector: %d" % selector)
