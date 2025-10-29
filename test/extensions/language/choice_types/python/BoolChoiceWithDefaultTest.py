import ChoiceTypes
from compoundutils import writeReadTest, readTest, hashTest
from zserio.bitbuffer import BitBuffer
from zserio.bitwriter import BitStreamWriter


class BoolChoiceWithDefaultTest(ChoiceTypes.TestCase):
    def testConstructor(self):
        selector = True
        data = self.api.BoolChoiceWithDefault(selector)
        self.assertEqual(selector, data.selector)

    def testEq(self):
        data1 = self.api.BoolChoiceWithDefault(True)
        data2 = self.api.BoolChoiceWithDefault(True)
        self.assertTrue(data1 == data2)

        data1.field = 99
        self.assertFalse(data1 == data2)

    def testBitSizeOf(self):
        data = self.api.BoolChoiceWithDefault(True)
        self.assertEqual(8, data.bitsizeof())

    def testHash(self):
        data1 = self.api.BoolChoiceWithDefault(False)
        data1.field = 99
        data2 = self.api.BoolChoiceWithDefault(False)
        data2.field = 99
        hashTest(data1, 31586, data2)

    def testWriteRead(self):
        selector = True
        data = self.api.BoolChoiceWithDefault(selector)
        data.field = 230
        writeReadTest(self.api.BoolChoiceWithDefault, data, selector)

    def testRead(self):
        selector = False
        value = 234
        data = self.api.BoolChoiceWithDefault(selector)
        data.field = value
        buffer = self.writeBoolParamChoiceToBitBuffer(selector, value)
        readTest(buffer, self.api.BoolChoiceWithDefault, data, selector)

    @staticmethod
    def writeBoolParamChoiceToBitBuffer(_selector, value) -> BitBuffer:
        writer = BitStreamWriter()
        writer.write_bits(value, 8)
        return BitBuffer(writer.byte_array, writer.bitposition)
