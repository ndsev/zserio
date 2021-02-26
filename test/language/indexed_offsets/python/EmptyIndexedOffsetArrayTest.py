import unittest
import zserio

from testutils import getZserioApi

class EmptyIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").empty_indexed_offset_array

    def testBitSizeOf(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        self.assertEqual(self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE, emptyIndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 1
        self.assertEqual(self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         emptyIndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 0
        self.assertEqual(self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         emptyIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        bitPosition = 9
        self.assertEqual(self.EMPTY_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition,
                         emptyIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeEmptyIndexedOffsetArrayToStream(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        emptyIndexedOffsetArray = self.api.EmptyIndexedOffsetArray()
        emptyIndexedOffsetArray.read(reader)
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)

    def testWrite(self):
        emptyIndexedOffsetArray = self._createEmptyIndexedOffsetArray()
        writer = zserio.BitStreamWriter()
        emptyIndexedOffsetArray.write(writer)
        self._checkEmptyIndexedOffsetArray(emptyIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readEmptyIndexedOffsetArray = self.api.EmptyIndexedOffsetArray.fromReader(reader)
        self._checkEmptyIndexedOffsetArray(readEmptyIndexedOffsetArray)
        self.assertTrue(emptyIndexedOffsetArray == readEmptyIndexedOffsetArray)

    def _writeEmptyIndexedOffsetArrayToStream(self, writer):
        writer.writeBits(self.SPACER_VALUE, 1)
        writer.writeBits(self.FIELD_VALUE, 6)

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
