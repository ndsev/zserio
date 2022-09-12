import unittest
import zserio

from testutils import getZserioApi

class BitmaskParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").bitmask_param_choice

    def testConstructor(self):
        selector = self.api.Selector.Values.BLACK
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        self.assertEqual(selector, bitmaskParamChoice.selector)

        bitmaskParamChoice = self.api.BitmaskParamChoice(selector, black_=42)
        self.assertEqual(selector, bitmaskParamChoice.selector)
        self.assertEqual(42, bitmaskParamChoice.black)

    def testFromReader(self):
        selector = self.api.Selector.Values.WHITE
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeBitmaskParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        bitmaskParamChoice = self.api.BitmaskParamChoice.from_reader(reader, selector)
        self.assertEqual(selector, bitmaskParamChoice.selector)
        self.assertEqual(value, bitmaskParamChoice.white)

    def testEq(self):
        bitmaskParamChoice1 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitmaskParamChoice2 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertTrue(bitmaskParamChoice1 == bitmaskParamChoice2)

        value = 99
        bitmaskParamChoice1.black = value
        self.assertFalse(bitmaskParamChoice1 == bitmaskParamChoice2)

        bitmaskParamChoice2.black = value
        self.assertTrue(bitmaskParamChoice1 == bitmaskParamChoice2)

        diffValue = value + 1
        bitmaskParamChoice2.black = diffValue
        self.assertFalse(bitmaskParamChoice1 == bitmaskParamChoice2)

    def testHash(self):
        bitmaskParamChoice1 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitmaskParamChoice2 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(hash(bitmaskParamChoice1), hash(bitmaskParamChoice2))

        value = 99
        bitmaskParamChoice1.black = value
        self.assertTrue(hash(bitmaskParamChoice1) != hash(bitmaskParamChoice2))

        bitmaskParamChoice2.black = value
        self.assertEqual(hash(bitmaskParamChoice1), hash(bitmaskParamChoice2))

        diffValue = value + 1
        bitmaskParamChoice2.black = diffValue
        self.assertTrue(hash(bitmaskParamChoice1) != hash(bitmaskParamChoice2))

        # use hardcoded values to check that the hash code is stable
        self.assertEqual(63110, hash(bitmaskParamChoice1))
        self.assertEqual(63111, hash(bitmaskParamChoice2))

    def testGetSelector(self):
        selector = self.api.Selector.Values.BLACK
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        self.assertEqual(selector, bitmaskParamChoice.selector)

    def testGetSetBlack(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        value = 99
        bitmaskParamChoice.black = value
        self.assertEqual(value, bitmaskParamChoice.black)

    def testGetSetWhite(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        value = 234
        bitmaskParamChoice.white = value
        self.assertEqual(value, bitmaskParamChoice.white)

    def testGetSetBlackAndWhite(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        value = 65535
        bitmaskParamChoice.black_and_white = value
        self.assertEqual(value, bitmaskParamChoice.black_and_white)

    def testChoiceTag(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(self.api.BitmaskParamChoice.CHOICE_BLACK_AND_WHITE, bitmaskParamChoice.choice_tag)

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(self.api.BitmaskParamChoice.CHOICE_BLACK, bitmaskParamChoice.choice_tag)

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(self.api.BitmaskParamChoice.CHOICE_WHITE, bitmaskParamChoice.choice_tag)

    def testBitSizeOf(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(8, bitmaskParamChoice.bitsizeof())

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(8, bitmaskParamChoice.bitsizeof())

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(16, bitmaskParamChoice.bitsizeof())

    def testInitializeOffsets(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitPosition = 1
        self.assertEqual(9, bitmaskParamChoice.initialize_offsets(bitPosition))

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(9, bitmaskParamChoice.initialize_offsets(bitPosition))

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(17, bitmaskParamChoice.initialize_offsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.Values.BLACK
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        byteValue = 99
        bitmaskParamChoice.black = byteValue
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBitmaskParamChoice.read(reader)
        self.assertEqual(byteValue, readBitmaskParamChoice.black)
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

        selector = self.api.Selector.Values.WHITE
        shortValue = 234
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector, white_=shortValue)
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBitmaskParamChoice.read(reader)
        self.assertEqual(shortValue, readBitmaskParamChoice.white)
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

        selector = self.api.Selector.Values.BLACK_AND_WHITE
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        intValue = 65535
        bitmaskParamChoice.black_and_white = intValue
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBitmaskParamChoice.read(reader)
        self.assertEqual(intValue, readBitmaskParamChoice.black_and_white)
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

    def _writeBitmaskParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.Values.BLACK:
            writer.write_bits(value, 8)
        elif selector == self.api.Selector.Values.WHITE:
            writer.write_bits(value, 8)
        elif selector == self.api.Selector.Values.BLACK_AND_WHITE:
            writer.write_bits(value, 16)
        else:
            self.fail(f"Invalid selector: {selector}")
