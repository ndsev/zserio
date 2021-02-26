import unittest
import zserio

from testutils import getZserioApi

class FullBitmaskParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").full_bitmask_param_choice

    def testConstructor(self):
        selector = self.api.Selector.Values.BLACK
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        self.assertEqual(selector, fullBitmaskParamChoice.selector)

        selector = self.api.Selector.Values.WHITE
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector, white_=0xff)
        self.assertEqual(selector, fullBitmaskParamChoice.selector)
        self.assertEqual(0xff, fullBitmaskParamChoice.white)

    def testFromReader(self):
        selector = self.api.Selector.Values.WHITE
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeFullBitmaskParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, fullBitmaskParamChoice.selector)
        self.assertEqual(value, fullBitmaskParamChoice.white)

    def testEq(self):
        fullBitmaskParamChoice1 = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        fullBitmaskParamChoice2 = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertTrue(fullBitmaskParamChoice1 == fullBitmaskParamChoice2)

        value = 99
        fullBitmaskParamChoice1.black = value
        self.assertFalse(fullBitmaskParamChoice1 == fullBitmaskParamChoice2)

        fullBitmaskParamChoice2.black = value
        self.assertTrue(fullBitmaskParamChoice1 == fullBitmaskParamChoice2)

        diffValue = value + 1
        fullBitmaskParamChoice2.black = diffValue
        self.assertFalse(fullBitmaskParamChoice1 == fullBitmaskParamChoice2)

    def testHash(self):
        fullBitmaskParamChoice1 = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        fullBitmaskParamChoice2 = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(hash(fullBitmaskParamChoice1), hash(fullBitmaskParamChoice2))

        value = 99
        fullBitmaskParamChoice1.black = value
        self.assertTrue(hash(fullBitmaskParamChoice1) != hash(fullBitmaskParamChoice2))

        fullBitmaskParamChoice2.black = value
        self.assertEqual(hash(fullBitmaskParamChoice1), hash(fullBitmaskParamChoice2))

        diffValue = value + 1
        fullBitmaskParamChoice2.black = diffValue
        self.assertTrue(hash(fullBitmaskParamChoice1) != hash(fullBitmaskParamChoice2))

    def testGetSelector(self):
        selector = self.api.Selector.Values.BLACK
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        self.assertEqual(selector, fullBitmaskParamChoice.selector)

    def testGetSetBlack(self):
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        value = 99
        fullBitmaskParamChoice.black = value
        self.assertEqual(value, fullBitmaskParamChoice.black)

    def testGetSetWhite(self):
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.WHITE)
        value = 234
        fullBitmaskParamChoice.white = value
        self.assertEqual(value, fullBitmaskParamChoice.white)

    def testGetSetBlackAndWhite(self):
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        value = 65535
        fullBitmaskParamChoice.black_and_white = value
        self.assertEqual(value, fullBitmaskParamChoice.black_and_white)

    def testBitSizeOf(self):
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        self.assertEqual(8, fullBitmaskParamChoice.bitSizeOf())

        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(8, fullBitmaskParamChoice.bitSizeOf())

        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(16, fullBitmaskParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK)
        bitPosition = 1
        self.assertEqual(9, fullBitmaskParamChoice.initializeOffsets(bitPosition))

        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.WHITE)
        self.assertEqual(9, fullBitmaskParamChoice.initializeOffsets(bitPosition))

        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(self.api.Selector.Values.BLACK_AND_WHITE)
        self.assertEqual(17, fullBitmaskParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.Values.BLACK
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        byteValue = 99
        fullBitmaskParamChoice.black = byteValue
        writer = zserio.BitStreamWriter()
        fullBitmaskParamChoice.write(writer)
        readFullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullBitmaskParamChoice.read(reader)
        self.assertEqual(byteValue, readFullBitmaskParamChoice.black)
        self.assertEqual(fullBitmaskParamChoice, readFullBitmaskParamChoice)

        selector = self.api.Selector.Values.WHITE
        shortValue = 234
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector, white_=shortValue)
        writer = zserio.BitStreamWriter()
        fullBitmaskParamChoice.write(writer)
        readFullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullBitmaskParamChoice.read(reader)
        self.assertEqual(shortValue, readFullBitmaskParamChoice.white)
        self.assertEqual(fullBitmaskParamChoice, readFullBitmaskParamChoice)

        selector = self.api.Selector.Values.BLACK_AND_WHITE
        fullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        intValue = 65535
        fullBitmaskParamChoice.black_and_white = intValue
        writer = zserio.BitStreamWriter()
        fullBitmaskParamChoice.write(writer)
        readFullBitmaskParamChoice = self.api.FullBitmaskParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullBitmaskParamChoice.read(reader)
        self.assertEqual(intValue, readFullBitmaskParamChoice.black_and_white)
        self.assertEqual(fullBitmaskParamChoice, readFullBitmaskParamChoice)

    def _writeFullBitmaskParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.Values.BLACK:
            writer.writeBits(value, 8)
        elif selector == self.api.Selector.Values.WHITE:
            writer.writeBits(value, 8)
        elif selector == self.api.Selector.Values.BLACK_AND_WHITE:
            writer.writeBits(value, 16)
        else:
            self.fail("Invalid selector: %d" % selector)
