import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class PackedIndexedOffsetArrayHolderTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").packed_indexed_offset_array_holder

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.NUM_ELEMENTS1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.NUM_ELEMENTS2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.NUM_ELEMENTS3)

    def testWriteReadLength1(self):
        self._checkWriteRead(self.NUM_ELEMENTS1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.NUM_ELEMENTS2)

    def testWriteReadLength3(self):
        self._checkWriteRead(self.NUM_ELEMENTS3)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.NUM_ELEMENTS1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.NUM_ELEMENTS2)

    def testWriteReadFileLength3(self):
        self._checkWriteReadFile(self.NUM_ELEMENTS3)

    def _checkBitSizeOf(self, numElements):
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(numElements)
        unpackedBitsizeOf = PackedIndexedOffsetArrayHolderTest._calcAutoIndexedOffsetArrayBitSize(numElements)
        packedBitsizeOf = autoIndexedOffsetArray.bitsizeof()
        minCompressionRatio = 0.82
        self.assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, "Unpacked array has " +
                        str(unpackedBitsizeOf) + " bits, packed array has " + str(packedBitsizeOf) + " bits, " +
                        "compression ratio is " + str(packedBitsizeOf / unpackedBitsizeOf * 100) + "%!")

    def _checkWriteRead(self, numElements):
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(numElements)
        writer = zserio.BitStreamWriter()
        autoIndexedOffsetArray.initialize_offsets(writer.bitposition)
        autoIndexedOffsetArray.write(writer)
        self.assertEqual(autoIndexedOffsetArray.bitsizeof(), writer.bitposition)
        self.assertEqual(autoIndexedOffsetArray.initialize_offsets(0), writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readAutoIndexedOffsetArray = self.api.AutoIndexedOffsetArray.from_reader(reader)
        self.assertEqual(autoIndexedOffsetArray, readAutoIndexedOffsetArray)

    def _checkWriteReadFile(self, numElements):
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(numElements)
        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(autoIndexedOffsetArray, filename)

        readAutoIndexedOffsetArray = zserio.deserialize_from_file(self.api.AutoIndexedOffsetArray, filename)
        self.assertEqual(autoIndexedOffsetArray, readAutoIndexedOffsetArray)

    def _createAutoIndexedOffsetArray(self, numElements):
        offsetHolders = []
        for i in range(numElements + 1):
            offsetHolders.append(self.api.OffsetHolder(0, [0], i))

        data1 = []
        for i in range(numElements):
            data1.append(i)

        data2 = []
        for i in range(numElements):
            data2.append(i * 2)

        return self.api.AutoIndexedOffsetArray(self.api.OffsetArray(offsetHolders), data1, data2)

    @staticmethod
    def _calcAutoIndexedOffsetArrayBitSize(numElements):
        bitSize = 0
        for _ in range(numElements + 1):
            bitSize += 32 # offset[i]
            bitSize += 32 # offsets[1]
            bitSize += 32 # value[i]
        for _ in range(numElements):
            bitSize += 32 # data1[i]
        for _ in range(numElements):
            bitSize += 32 # data2[i]

        return bitSize

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_indexed_offset_array_holder_")

    NUM_ELEMENTS1 = 50
    NUM_ELEMENTS2 = 100
    NUM_ELEMENTS3 = 1000
