import unittest
import zserio

from testutils import getZserioApi

class Int14IndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").int14_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        self.assertEqual(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE, int14IndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         int14IndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         int14IndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkInt14IndexedOffsetArray(int14IndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         int14IndexedOffsetArray.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(int14IndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        Int14IndexedOffsetArrayTest._writeInt14IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        int14IndexedOffsetArray = self.api.Int14IndexedOffsetArray()
        int14IndexedOffsetArray.read(reader)
        self._checkInt14IndexedOffsetArray(int14IndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        Int14IndexedOffsetArrayTest._writeInt14IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        int14IndexedOffsetArray = self.api.Int14IndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            int14IndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        int14IndexedOffsetArray.write(writer)
        self._checkInt14IndexedOffsetArray(int14IndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInt14IndexedOffsetArray = self.api.Int14IndexedOffsetArray.fromReader(reader)
        self._checkInt14IndexedOffsetArray(readInt14IndexedOffsetArray)
        self.assertTrue(int14IndexedOffsetArray == readInt14IndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.write_bits(0, bitPosition)
        int14IndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(int14IndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        int14IndexedOffsetArray = self._createInt14IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            int14IndexedOffsetArray.write(writer, callInitializeOffsets=False)

    @staticmethod
    def _writeInt14IndexedOffsetArrayToStream(writer, writeWrongOffsets):
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.write_bits(WRONG_OFFSET, 32)
            else:
                writer.write_bits(currentOffset, 32)
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE

        writer.write_bits(SPACER_VALUE, 1)

        writer.write_bits(0, 7)
        for i in range(NUM_ELEMENTS):
            writer.write_bits(i, ELEMENT_SIZE)
            if i != NUM_ELEMENTS - 1:
                writer.write_bits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE)

    def _checkOffsets(self, int14IndexedOffsetArray, offsetShift):
        offsets = int14IndexedOffsetArray.offsets
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkInt14IndexedOffsetArray(self, int14IndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(int14IndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, int14IndexedOffsetArray.spacer)

        data = int14IndexedOffsetArray.data
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual(i, data[i])

    def _createInt14IndexedOffsetArray(self, createWrongOffsets):
        offsets = []
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if createWrongOffsets and i == NUM_ELEMENTS - 1:
                offsets.append(WRONG_OFFSET)
            else:
                offsets.append(currentOffset)
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE

        data = []
        for i in range(NUM_ELEMENTS):
            data.append(i)

        return self.api.Int14IndexedOffsetArray(offsets, SPACER_VALUE, data)

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1
ELEMENT_SIZE = 14
ALIGNED_ELEMENT_SIZE = 16
ALIGNED_ELEMENT_BYTE_SIZE = 2

SPACER_VALUE = 1

INT14_INDEXED_OFFSET_ARRAY_BIT_SIZE = (ELEMENT0_OFFSET * 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                       ELEMENT_SIZE)
