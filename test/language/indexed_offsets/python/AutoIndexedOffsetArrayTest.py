import unittest
import zserio

from testutils import getZserioApi

class AutoIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").auto_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE, autoIndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         autoIndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         autoIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         autoIndexedOffsetArray.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        AutoIndexedOffsetArrayTest._writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        autoIndexedOffsetArray = self.api.AutoIndexedOffsetArray()
        autoIndexedOffsetArray.read(reader)
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        AutoIndexedOffsetArrayTest._writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        autoIndexedOffsetArray = self.api.AutoIndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            autoIndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        autoIndexedOffsetArray.write(writer)
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readAutoIndexedOffsetArray = self.api.AutoIndexedOffsetArray.fromReader(reader)
        self._checkAutoIndexedOffsetArray(readAutoIndexedOffsetArray)
        self.assertTrue(autoIndexedOffsetArray == readAutoIndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.writeBits(0, bitPosition)
        autoIndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            autoIndexedOffsetArray.write(writer, callInitializeOffsets=False)

    @staticmethod
    def _writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets):
        writer.writeVarSize(NUM_ELEMENTS)
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.writeBits(WRONG_OFFSET, 32)
            else:
                writer.writeBits(currentOffset, 32)
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE

        writer.writeBits(SPACER_VALUE, 1)

        writer.writeVarSize(NUM_ELEMENTS)
        writer.writeBits(0, 7)
        for i in range(NUM_ELEMENTS):
            writer.writeBits(i % 64, ELEMENT_SIZE)
            if i != NUM_ELEMENTS - 1:
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE)

    def _checkOffsets(self, autoIndexedOffsetArray, offsetShift):
        offsets = autoIndexedOffsetArray.getOffsets()
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkAutoIndexedOffsetArray(self, autoIndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, autoIndexedOffsetArray.getSpacer())

        data = autoIndexedOffsetArray.getData()
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual(i % 64, data[i])

    def _createAutoIndexedOffsetArray(self, createWrongOffsets):
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
            data.append(i % 64)

        return self.api.AutoIndexedOffsetArray(offsets, SPACER_VALUE, data)

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

AUTO_ARRAY_LENGTH_BYTE_SIZE = 1
ELEMENT0_OFFSET = AUTO_ARRAY_LENGTH_BYTE_SIZE + (NUM_ELEMENTS * 4 + 1) + AUTO_ARRAY_LENGTH_BYTE_SIZE
ELEMENT_SIZE = 5
ALIGNED_ELEMENT_SIZE = 8
ALIGNED_ELEMENT_BYTE_SIZE = 1

SPACER_VALUE = 1

AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE = (ELEMENT0_OFFSET * 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                      ELEMENT_SIZE)
