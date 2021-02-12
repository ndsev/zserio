import unittest
import zserio

from testutils import getZserioApi

class Bit5IndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").bit5_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        self.assertEqual(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE, bit5IndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         bit5IndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         bit5IndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkBit5IndexedOffsetArray(bit5IndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         bit5IndexedOffsetArray.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(bit5IndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        Bit5IndexedOffsetArrayTest._writeBit5IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        bit5IndexedOffsetArray = self.api.Bit5IndexedOffsetArray()
        bit5IndexedOffsetArray.read(reader)
        self._checkBit5IndexedOffsetArray(bit5IndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        Bit5IndexedOffsetArrayTest._writeBit5IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        bit5IndexedOffsetArray = self.api.Bit5IndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            bit5IndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bit5IndexedOffsetArray.write(writer)
        self._checkBit5IndexedOffsetArray(bit5IndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readBit5IndexedOffsetArray = self.api.Bit5IndexedOffsetArray.fromReader(reader)
        self._checkBit5IndexedOffsetArray(readBit5IndexedOffsetArray)
        self.assertTrue(bit5IndexedOffsetArray == readBit5IndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.writeBits(0, bitPosition)
        bit5IndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(bit5IndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        bit5IndexedOffsetArray = self._createBit5IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            bit5IndexedOffsetArray.write(writer, callInitializeOffsets=False)

    @staticmethod
    def _writeBit5IndexedOffsetArrayToStream(writer, writeWrongOffsets):
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
            writer.writeBits(i % 64, ELEMENT_SIZE)
            if i != NUM_ELEMENTS - 1:
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE)

    def _checkOffsets(self, bit5IndexedOffsetArray, offsetShift):
        offsets = bit5IndexedOffsetArray.getOffsets()
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkBit5IndexedOffsetArray(self, bit5IndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(bit5IndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, bit5IndexedOffsetArray.getSpacer())

        data = bit5IndexedOffsetArray.getData()
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual(i % 64, data[i])

    def _createBit5IndexedOffsetArray(self, createWrongOffsets):
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

        return self.api.Bit5IndexedOffsetArray(offsets, SPACER_VALUE, data)

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1
ELEMENT_SIZE = 5
ALIGNED_ELEMENT_SIZE = 8
ALIGNED_ELEMENT_BYTE_SIZE = 1

SPACER_VALUE = 1

BIT5_INDEXED_OFFSET_ARRAY_BIT_SIZE = (ELEMENT0_OFFSET * 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                      ELEMENT_SIZE)
