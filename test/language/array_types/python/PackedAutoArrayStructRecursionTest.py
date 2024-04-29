import os
import zserio

import ArrayTypes

from testutils import getApiDir


class PackedAutoArrayStructRecursionTest(ArrayTypes.TestCase):
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

    def testWriteReadLength1(self):
        self._checkWriteRead(self.AUTO_ARRAY_LENGTH1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.AUTO_ARRAY_LENGTH2)

    def testWriteReadLength3(self):
        self._checkWriteRead(self.AUTO_ARRAY_LENGTH3)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.AUTO_ARRAY_LENGTH1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.AUTO_ARRAY_LENGTH2)

    def testWriteReadFileLength3(self):
        self._checkWriteReadFile(self.AUTO_ARRAY_LENGTH3)

    def _checkBitSizeOf(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitPosition = 2
        autoArrayRecursionBitSize = PackedAutoArrayStructRecursionTest._calcPackedAutoArrayRecursionBitSize(
            numElements
        )
        self.assertEqual(autoArrayRecursionBitSize, packedAutoArrayRecursion.bitsizeof(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + (
            PackedAutoArrayStructRecursionTest._calcPackedAutoArrayRecursionBitSize(numElements)
        )
        self.assertEqual(expectedEndBitPosition, packedAutoArrayRecursion.initialize_offsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        self._writePackedAutoArrayRecursionToStream(writer, numElements)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        packedAutoArrayRecursion = self.api.PackedAutoArrayRecursion.from_reader(reader)
        self._checkPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements)

    def _checkWriteRead(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitBuffer = zserio.serialize(packedAutoArrayRecursion)

        self.assertEqual(packedAutoArrayRecursion.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(packedAutoArrayRecursion.initialize_offsets(), bitBuffer.bitsize)

        readPackedAutoArrayRecursion = zserio.deserialize(self.api.PackedAutoArrayRecursion, bitBuffer)
        self._checkPackedAutoArrayRecursion(readPackedAutoArrayRecursion, numElements)

    def _checkWriteReadFile(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(packedAutoArrayRecursion, filename)

        readPackedAutoArrayRecursion = zserio.deserialize_from_file(self.api.PackedAutoArrayRecursion, filename)
        self._checkPackedAutoArrayRecursion(readPackedAutoArrayRecursion, numElements)

    def _createPackedAutoArrayRecursion(self, numElements):
        autoArray = []
        for i in range(1, numElements + 1):
            element = self.api.PackedAutoArrayRecursion(i, [])
            autoArray.append(element)

        return self.api.PackedAutoArrayRecursion(0, autoArray)

    def _checkPackedAutoArrayRecursion(self, packedAutoArrayRecursion, numElements):
        self.assertEqual(0, packedAutoArrayRecursion.id)
        autoArray = packedAutoArrayRecursion.packed_auto_array_recursion
        self.assertEqual(numElements, len(autoArray))
        for i in range(1, numElements + 1):
            element = autoArray[i - 1]
            self.assertEqual(i, element.id)
            self.assertEqual(0, len(element.packed_auto_array_recursion))

    @staticmethod
    def _writePackedAutoArrayRecursionToStream(writer, numElements):
        writer.write_bits(0, 8)
        writer.write_varsize(numElements)
        writer.write_bool(True)
        maxBitNumber = 1
        writer.write_bits(maxBitNumber, 6)
        writer.write_bits(1, 8)
        writer.write_varsize(0)
        for _ in range(numElements - 1):
            writer.write_signed_bits(1, maxBitNumber + 1)
            writer.write_varsize(0)

    @staticmethod
    def _calcPackedAutoArrayRecursionBitSize(numElements):
        bitSize = 8  # id
        bitSize += 8  # varsize (length of auto array)
        bitSize += 1  # packing descriptor: is_packed
        if numElements > 1:
            bitSize += 6  # packing descriptor: max_bit_number
        bitSize += 8 + 8  # first element
        bitSize += (numElements - 1) * (8 + 2)  # all deltas

        return bitSize

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_auto_array_struct_recursion_")

    AUTO_ARRAY_LENGTH1 = 1
    AUTO_ARRAY_LENGTH2 = 5
    AUTO_ARRAY_LENGTH3 = 10
