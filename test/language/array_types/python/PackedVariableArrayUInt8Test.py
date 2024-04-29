import os
import zserio

import ArrayTypes

from testutils import getApiDir


class PackedVariableArrayUInt8Test(ArrayTypes.TestCase):
    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH3)

    def testInitializeOffsetsLength1(self):
        self._checkInitializeOffsets(self.VARIABLE_ARRAY_LENGTH1)

    def testInitializeOffsetsLength2(self):
        self._checkInitializeOffsets(self.VARIABLE_ARRAY_LENGTH2)

    def testInitializeOffsetsLength3(self):
        self._checkInitializeOffsets(self.VARIABLE_ARRAY_LENGTH3)

    def testReadLength1(self):
        self._checkRead(self.VARIABLE_ARRAY_LENGTH1)

    def testReadLength2(self):
        self._checkRead(self.VARIABLE_ARRAY_LENGTH2)

    def testReadLength3(self):
        self._checkRead(self.VARIABLE_ARRAY_LENGTH3)

    def testWriteReadLength1(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH2)

    def testWriteReadLength3(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH3)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH2)

    def testWriteReadFileLength3(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH3)

    def _checkBitSizeOf(self, numElements):
        uint8Array = self._createVariableArray(numElements)
        packedVariableArray = self.api.PackedVariableArray(numElements, uint8Array)
        bitPosition = 2
        packedVariableArrayBitSize = self._calcPackedVariableArrayBitSize(numElements)
        self.assertEqual(packedVariableArrayBitSize, packedVariableArray.bitsizeof(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        uint8Array = self._createVariableArray(numElements)
        packedVariableArray = self.api.PackedVariableArray(numElements, uint8_array_=uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self._calcPackedVariableArrayBitSize(numElements)
        self.assertEqual(expectedEndBitPosition, packedVariableArray.initialize_offsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        self._writePackedVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        packedVariableArray = self.api.PackedVariableArray.from_reader(reader)

        uint8Array = packedVariableArray.uint8_array
        self._checkPackedVariableArray(uint8Array, numElements)

    def _checkWriteRead(self, numElements):
        uint8Array = self._createVariableArray(numElements)
        packedVariableArray = self.api.PackedVariableArray(numElements, uint8Array)
        bitBuffer = zserio.serialize(packedVariableArray)

        self.assertEqual(packedVariableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(packedVariableArray.initialize_offsets(), bitBuffer.bitsize)

        readPackedVariableArray = zserio.deserialize(self.api.PackedVariableArray, bitBuffer)
        readUint8Array = readPackedVariableArray.uint8_array
        self._checkPackedVariableArray(readUint8Array, numElements)

    def _checkWriteReadFile(self, numElements):
        uint8Array = self._createVariableArray(numElements)
        packedVariableArray = self.api.PackedVariableArray(numElements, uint8Array)
        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(packedVariableArray, filename)

        readPackedVariableArray = zserio.deserialize_from_file(self.api.PackedVariableArray, filename)
        readUint8Array = readPackedVariableArray.uint8_array
        self._checkPackedVariableArray(readUint8Array, numElements)

    def _checkPackedVariableArray(self, uint8Array, expectedNumElements):
        self.assertEqual(expectedNumElements, len(uint8Array))
        value = self.PACKED_ARRAY_ELEMENT0
        self.assertEqual(value, uint8Array[0])
        value += self.PACKED_ARRAY_DELTA
        for i in range(1, expectedNumElements - 1):
            value += self.PACKED_ARRAY_DELTA
            self.assertEqual(value, uint8Array[i])

    def _createVariableArray(self, numElements):
        value = self.PACKED_ARRAY_ELEMENT0
        uint8Array = [value]
        value += self.PACKED_ARRAY_DELTA
        for _ in range(numElements - 1):
            value += self.PACKED_ARRAY_DELTA
            uint8Array.append(value)

        return uint8Array

    def _writePackedVariableArrayToStream(self, writer, numElements):
        writer.write_varsize(numElements)
        writer.write_bool(True)
        writer.write_bits(self.PACKED_ARRAY_MAX_BIT_NUMBER, 6)
        value = self.PACKED_ARRAY_ELEMENT0
        writer.write_bits(value, 8)
        if numElements > 1:
            writer.write_signed_bits(self.PACKED_ARRAY_DELTA * 2, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)
            for _ in range(numElements - 2):
                writer.write_signed_bits(self.PACKED_ARRAY_DELTA, self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)

    def _calcPackedVariableArrayBitSize(self, numElements):
        bitSize = 8  # array size: numElements
        bitSize += 1  # packing descriptor: is_packed
        if numElements > 1:
            bitSize += 6  # packing descriptor: max_bit_number
        bitSize += 8  # first element
        bitSize += (numElements - 1) * (self.PACKED_ARRAY_MAX_BIT_NUMBER + 1)  # all deltas

        return bitSize

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_variable_array_uint8_")

    VARIABLE_ARRAY_LENGTH1 = 1
    VARIABLE_ARRAY_LENGTH2 = 5
    VARIABLE_ARRAY_LENGTH3 = 10

    PACKED_ARRAY_ELEMENT0 = 255
    PACKED_ARRAY_DELTA = -2
    PACKED_ARRAY_MAX_BIT_NUMBER = 3
