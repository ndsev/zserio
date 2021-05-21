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
        fixedArrayBitSize = self._calcPackedFixedArrayBitSize()
        self.assertEqual(fixedArrayBitSize, packedFixedArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        uint8Array = self._createAutoArray()
        packedFixedArray = self.api.PackedFixedArray(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self._calcPackedFixedArrayBitSize()
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
        value = self.PACKED_ARRAY_ELEMENT0
        uint8Array = [value]
        value += self.PACKED_ARRAY_DELTA
        for _ in range(self.FIXED_ARRAY_LENGTH - 1):
            value += self.PACKED_ARRAY_DELTA
            uint8Array.append(value)

        return uint8Array

    def _checkPackedFixedArray(self, uint8Array):
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(uint8Array))
        value = self.PACKED_ARRAY_ELEMENT0
        self.assertEqual(value, uint8Array[0])
        value += self.PACKED_ARRAY_DELTA
        for i in range(1, self.FIXED_ARRAY_LENGTH - 1):
            value += self.PACKED_ARRAY_DELTA
            self.assertEqual(value, uint8Array[i])

    def _writePackedFixedArrayToStream(self, writer):
        writer.write_bool(True)
        writer.write_bits(self.PACKED_ARRAY_MAX_BIT_NUMBER, 6)
        value = self.PACKED_ARRAY_ELEMENT0
        writer.write_bits(value, 8)
        writer.write_signed_bits(self.PACKED_ARRAY_DELTA * 2, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)
        for _ in range(self.FIXED_ARRAY_LENGTH - 2):
            writer.write_signed_bits(self.PACKED_ARRAY_DELTA, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)

    def _calcPackedFixedArrayBitSize(self):
        bitSize = 1  # packing descriptor: is_packed
        bitSize += 6 # packing descriptor: max_bit_number
        bitSize += 8 # first element
        bitSize += (self.FIXED_ARRAY_LENGTH - 1) * (self.PACKED_ARRAY_MAX_BIT_NUMBER + 1) # all deltas

        return bitSize

    FIXED_ARRAY_LENGTH = 5

    PACKED_ARRAY_ELEMENT0 = 1
    PACKED_ARRAY_DELTA = 2
    PACKED_ARRAY_MAX_BIT_NUMBER = 3
