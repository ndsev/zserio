import unittest
import zserio

from testutils import getZserioApi

class PackedAutoArrayUInt8Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").packed_auto_array_uint8

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH3)

    def testInitializeOffsetsLength1(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH1)

    def testInitializeOffsetsLength2(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH2)

    def testInitializeOffsetsLength3(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH3)

    def testReadLength1(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH1)

    def testReadLength2(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH2)

    def testReadLength3(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH3)

    def testWriteLength1(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH1)

    def testWriteLength2(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH2)

    def testWriteLength3(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH3)

    def _checkBitSizeOf(self, numElements):
        uint8Array = self._createAutoArray(numElements)
        packedAutoArray = self.api.PackedAutoArray(uint8Array)
        bitPosition = 2
        packedAutoArrayBitSize = self._calcPackedAutoArrayBitSize(numElements)
        self.assertEqual(packedAutoArrayBitSize, packedAutoArray.bitsizeof(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        uint8Array = self._createAutoArray(numElements)
        packedAutoArray = self.api.PackedAutoArray(uint8_array_=uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self._calcPackedAutoArrayBitSize(numElements)
        self.assertEqual(expectedEndBitPosition, packedAutoArray.initialize_offsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        self._writePackedAutoArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        packedAutoArray = self.api.PackedAutoArray.from_reader(reader)

        uint8Array = packedAutoArray.uint8_array
        self._checkPackedAutoArray(uint8Array, numElements)

    def _checkWrite(self, numElements):
        uint8Array = self._createAutoArray(numElements)
        packedAutoArray = self.api.PackedAutoArray(uint8Array)
        bitBuffer = zserio.serialize(packedAutoArray)
        readPackedAutoArray = zserio.deserialize(self.api.PackedAutoArray, bitBuffer)
        readUint8Array = readPackedAutoArray.uint8_array
        self._checkPackedAutoArray(readUint8Array, numElements)

    def _checkPackedAutoArray(self, uint8Array, expectedNumElements):
        self.assertEqual(expectedNumElements, len(uint8Array))
        value = self.PACKED_ARRAY_ELEMENT0
        self.assertEqual(value, uint8Array[0])
        value += self.PACKED_ARRAY_DELTA
        for i in range(1, expectedNumElements - 1):
            value += self.PACKED_ARRAY_DELTA
            self.assertEqual(value, uint8Array[i])

    def _createAutoArray(self, numElements):
        value = self.PACKED_ARRAY_ELEMENT0
        uint8Array = [value]
        value += self.PACKED_ARRAY_DELTA
        for _ in range(numElements - 1):
            value += self.PACKED_ARRAY_DELTA
            uint8Array.append(value)

        return uint8Array

    def _writePackedAutoArrayToStream(self, writer, numElements):
        writer.write_varsize(numElements)
        writer.write_bool(True)
        writer.write_bits(self.PACKED_ARRAY_MAX_BIT_NUMBER, 6)
        value = self.PACKED_ARRAY_ELEMENT0
        writer.write_bits(value, 8)
        if numElements > 1:
            writer.write_signed_bits(self.PACKED_ARRAY_DELTA * 2, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)
            for _ in range(numElements - 2):
                writer.write_signed_bits(self.PACKED_ARRAY_DELTA, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)

    def _calcPackedAutoArrayBitSize(self, numElements):
        bitSize = 8  # auto array size: varsize
        bitSize += 1 # packing descriptor: is_packed
        bitSize += 6 # packing descriptor: max_bit_number
        bitSize += 8 # first element
        bitSize += (numElements - 1) * (self.PACKED_ARRAY_MAX_BIT_NUMBER + 1) # all deltas

        return bitSize

    AUTO_ARRAY_LENGTH1 = 1
    AUTO_ARRAY_LENGTH2 = 5
    AUTO_ARRAY_LENGTH3 = 10

    PACKED_ARRAY_ELEMENT0 = 255
    PACKED_ARRAY_DELTA = -2
    PACKED_ARRAY_MAX_BIT_NUMBER = 3
