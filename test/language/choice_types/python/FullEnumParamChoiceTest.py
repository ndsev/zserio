import unittest
import zserio

from testutils import getZserioApi

class FullEnumParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").full_enum_param_choice

    def testConstructor(self):
        selector = self.api.Selector.BLACK
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        self.assertEqual(selector, fullEnumParamChoice.selector)

        selector = self.api.Selector.WHITE
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector, white_=123)
        self.assertEqual(selector, fullEnumParamChoice.selector)
        self.assertEqual(123, fullEnumParamChoice.white)

    def testFromReader(self):
        selector = self.api.Selector.GREY
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeFullEnumParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        fullEnumParamChoice = self.api.FullEnumParamChoice.from_reader(reader, selector)
        self.assertEqual(selector, fullEnumParamChoice.selector)
        self.assertEqual(value, fullEnumParamChoice.grey)

    def testEq(self):
        enumParamChoice1 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
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
        enumParamChoice1 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
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
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        self.assertEqual(selector, fullEnumParamChoice.selector)

    def testGetSetBlack(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        value = 99
        fullEnumParamChoice.black = value
        self.assertEqual(value, fullEnumParamChoice.black)

    def testGetSetGrey(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        value = 234
        fullEnumParamChoice.grey = value
        self.assertEqual(value, fullEnumParamChoice.grey)

    def testGetSetWhite(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        value = 65535
        fullEnumParamChoice.white = value
        self.assertEqual(value, fullEnumParamChoice.white)

    def testBitSizeOf(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        self.assertEqual(8, fullEnumParamChoice.bitsizeof())

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(16, fullEnumParamChoice.bitsizeof())

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(32, fullEnumParamChoice.bitsizeof())

    def testInitializeOffsets(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        bitPosition = 1
        self.assertEqual(9, fullEnumParamChoice.initialize_offsets(bitPosition))

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(17, fullEnumParamChoice.initialize_offsets(bitPosition))

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(33, fullEnumParamChoice.initialize_offsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.BLACK
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        byteValue = 99
        fullEnumParamChoice.black = byteValue
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFullEnumParamChoice.read(reader)
        self.assertEqual(byteValue, readFullEnumParamChoice.black)
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

        selector = self.api.Selector.GREY
        shortValue = 234
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector, grey_=shortValue)
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFullEnumParamChoice.read(reader)
        self.assertEqual(shortValue, readFullEnumParamChoice.grey)
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

        selector = self.api.Selector.WHITE
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        intValue = 65535
        fullEnumParamChoice.white = intValue
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFullEnumParamChoice.read(reader)
        self.assertEqual(intValue, readFullEnumParamChoice.white)
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

    def _writeFullEnumParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.BLACK:
            writer.write_signed_bits(value, 8)
        elif selector == self.api.Selector.GREY:
            writer.write_signed_bits(value, 16)
        elif selector == self.api.Selector.WHITE:
            writer.write_signed_bits(value, 32)
        else:
            self.fail("Invalid selector: %d" % selector)
