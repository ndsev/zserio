import unittest
import zserio

from testutils import getZserioApi

class PackedArrayElementParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").packed_array_element_param

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.NUM_BLOCKS1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.NUM_BLOCKS2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.NUM_BLOCKS3)

    def testWriteReadfLength1(self):
        self._checkWriteRead(self.NUM_BLOCKS1)

    def testWriteReadfLength2(self):
        self._checkWriteRead(self.NUM_BLOCKS2)

    def testWriteReadfLength3(self):
        self._checkWriteRead(self.NUM_BLOCKS3)

    def _checkBitSizeOf(self, numBlocks):
        database = self._createDatabase(numBlocks)
        unpackedBitsizeOf = PackedArrayElementParamTest._calcUnpackedDatabaseBitSize(numBlocks)
        packedBitsizeOf = database.bitsizeof()
        minCompressionRatio = 0.12
        self.assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, "Unpacked array has " +
                        str(unpackedBitsizeOf) + " bits, packed array has " + str(packedBitsizeOf) + " bits, " +
                        "compression ratio is " + str(packedBitsizeOf / unpackedBitsizeOf * 100) + "%!")

    def _checkWriteRead(self, numBlocks):
        database = self._createDatabase(numBlocks)
        writer = zserio.BitStreamWriter()
        database.write(writer)
        self.assertEqual(database.bitsizeof(), writer.bitposition)
        self.assertEqual(database.initialize_offsets(0), writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readDatabase = self.api.Database.from_reader(reader)
        self.assertEqual(database, readDatabase)

    def _createDatabase(self, numBlocks):
        blockHeaderList = []
        blockList = []
        for i in range(numBlocks):
            numItems = i + 1
            blockHeader = self.api.BlockHeader(numItems, 0)
            blockHeaderList.append(blockHeader)
            itemList = []
            for j in range(numItems):
                itemList.append(j * 2)

            blockList.append(self.api.Block(blockHeader, numItems, itemList))

        return self.api.Database(numBlocks, blockHeaderList, blockList)

    @staticmethod
    def _calcUnpackedDatabaseBitSize(numBlocks):
        bitSize = 16  # numBlocks
        bitSize += numBlocks * (16 + 32) # headers
        for i in range(numBlocks):
            numItems = i + 1
            bitSize += 64 + numItems * 64 # blocks[i]

        return bitSize

    NUM_BLOCKS1 = 50
    NUM_BLOCKS2 = 100
    NUM_BLOCKS3 = 1000
