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
        self.assertEqual(selector, fullEnumParamChoice.getSelector())

        selector = self.api.Selector.WHITE
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector, white_=123)
        self.assertEqual(selector, fullEnumParamChoice.getSelector())
        self.assertEqual(123, fullEnumParamChoice.getWhite())

    def testFromReader(self):
        selector = self.api.Selector.GREY
        value = 234
        writer = zserio.BitStreamWriter()
        self._writeFullEnumParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        fullEnumParamChoice = self.api.FullEnumParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, fullEnumParamChoice.getSelector())
        self.assertEqual(value, fullEnumParamChoice.getGrey())

    def testEq(self):
        enumParamChoice1 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        self.assertTrue(enumParamChoice1 == enumParamChoice2)

        value = 99
        enumParamChoice1.setBlack(value)
        self.assertFalse(enumParamChoice1 == enumParamChoice2)

        enumParamChoice2.setBlack(value)
        self.assertTrue(enumParamChoice1 == enumParamChoice2)

        diffValue = value + 1
        enumParamChoice2.setBlack(diffValue)
        self.assertFalse(enumParamChoice1 == enumParamChoice2)

    def testHash(self):
        enumParamChoice1 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        enumParamChoice2 = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        self.assertEqual(hash(enumParamChoice1), hash(enumParamChoice2))

        value = 99
        enumParamChoice1.setBlack(value)
        self.assertTrue(hash(enumParamChoice1) != hash(enumParamChoice2))

        enumParamChoice2.setBlack(value)
        self.assertEqual(hash(enumParamChoice1), hash(enumParamChoice2))

        diffValue = value + 1
        enumParamChoice2.setBlack(diffValue)
        self.assertTrue(hash(enumParamChoice1) != hash(enumParamChoice2))

    def testGetSelector(self):
        selector = self.api.Selector.BLACK
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        self.assertEqual(selector, fullEnumParamChoice.getSelector())

    def testGetSetBlack(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        value = 99
        fullEnumParamChoice.setBlack(value)
        self.assertEqual(value, fullEnumParamChoice.getBlack())

    def testGetSetGrey(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        value = 234
        fullEnumParamChoice.setGrey(value)
        self.assertEqual(value, fullEnumParamChoice.getGrey())

    def testGetSetWhite(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        value = 65535
        fullEnumParamChoice.setWhite(value)
        self.assertEqual(value, fullEnumParamChoice.getWhite())

    def testBitSizeOf(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        self.assertEqual(8, fullEnumParamChoice.bitSizeOf())

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(16, fullEnumParamChoice.bitSizeOf())

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(32, fullEnumParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.BLACK)
        bitPosition = 1
        self.assertEqual(9, fullEnumParamChoice.initializeOffsets(bitPosition))

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.GREY)
        self.assertEqual(17, fullEnumParamChoice.initializeOffsets(bitPosition))

        fullEnumParamChoice = self.api.FullEnumParamChoice(self.api.Selector.WHITE)
        self.assertEqual(33, fullEnumParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        selector = self.api.Selector.BLACK
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        byteValue = 99
        fullEnumParamChoice.setBlack(byteValue)
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullEnumParamChoice.read(reader)
        self.assertEqual(byteValue, readFullEnumParamChoice.getBlack())
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

        selector = self.api.Selector.GREY
        shortValue = 234
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector, grey_=shortValue)
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullEnumParamChoice.read(reader)
        self.assertEqual(shortValue, readFullEnumParamChoice.getGrey())
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

        selector = self.api.Selector.WHITE
        fullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        intValue = 65535
        fullEnumParamChoice.setWhite(intValue)
        writer = zserio.BitStreamWriter()
        fullEnumParamChoice.write(writer)
        readFullEnumParamChoice = self.api.FullEnumParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readFullEnumParamChoice.read(reader)
        self.assertEqual(intValue, readFullEnumParamChoice.getWhite())
        self.assertEqual(fullEnumParamChoice, readFullEnumParamChoice)

    def _writeFullEnumParamChoiceToStream(self, writer, selector, value):
        if selector == self.api.Selector.BLACK:
            writer.writeSignedBits(value, 8)
        elif selector == self.api.Selector.GREY:
            writer.writeSignedBits(value, 16)
        elif selector == self.api.Selector.WHITE:
            writer.writeSignedBits(value, 32)
        else:
            self.fail("Invalid selector: %d" % selector)
