import os
import zserio

import ParameterizedTypes
from testutils import getApiDir


class PackedArrayElementParamTest(ParameterizedTypes.TestCase):
    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.NUM_BLOCKS1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.NUM_BLOCKS2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.NUM_BLOCKS3)

    def testWriteReadLength1(self):
        self._checkWriteRead(self.NUM_BLOCKS1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.NUM_BLOCKS2)

    def testWriteReadLength3(self):
        self._checkWriteRead(self.NUM_BLOCKS3)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.NUM_BLOCKS1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.NUM_BLOCKS2)

    def testWriteReadFileLength3(self):
        self._checkWriteReadFile(self.NUM_BLOCKS3)

    def _checkBitSizeOf(self, numBlocks):
        database = self._createDatabase(numBlocks)
        unpackedBitsizeOf = PackedArrayElementParamTest._calcUnpackedDatabaseBitSize(numBlocks)
        packedBitsizeOf = database.bitsizeof()
        minCompressionRatio = 0.12
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

    def _checkWriteRead(self, numBlocks):
        database = self._createDatabase(numBlocks)
        writer = zserio.BitStreamWriter()
        database.initialize_offsets(writer.bitposition)
        database.write(writer)
        self.assertEqual(database.bitsizeof(), writer.bitposition)
        self.assertEqual(database.initialize_offsets(), writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readDatabase = self.api.Database.from_reader(reader)
        self.assertEqual(database, readDatabase)

    def _checkWriteReadFile(self, numBlocks):
        database = self._createDatabase(numBlocks)
        filename = self.BLOB_NAME_BASE + str(numBlocks) + ".blob"
        zserio.serialize_to_file(database, filename)

        readDatabase = zserio.deserialize_from_file(self.api.Database, filename)
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
        bitSize += numBlocks * (16 + 32)  # headers
        for i in range(numBlocks):
            numItems = i + 1
            bitSize += 64 + numItems * 64  # blocks[i]

        return bitSize

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_array_element_param_")
    NUM_BLOCKS1 = 50
    NUM_BLOCKS2 = 100
    NUM_BLOCKS3 = 1000
