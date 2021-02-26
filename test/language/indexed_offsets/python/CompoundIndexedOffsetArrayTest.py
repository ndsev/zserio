import unittest
import zserio

from testutils import getZserioApi

class CompoundIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").compound_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        self.assertEqual(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE, compoundIndexedOffsetArray.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         compoundIndexedOffsetArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         compoundIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         compoundIndexedOffsetArray.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(compoundIndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        CompoundIndexedOffsetArrayTest._writeCompoundIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        compoundIndexedOffsetArray = self.api.CompoundIndexedOffsetArray()
        compoundIndexedOffsetArray.read(reader)
        self._checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        CompoundIndexedOffsetArrayTest._writeCompoundIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        compoundIndexedOffsetArray = self.api.CompoundIndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            compoundIndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        compoundIndexedOffsetArray.write(writer)
        self._checkCompoundIndexedOffsetArray(compoundIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readCompoundIndexedOffsetArray = self.api.CompoundIndexedOffsetArray.fromReader(reader)
        self._checkCompoundIndexedOffsetArray(readCompoundIndexedOffsetArray)
        self.assertTrue(compoundIndexedOffsetArray == readCompoundIndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.writeBits(0, bitPosition)
        compoundIndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(compoundIndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        compoundIndexedOffsetArray = self._createCompoundIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            compoundIndexedOffsetArray.write(writer, callInitializeOffsets=False)

    @staticmethod
    def _writeCompoundIndexedOffsetArrayToStream(writer, writeWrongOffsets):
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
            writer.writeBits(i, 32)
            writer.writeBits(i % 8, 3)
            if i != NUM_ELEMENTS - 1:
                writer.writeBits(0, ALIGNED_ELEMENT_SIZE - ELEMENT_SIZE)

    def _checkOffsets(self, compoundIndexedOffsetArray, offsetShift):
        offsets = compoundIndexedOffsetArray.offsets
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkCompoundIndexedOffsetArray(self, compoundIndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(compoundIndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, compoundIndexedOffsetArray.spacer)

        data = compoundIndexedOffsetArray.data
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            compound = data[i]
            self.assertEqual(i, compound.id)
            self.assertEqual(i % 8, compound.value)

    def _createCompoundIndexedOffsetArray(self, createWrongOffsets):
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
            compound = self.api.Compound(i, i % 8)
            data.append(compound)

        return self.api.CompoundIndexedOffsetArray(offsets, SPACER_VALUE, data)

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1
ELEMENT_SIZE = 35
ALIGNED_ELEMENT_BYTE_SIZE = 5
ALIGNED_ELEMENT_SIZE = ALIGNED_ELEMENT_BYTE_SIZE * 8

SPACER_VALUE = 1

COMPOUND_INDEXED_OFFSET_ARRAY_BIT_SIZE = (NUM_ELEMENTS * 32 + 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                          ELEMENT_SIZE)
