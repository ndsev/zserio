import zserio

import Functions


class StructureValueTest(Functions.TestCase):
    def testCustomVarIntValue42(self):
        self._checkCustomVarInt(42)

    def testCustomVarIntValue253(self):
        self._checkCustomVarInt(self.MAX_ONE_BYTE_VALUE)

    def testCustomVarIntValue255(self):
        self._checkCustomVarInt(self.TWO_BYTES_INDICATOR)

    def testCustomVarIntValue254(self):
        self._checkCustomVarInt(self.FOUR_BYTES_INDICATOR)

    def testCustomVarIntValue1000(self):
        self._checkCustomVarInt(1000)

    def testCustomVarIntValue87654(self):
        self._checkCustomVarInt(87654)

    def _writeCustomVarIntToStream(self, writer, value):
        if value <= self.MAX_ONE_BYTE_VALUE:
            writer.write_bits(value, 8)
        elif value <= 0xFFFF:
            writer.write_bits(self.TWO_BYTES_INDICATOR, 8)
            writer.write_bits(value, 16)
        else:
            writer.write_bits(self.FOUR_BYTES_INDICATOR, 8)
            writer.write_bits(value, 32)

    def _createCustomVarInt(self, value):
        customVarInt = self.api.CustomVarInt()
        if value <= self.MAX_ONE_BYTE_VALUE:
            customVarInt.val1 = value
        elif value <= 0xFFFF:
            customVarInt.val1 = self.TWO_BYTES_INDICATOR
            customVarInt.val2 = value
        else:
            customVarInt.val1 = self.FOUR_BYTES_INDICATOR
            customVarInt.val3 = value

        return customVarInt

    def _checkCustomVarInt(self, value):
        customVarInt = self._createCustomVarInt(value)
        readValue = customVarInt.get_value()
        self.assertEqual(value, readValue)

        writer = zserio.BitStreamWriter()
        customVarInt.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeCustomVarIntToStream(expectedWriter, value)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readcustomVarInt = self.api.CustomVarInt.from_reader(reader)
        self.assertEqual(customVarInt, readcustomVarInt)

    MAX_ONE_BYTE_VALUE = 253
    TWO_BYTES_INDICATOR = 255
    FOUR_BYTES_INDICATOR = 254
