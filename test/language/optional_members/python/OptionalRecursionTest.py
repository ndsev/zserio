import unittest
import zserio

from testutils import getZserioApi

class OptionalRecursionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_recursion

    def testConstructor(self):
        emptyBlock = self.api.Block(0)
        self.assertEqual(0, emptyBlock.byte_count)
        self.assertEqual(0, len(emptyBlock.data_bytes))
        self.assertEqual(0, emptyBlock.block_terminator)
        self.assertEqual(None, emptyBlock.next_data)

        emptyBlock = self.api.Block(0, [], 0, None)
        self.assertEqual(0, emptyBlock.byte_count)
        self.assertEqual(0, len(emptyBlock.data_bytes))
        self.assertEqual(0, emptyBlock.block_terminator)
        self.assertEqual(None, emptyBlock.next_data)

        emptyBlock = self.api.Block(byteCount_=0, dataBytes_=[], blockTerminator_=0, nextData_=None)
        self.assertEqual(0, emptyBlock.byte_count)
        self.assertEqual(0, len(emptyBlock.data_bytes))
        self.assertEqual(0, emptyBlock.block_terminator)
        self.assertEqual(None, emptyBlock.next_data)

    def testEq(self):
        emptyBlock1 = self._createEmptyBlock()
        emptyBlock2 = self._createEmptyBlock()
        self.assertTrue(emptyBlock1 == emptyBlock2)

        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertFalse(block1 == emptyBlock1)

        block2 = self._createBlock(self.BLOCK1_DATA)
        self.assertTrue(block2 == block1)

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertFalse(block12 == block1)

    def testHash(self):
        emptyBlock1 = self._createEmptyBlock()
        emptyBlock2 = self._createEmptyBlock()
        self.assertEqual(hash(emptyBlock1), hash(emptyBlock2))

        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertTrue(hash(block1) != hash(emptyBlock1))

        block2 = self._createBlock(self.BLOCK1_DATA)
        self.assertEqual(hash(block2), hash(block1))

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertTrue(hash(block12) != hash(block1))

    def testHasNextData(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertFalse(block1.isNextDataUsed())

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertTrue(block12.isNextDataUsed())

    def testBitSizeOf(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertEqual(OptionalRecursionTest._getBlockBitSize(self.BLOCK1_DATA), block1.bitSizeOf())

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertEqual(OptionalRecursionTest._getBlock12BitSize(self.BLOCK1_DATA, self.BLOCK2_DATA),
                         block12.bitSizeOf())

    def testInitializeOffsets(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        bitPosition = 1
        self.assertEqual(bitPosition + OptionalRecursionTest._getBlockBitSize(self.BLOCK1_DATA),
                         block1.initializeOffsets(bitPosition))

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertEqual(bitPosition +
                         OptionalRecursionTest._getBlock12BitSize(self.BLOCK1_DATA, self.BLOCK2_DATA),
                         block12.initializeOffsets(bitPosition))

    def testWriteBlock1(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        writer = zserio.BitStreamWriter()
        block1.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkBlockInStream(reader, self.BLOCK1_DATA)

        reader.bitposition = 0
        readBlock1 = self.api.Block.fromReader(reader, len(self.BLOCK1_DATA))
        self.assertEqual(block1, readBlock1)

    def testWriteBlock12(self):
        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        writer = zserio.BitStreamWriter()
        block12.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkBlock12InStream(reader, self.BLOCK1_DATA, self.BLOCK2_DATA)

        reader.bitposition = 0
        readBlock12 = self.api.Block.fromReader(reader, len(self.BLOCK1_DATA))
        self.assertEqual(block12, readBlock12)

    def _createEmptyBlock(self):
        return self.api.Block(0, None, 0, None)

    def _createBlock(self, blockData):
        return self.api.Block(len(blockData), blockData, 0, None)

    def _createBlock12(self, block1Data, block2Data):
        block2 = self._createBlock(block2Data)

        return self.api.Block(len(block1Data), block1Data, len(block2Data), block2)

    @staticmethod
    def _getBlockBitSize(blockData):
        return 8 * len(blockData) + 8

    @staticmethod
    def _getBlock12BitSize(block1Data, block2Data):
        return (OptionalRecursionTest._getBlockBitSize(block1Data) +
                OptionalRecursionTest._getBlockBitSize(block2Data))

    def _checkBlockInStream(self, reader, blockData):
        for element in blockData:
            self.assertEqual(element, reader.read_bits(8))
        self.assertEqual(0, reader.read_bits(8))

    def _checkBlock12InStream(self, reader, block1Data, block2Data):
        for element in block1Data:
            self.assertEqual(element, reader.read_bits(8))
        self.assertEqual(len(block2Data), reader.read_bits(8))

        self._checkBlockInStream(reader, block2Data)

    BLOCK1_DATA = [1, 2, 3, 4, 5, 6]
    BLOCK2_DATA = [10, 9, 8, 7]
