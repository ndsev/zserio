import zserio

import OptionalMembers


class OptionalRecursionTest(OptionalMembers.TestCase):
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

        emptyBlock = self.api.Block(byte_count_=0, data_bytes_=[], block_terminator_=0, next_data_=None)
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

        block12_1 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertTrue(hash(block12_1) != hash(block1))

        block12_2 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertEqual(hash(block12_1), hash(block12_2))

        block12_1.block_terminator = 0
        self.assertNotEqual(hash(block12_1), hash(block12_2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(6240113, block12_1.__hash__())
        self.assertEqual(1846174533, block12_2.__hash__())

    def testIsNextDataSetAndUsed(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertFalse(block1.is_next_data_set())
        self.assertFalse(block1.is_next_data_used())

        block1.block_terminator = 1  # used but not set
        self.assertFalse(block1.is_next_data_set())
        self.assertTrue(block1.is_next_data_used())

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertTrue(block12.is_next_data_set())
        self.assertTrue(block12.is_next_data_used())

        block12.block_terminator = 0  # set but not used
        self.assertTrue(block12.is_next_data_set())
        self.assertFalse(block12.is_next_data_used())

    def testResetNextData(self):
        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertTrue(block12.is_next_data_set())
        self.assertTrue(block12.is_next_data_used())

        block12.reset_next_data()  # used but not set
        self.assertFalse(block12.is_next_data_set())
        self.assertTrue(block12.is_next_data_used())
        self.assertEqual(None, block12.next_data)

    def testBitSizeOf(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        self.assertEqual(OptionalRecursionTest._getBlockBitSize(self.BLOCK1_DATA), block1.bitsizeof())

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertEqual(
            OptionalRecursionTest._getBlock12BitSize(self.BLOCK1_DATA, self.BLOCK2_DATA), block12.bitsizeof()
        )

    def testInitializeOffsets(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        bitPosition = 1
        self.assertEqual(
            bitPosition + OptionalRecursionTest._getBlockBitSize(self.BLOCK1_DATA),
            block1.initialize_offsets(bitPosition),
        )

        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        self.assertEqual(
            bitPosition + OptionalRecursionTest._getBlock12BitSize(self.BLOCK1_DATA, self.BLOCK2_DATA),
            block12.initialize_offsets(bitPosition),
        )

    def testWriteBlock1(self):
        block1 = self._createBlock(self.BLOCK1_DATA)
        writer = zserio.BitStreamWriter()
        block1.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkBlockInStream(reader, self.BLOCK1_DATA)

        reader.bitposition = 0
        readBlock1 = self.api.Block.from_reader(reader, len(self.BLOCK1_DATA))
        self.assertEqual(block1, readBlock1)

    def testWriteBlock12(self):
        block12 = self._createBlock12(self.BLOCK1_DATA, self.BLOCK2_DATA)
        writer = zserio.BitStreamWriter()
        block12.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkBlock12InStream(reader, self.BLOCK1_DATA, self.BLOCK2_DATA)

        reader.bitposition = 0
        readBlock12 = self.api.Block.from_reader(reader, len(self.BLOCK1_DATA))
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
        return OptionalRecursionTest._getBlockBitSize(block1Data) + OptionalRecursionTest._getBlockBitSize(
            block2Data
        )

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
