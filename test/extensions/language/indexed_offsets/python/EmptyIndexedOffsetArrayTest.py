import zserio

import IndexedOffsets


class EmptyIndexedOffsetArrayTest(IndexedOffsets.TestCase):
    def testBitSizeOf(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        self.assertEqual(self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.bitsizeof())

    def testBitSizeOfWithPosition(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 1
        self.assertEqual(
            self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.bitsizeof(bitPosition)
        )

    def testInitializeOffsets(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 0
        self.assertEqual(
            self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.initialize_offsets(bitPosition)
        )
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 9
        self.assertEqual(
            self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition,
            emptyIndexedOffsetArray.initialize_offsets(bitPosition),
        )
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeEmptyIndexedOffsetArrayToStream(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        emptyIndexedOffsetArray = self.api.EmptyIndexedOffsetArray()
        emptyIndexedOffsetArray.read(reader)
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testWrite(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        writer = zserio.BitStreamWriter()
        emptyIndexedOffsetArray.write(writer)
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyIndexedOffsetArray = self.api.EmptyIndexedOffsetArray.from_reader(reader)
        self._checkEmptyIndexedOffsetArray(readEmptyIndexedOffsetArray)
        self.assertTrue(emptyIndexedOffsetArray == readEmptyIndexedOffsetArray)

    def _writeEmptyIndexedOffsetArrayToStream(self, writer):
        writer.write_bits(self.SPACER_VALUE, 1)
        writer.write_bits(self.FIELD_VALUE, 6)

    def _checkEmptyIndexedOffsetArray(self, emptyIndexedOffsetArray):
        offsets = emptyIndexedOffsetArray.offsets
        self.assertEqual(self.NUM_ELEMENTS, len(offsets))

        self.assertEqual(self.SPACER_VALUE, emptyIndexedOffsetArray.spacer)
        self.assertEqual(self.FIELD_VALUE, emptyIndexedOffsetArray.field)

        data = emptyIndexedOffsetArray.data
        self.assertEqual(self.NUM_ELEMENTS, len(data))

    def _createEmptyIndexedOffsetArray(self):
        return self.api.EmptyIndexedOffsetArray([], self.SPACER_VALUE, [], self.FIELD_VALUE)

    NUM_ELEMENTS = 0

    SPACER_VALUE = 1
    FIELD_VALUE = 63

    EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE = 1 + 6
