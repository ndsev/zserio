import unittest
import zserio

from testutils import getZserioApi

class BoolIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").bool_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        self.assertEqual(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE, boolIndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         boolIndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         boolIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkBoolIndexedOffsetArray(boolIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         boolIndexedOffsetArray.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(boolIndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        BoolIndexedOffsetArrayTest._writeBoolIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        boolIndexedOffsetArray = self.api.BoolIndexedOffsetArray()
        boolIndexedOffsetArray.read(reader)
        self._checkBoolIndexedOffsetArray(boolIndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        BoolIndexedOffsetArrayTest._writeBoolIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        boolIndexedOffsetArray = self.api.BoolIndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            boolIndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        boolIndexedOffsetArray.write(writer)
        self._checkBoolIndexedOffsetArray(boolIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBoolIndexedOffsetArray = self.api.BoolIndexedOffsetArray.fromReader(reader)
        self._checkBoolIndexedOffsetArray(readBoolIndexedOffsetArray)
        self.assertTrue(boolIndexedOffsetArray == readBoolIndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.writeBits(0, bitPosition)
        boolIndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(boolIndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        boolIndexedOffsetArray = self._createBoolIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            boolIndexedOffsetArray.write(writer, callInitializeOffsets=False)

    @staticmethod
    def _writeBoolIndexedOffsetArrayToStream(writer, writeWrongOffsets):
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.writeBits(WRONG_OFFSET, 32)
            else:
                writer.writeBits(currentOffset, 32)
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE

        writer.writeBits(SPACER_VALUE, 1)

        writer.writeBits(0, 7)
        for i in range(NUM_ELEMENTS):
            writer.writeBits((i & 0x01) != 0, ELEMENT_SIZE)
            if i != NUM_ELEMENTS - 1:
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE)

    def _checkOffsets(self, boolIndexedOffsetArray, offsetShift):
        offsets = boolIndexedOffsetArray.getOffsets()
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkBoolIndexedOffsetArray(self, boolIndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(boolIndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, boolIndexedOffsetArray.getSpacer())

        data = boolIndexedOffsetArray.getData()
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual((i & 0x01) != 0, data[i])

    def _createBoolIndexedOffsetArray(self, createWrongOffsets):
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
            data.append((i & 0x01) != 0)

        return self.api.BoolIndexedOffsetArray(offsets, SPACER_VALUE, data)

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1
ELEMENT_SIZE = 1
ALIGNED_ELEMENT_SIZE = 8
ALIGNED_ELEMENT_BYTE_SIZE = 1

SPACER_VALUE = 1

BOOL_INDEXED_OFFSET_ARRAY_BIT_SIZE = (ELEMENT0_OFFSET * 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                      ELEMENT_SIZE)
