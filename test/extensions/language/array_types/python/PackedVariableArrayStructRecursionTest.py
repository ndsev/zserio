import os
import zserio

import ArrayTypes

from testutils import getApiDir


class PackedVariableArrayStructRecursionTest(ArrayTypes.TestCase):
    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH3)

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
        packedVariableArray = self._createPackedVariableArray(numElements)
        unpackedBitsizeOf = PackedVariableArrayStructRecursionTest._calcUnpackedVariableArrayBitSize(
            numElements
        )
        packedBitsizeOf = packedVariableArray.bitsizeof()
        minCompressionRatio = 0.9
        self.assertTrue(
            unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf,
            "Unpacked array has "
            + str(unpackedBitsizeOf)
            + " bits, packed array has "
            + str(packedBitsizeOf)
            + " bits, "
            + "compression ratio is "
            + str(packedBitsizeOf / unpackedBitsizeOf * 100)
            + "%!",
        )

    def _checkWriteRead(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        bitBuffer = zserio.serialize(packedVariableArray)

        self.assertEqual(packedVariableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(packedVariableArray.initialize_offsets(), bitBuffer.bitsize)

        readPackedVariableArray = zserio.deserialize(self.api.PackedVariableArray, bitBuffer)
        self.assertEqual(packedVariableArray, readPackedVariableArray)

    def _checkWriteReadFile(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(packedVariableArray, filename)

        readPackedVariableArray = zserio.deserialize_from_file(self.api.PackedVariableArray, filename)
        self.assertEqual(packedVariableArray, readPackedVariableArray)

    def _createPackedVariableArray(self, numElements):
        byteCount = 1
        blocks = []
        for _ in range(numElements):
            blocks.append(self._createBlock(byteCount, False))

        return self.api.PackedVariableArray(byteCount, numElements, blocks)

    def _createBlock(self, byteCount, isLast):
        dataBytes = list(range(byteCount))
        if isLast:
            return self.api.Block(byteCount, dataBytes, 0)

        blockTerminator = byteCount + 1
        block = self._createBlock(blockTerminator, blockTerminator > 5)

        return self.api.Block(byteCount, dataBytes, blockTerminator, block)

    @staticmethod
    def _calcUnpackedVariableArrayBitSize(numElements):
        bitSize = 8  # byteCount
        bitSize += 8  # numElements
        byteCount = 1
        for _ in range(numElements):
            bitSize += PackedVariableArrayStructRecursionTest._calcUnpackedBlockBitSize(byteCount, False)

        return bitSize

    @staticmethod
    def _calcUnpackedBlockBitSize(byteCount, isLast):
        bitSize = 8 * byteCount  # dataBytes[byteCount]
        bitSize += 8  # blockTerminator
        if not isLast:
            blockTerminator = byteCount + 1
            bitSize += PackedVariableArrayStructRecursionTest._calcUnpackedBlockBitSize(
                blockTerminator, blockTerminator > 5
            )

        return bitSize

    BLOB_NAME_BASE = os.path.join(
        getApiDir(os.path.dirname(__file__)), "packed_variable_array_struct_recursion_"
    )

    VARIABLE_ARRAY_LENGTH1 = 100
    VARIABLE_ARRAY_LENGTH2 = 500
    VARIABLE_ARRAY_LENGTH3 = 1000
