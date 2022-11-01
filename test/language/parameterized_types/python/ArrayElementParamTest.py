import unittest
import zserio

from testutils import getZserioApi

class ArrayElementParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").array_element_param

    def testWrite(self):
        database = self._createDatabase()
        writer = zserio.BitStreamWriter()
        database.initialize_offsets(writer.bitposition)
        database.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkDatabaseInStream(reader, database)

        reader.bitposition = 0
        readDatabase = self.api.Database.from_reader(reader)
        self.assertEqual(database, readDatabase)

    def _createDatabase(self):
        blockHeaderList = []
        blockList = []
        for i in range(self.NUM_BLOCKS):
            numItems = i + 1
            blockHeader = self.api.BlockHeader(numItems, 0)
            blockHeaderList.append(blockHeader)
            itemList = []
            for j in range(numItems):
                itemList.append(j * 2)

            blockList.append(self.api.Block(blockHeader, itemList))

        return self.api.Database(self.NUM_BLOCKS, blockHeaderList, blockList)

    def _checkDatabaseInStream(self, reader, database):
        numBlocks = database.num_blocks
        self.assertEqual(numBlocks, reader.read_bits(16))

        headers = database.headers
        expectedOffset = self.FIRST_BYTE_OFFSET
        for i in range(numBlocks):
            numItems = reader.read_bits(16)
            self.assertEqual(headers[i].num_items, numItems)
            self.assertEqual(expectedOffset, reader.read_bits(32))
            expectedOffset += 8 * numItems

        blocks = database.blocks
        for i in range(numBlocks):
            numItems = headers[i].num_items
            items = blocks[i].items
            for j in range(numItems):
                self.assertEqual(items[j], reader.read_signed_bits(64))

    NUM_BLOCKS = 3
    FIRST_BYTE_OFFSET = 2 + NUM_BLOCKS * (2 + 4)
