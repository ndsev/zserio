import unittest
import zserio

from testutils import getZserioApi

class StructureValueTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_value

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
            writer.writeBits(value, 8)
        elif value <= 0xFFFF:
            writer.writeBits(self.TWO_BYTES_INDICATOR, 8)
            writer.writeBits(value, 16)
        else:
            writer.writeBits(self.FOUR_BYTES_INDICATOR, 8)
            writer.writeBits(value, 32)

    def _createCustomVarInt(self, value):
        customVarInt = self.api.CustomVarInt()
        if value <= self.MAX_ONE_BYTE_VALUE:
            customVarInt.setVal1(value)
        elif value <= 0xFFFF:
            customVarInt.setVal1(self.TWO_BYTES_INDICATOR)
            customVarInt.setVal2(value)
        else:
            customVarInt.setVal1(self.FOUR_BYTES_INDICATOR)
            customVarInt.setVal3(value)

        return customVarInt

    def _checkCustomVarInt(self, value):
        customVarInt = self._createCustomVarInt(value)
        readValue = customVarInt.funcGetValue()
        self.assertEqual(value, readValue)

        writer = zserio.BitStreamWriter()
        customVarInt.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeCustomVarIntToStream(expectedWriter, value)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())
        self.assertTrue(expectedWriter.getBitPosition() == writer.getBitPosition())

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readcustomVarInt = self.api.CustomVarInt.fromReader(reader)
        self.assertEqual(customVarInt, readcustomVarInt)

    MAX_ONE_BYTE_VALUE = 253
    TWO_BYTES_INDICATOR = 255
    FOUR_BYTES_INDICATOR = 254
