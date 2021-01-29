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
        database.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkDatabaseInStream(reader, database)

        reader.setBitPosition(0)
        readDatabase = self.api.Database.fromReader(reader)
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
        numBlocks = database.getNumBlocks()
        self.assertEqual(numBlocks, reader.readBits(16))

        headers = database.getHeaders()
        expectedOffset = self.FIRST_BYTE_OFFSET
        for i in range(numBlocks):
            numItems = reader.readBits(16)
            self.assertEqual(headers[i].getNumItems(), numItems)
            self.assertEqual(expectedOffset, reader.readBits(32))
            expectedOffset += 8 * numItems

        blocks = database.getBlocks()
        for i in range(numBlocks):
            numItems = headers[i].getNumItems()
            items = blocks[i].getItems()
            for j in range(numItems):
                self.assertEqual(items[j], reader.readSignedBits(64))

    NUM_BLOCKS = 3
    FIRST_BYTE_OFFSET = 2 + NUM_BLOCKS * (2 + 4)
