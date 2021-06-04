import unittest
import zserio

from testutils import getZserioApi

class PackedFixedArrayUInt8Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").packed_fixed_array_uint8

    def testBitSizeOf(self):
        uint8Array = self._createAutoArray()
        packedFixedArray = self.api.PackedFixedArray(uint8Array)
        bitPosition = 2
        fixedArrayBitSize = PackedFixedArrayUInt8Test._calcPackedFixedArrayBitSize()
        self.assertEqual(fixedArrayBitSize, packedFixedArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        uint8Array = self._createAutoArray()
        packedFixedArray = self.api.PackedFixedArray(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + PackedFixedArrayUInt8Test._calcPackedFixedArrayBitSize()
        self.assertEqual(expectedEndBitPosition, packedFixedArray.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writePackedFixedArrayToStream(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        packedFixedArray = self.api.PackedFixedArray.from_reader(reader)

        uint8Array = packedFixedArray.uint8_array
        self._checkPackedFixedArray(uint8Array)

    def testWrite(self):
        uint8Array = self._createAutoArray()
        packedFixedArray = self.api.PackedFixedArray(uint8Array)
        bitBuffer = zserio.serialize(packedFixedArray)
        readPackedFixedArray = zserio.deserialize(self.api.PackedFixedArray, bitBuffer)
        readUint8Array = readPackedFixedArray.uint8_array
        self._checkPackedFixedArray(readUint8Array)

    def testWriteWrongArray(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH + 1))
        packedFixedArray = self.api.PackedFixedArray(uint8_array_=uint8Array)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            packedFixedArray.write(writer)

    def _createAutoArray(self):
        return [self.PACKED_ARRAY_ELEMENT] * self.FIXED_ARRAY_LENGTH

    def _checkPackedFixedArray(self, uint8Array):
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(uint8Array))
        for i in range(self.FIXED_ARRAY_LENGTH):
            self.assertEqual(self.PACKED_ARRAY_ELEMENT, uint8Array[i])

    def _writePackedFixedArrayToStream(self, writer):
        writer.write_bool(True)
        writer.write_bits(self.PACKED_ARRAY_MAX_BIT_NUMBER, 6)
        writer.write_bits(self.PACKED_ARRAY_ELEMENT, 8)

    @staticmethod
    def _calcPackedFixedArrayBitSize():
        bitSize = 1  # packing descriptor: is_packed
        bitSize += 6 # packing descriptor: max_bit_number
        bitSize += 8 # first element

        return bitSize

    FIXED_ARRAY_LENGTH = 5
    PACKED_ARRAY_MAX_BIT_NUMBER = 0
    PACKED_ARRAY_ELEMENT = 100
