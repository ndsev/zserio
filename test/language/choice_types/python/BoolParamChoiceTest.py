import unittest
import zserio

from testutils import getZserioApi

class BoolParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").bool_param_choice

    def testConstructor(self):
        selector = True
        boolParamChoice = self.api.BoolParamChoice(selector)
        self.assertEqual(selector, boolParamChoice.getSelector())

        selector = False
        boolParamChoice = self.api.BoolParamChoice(selector, grey_=1234)
        self.assertEqual(selector, boolParamChoice.getSelector())
        self.assertEqual(1234, boolParamChoice.getGrey())

    def testFromReader(self):
        selector = False
        value = 234
        writer = zserio.BitStreamWriter()
        BoolParamChoiceTest._writeBoolParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.getByteArray())
        boolParamChoice = self.api.BoolParamChoice.fromReader(reader, selector)
        self.assertEqual(selector, boolParamChoice.getSelector())
        self.assertEqual(value, boolParamChoice.getGrey())

    def testEq(self):
        enumParamChoice1 = self.api.BoolParamChoice(True)
        enumParamChoice2 = self.api.BoolParamChoice(True)
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
        enumParamChoice1 = self.api.BoolParamChoice(True)
        enumParamChoice2 = self.api.BoolParamChoice(True)
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
        selector = True
        boolParamChoice = self.api.BoolParamChoice(selector)
        self.assertEqual(selector, boolParamChoice.getSelector())

    def testGetSetBlack(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        value = 99
        boolParamChoice.setBlack(value)
        self.assertEqual(value, boolParamChoice.getBlack())

    def testGetSetGrey(self):
        boolParamChoice = self.api.BoolParamChoice(False)
        value = 234
        boolParamChoice.setGrey(value)
        self.assertEqual(value, boolParamChoice.getGrey())

    def testBitSizeOf(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        self.assertEqual(8, boolParamChoice.bitSizeOf())

        boolParamChoice = self.api.BoolParamChoice(False)
        self.assertEqual(16, boolParamChoice.bitSizeOf())

    def testInitializeOffsets(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        bitPosition = 1
        self.assertEqual(9, boolParamChoice.initializeOffsets(bitPosition))

        boolParamChoice = self.api.BoolParamChoice(False)
        self.assertEqual(17, boolParamChoice.initializeOffsets(bitPosition))

    def testReadWrite(self):
        selector = True
        boolParamChoice = self.api.BoolParamChoice(selector)
        byteValue = 99
        boolParamChoice.setBlack(byteValue)
        writer = zserio.BitStreamWriter()
        boolParamChoice.write(writer)
        readBoolParamChoice = self.api.BoolParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBoolParamChoice.read(reader)
        self.assertEqual(byteValue, readBoolParamChoice.getBlack())
        self.assertEqual(boolParamChoice, readBoolParamChoice)

        selector = False
        shortValue = 234
        boolParamChoice = self.api.BoolParamChoice(selector, grey_=shortValue)
        writer = zserio.BitStreamWriter()
        boolParamChoice.write(writer)
        readBoolParamChoice = self.api.BoolParamChoice(selector)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBoolParamChoice.read(reader)
        self.assertEqual(shortValue, readBoolParamChoice.getGrey())
        self.assertEqual(boolParamChoice, readBoolParamChoice)

    @staticmethod
    def _writeBoolParamChoiceToStream(writer, selector, value):
        if selector:
            writer.writeSignedBits(value, 8)
        else:
            writer.writeSignedBits(value, 16)
