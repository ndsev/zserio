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
        self.assertEqual(selector, boolParamChoice.selector)

        selector = False
        boolParamChoice = self.api.BoolParamChoice(selector, grey_=1234)
        self.assertEqual(selector, boolParamChoice.selector)
        self.assertEqual(1234, boolParamChoice.grey)

    def testFromReader(self):
        selector = False
        value = 234
        writer = zserio.BitStreamWriter()
        BoolParamChoiceTest._writeBoolParamChoiceToStream(writer, selector, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        boolParamChoice = self.api.BoolParamChoice.from_reader(reader, selector)
        self.assertEqual(selector, boolParamChoice.selector)
        self.assertEqual(value, boolParamChoice.grey)

    def testEq(self):
        boolParamChoice1 = self.api.BoolParamChoice(True)
        boolParamChoice2 = self.api.BoolParamChoice(True)
        self.assertTrue(boolParamChoice1 == boolParamChoice2)

        value = 99
        boolParamChoice1.black = value
        self.assertFalse(boolParamChoice1 == boolParamChoice2)

        boolParamChoice2.black = value
        self.assertTrue(boolParamChoice1 == boolParamChoice2)

        diffValue = value + 1
        boolParamChoice2.black = diffValue
        self.assertFalse(boolParamChoice1 == boolParamChoice2)

    def testHash(self):
        boolParamChoice1 = self.api.BoolParamChoice(True)
        boolParamChoice2 = self.api.BoolParamChoice(True)
        self.assertEqual(hash(boolParamChoice1), hash(boolParamChoice2))

        value = 99
        boolParamChoice1.black = value
        self.assertTrue(hash(boolParamChoice1) != hash(boolParamChoice2))

        boolParamChoice2.black = value
        self.assertEqual(hash(boolParamChoice1), hash(boolParamChoice2))

        diffValue = value + 1
        boolParamChoice2.black = diffValue
        self.assertTrue(hash(boolParamChoice1) != hash(boolParamChoice2))

        # use hardcoded values to check that the hash code is stable
        self.assertEqual(31623, hash(boolParamChoice1))
        self.assertEqual(31624, hash(boolParamChoice2))

    def testGetSelector(self):
        selector = True
        boolParamChoice = self.api.BoolParamChoice(selector)
        self.assertEqual(selector, boolParamChoice.selector)

    def testGetSetBlack(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        value = 99
        boolParamChoice.black = value
        self.assertEqual(value, boolParamChoice.black)

    def testGetSetGrey(self):
        boolParamChoice = self.api.BoolParamChoice(False)
        value = 234
        boolParamChoice.grey = value
        self.assertEqual(value, boolParamChoice.grey)

    def testChoiceTag(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        self.assertEqual(self.api.BoolParamChoice.CHOICE_BLACK, boolParamChoice.choice_tag)

        boolParamChoice = self.api.BoolParamChoice(False)
        self.assertEqual(self.api.BoolParamChoice.CHOICE_GREY, boolParamChoice.choice_tag)

    def testBitSizeOf(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        self.assertEqual(8, boolParamChoice.bitsizeof())

        boolParamChoice = self.api.BoolParamChoice(False)
        self.assertEqual(16, boolParamChoice.bitsizeof())

    def testInitializeOffsets(self):
        boolParamChoice = self.api.BoolParamChoice(True)
        bitPosition = 1
        self.assertEqual(9, boolParamChoice.initialize_offsets(bitPosition))

        boolParamChoice = self.api.BoolParamChoice(False)
        self.assertEqual(17, boolParamChoice.initialize_offsets(bitPosition))

    def testReadWrite(self):
        selector = True
        boolParamChoice = self.api.BoolParamChoice(selector)
        byteValue = 99
        boolParamChoice.black = byteValue
        writer = zserio.BitStreamWriter()
        boolParamChoice.write(writer)
        readBoolParamChoice = self.api.BoolParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBoolParamChoice.read(reader)
        self.assertEqual(byteValue, readBoolParamChoice.black)
        self.assertEqual(boolParamChoice, readBoolParamChoice)

        selector = False
        shortValue = 234
        boolParamChoice = self.api.BoolParamChoice(selector, grey_=shortValue)
        writer = zserio.BitStreamWriter()
        boolParamChoice.write(writer)
        readBoolParamChoice = self.api.BoolParamChoice(selector)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBoolParamChoice.read(reader)
        self.assertEqual(shortValue, readBoolParamChoice.grey)
        self.assertEqual(boolParamChoice, readBoolParamChoice)

    @staticmethod
    def _writeBoolParamChoiceToStream(writer, selector, value):
        if selector:
            writer.write_signed_bits(value, 8)
        else:
            writer.write_signed_bits(value, 16)
