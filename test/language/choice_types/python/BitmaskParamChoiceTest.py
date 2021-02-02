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
        self.assertEqual(selector, bitmaskParamChoice.getSelector())

        bitmaskParamChoice = self.api.BitmaskParamChoice(selector, black_=42)
        self.assertEqual(selector, bitmaskParamChoice.getSelector())
        self.assertEqual(42, bitmaskParamChoice.getBlack())

    def testFromReader(self):
        selector = self.api.Selector.Values.WHITE
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeBitmaskParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray())
        bitmaskParamChoice = self.api.BitmaskParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, bitmaskParamChoice.getSelector())
        self.assertEqual(value, bitmaskParamChoice.getWhite())

    def testEq(self):
        bitmaskParamChoice1 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitmaskParamChoice2 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertTrue(bitmaskParamChoice1 == bitmaskParamChoice2)

        value = 99
        bitmaskParamChoice1.setBlack(value)
        self.assertFalse(bitmaskParamChoice1 == bitmaskParamChoice2)

        bitmaskParamChoice2.setBlack(value)
        self.assertTrue(bitmaskParamChoice1 == bitmaskParamChoice2)

        diffValue = value + 1
        bitmaskParamChoice2.setBlack(diffValue)
        self.assertFalse(bitmaskParamChoice1 == bitmaskParamChoice2)

    def testHash(self):
        bitmaskParamChoice1 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitmaskParamChoice2 = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(hash(bitmaskParamChoice1), hash(bitmaskParamChoice2))

        value = 99
        bitmaskParamChoice1.setBlack(value)
        self.assertTrue(hash(bitmaskParamChoice1) != hash(bitmaskParamChoice2))

        bitmaskParamChoice2.setBlack(value)
        self.assertEqual(hash(bitmaskParamChoice1), hash(bitmaskParamChoice2))

        diffValue = value + 1
        bitmaskParamChoice2.setBlack(diffValue)
        self.assertTrue(hash(bitmaskParamChoice1) != hash(bitmaskParamChoice2))

    def testGetSelector(self):
        selector = self.api.Selector.Values.BLACK
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        self.assertEqual(selector, bitmaskParamChoice.getSelector())

    def testGetSetBlack(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        value = 99
        bitmaskParamChoice.setBlack(value)
        self.assertEqual(value, bitmaskParamChoice.getBlack())

    def testGetSetWhite(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        value = 234
        bitmaskParamChoice.setWhite(value)
        self.assertEqual(value, bitmaskParamChoice.getWhite())

    def testGetSetBlackAndWhite(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        value = 65535
        bitmaskParamChoice.setBlackAndWhite(value)
        self.assertEqual(value, bitmaskParamChoice.getBlackAndWhite())

    def testBitSizeOf(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(8, bitmaskParamChoice.bitSizeOf())

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(8, bitmaskParamChoice.bitSizeOf())

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(16, bitmaskParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitPosition = 1
        self.assertEqual(9, bitmaskParamChoice.initializeOffsets(bitPosition))

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(9, bitmaskParamChoice.initializeOffsets(bitPosition))

        bitmaskParamChoice = self.api.BitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(17, bitmaskParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.Values.BLACK
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        byteValue = 99
        bitmaskParamChoice.setBlack(byteValue)
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBitmaskParamChoice.read(reader)
        self.assertEqual(byteValue, readBitmaskParamChoice.getBlack())
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

        selector = self.api.Selector.Values.WHITE
        shortValue = 234
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector, white_=shortValue)
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBitmaskParamChoice.read(reader)
        self.assertEqual(shortValue, readBitmaskParamChoice.getWhite())
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

        selector = self.api.Selector.Values.BLACK_AND_WHITE
        bitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        intValue = 65535
        bitmaskParamChoice.setBlackAndWhite(intValue)
        writer = zserio.BitStreamWriter()
        bitmaskParamChoice.write(writer)
        readBitmaskParamChoice = self.api.BitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBitmaskParamChoice.read(reader)
        self.assertEqual(intValue, readBitmaskParamChoice.getBlackAndWhite())
        self.assertEqual(bitmaskParamChoice, readBitmaskParamChoice)

    def _writeBitmaskParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.Values.BLACK:
            writer.writeBits(value, 8)
        elif selector == self.api.Selector.Values.WHITE:
            writer.writeBits(value, 8)
        elif selector == self.api.Selector.Values.BLACK_AND_WHITE:
            writer.writeBits(value, 16)
        else:
            self.fail("Invalid selector: %d" % selector)
