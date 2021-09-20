import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class PackedAutoIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").packed_auto_indexed_offset_array

    def testBitSizeOf(self):
        createWrongOffsets = False
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE, autoIndexedOffsetArray.bitsizeof())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE - bitPosition,
                         autoIndexedOffsetArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE,
                         autoIndexedOffsetArray.initialize_offsets(bitPosition))
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE + bitPosition - 1,
                         autoIndexedOffsetArray.initialize_offsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        PackedAutoIndexedOffsetArrayTest._writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        autoIndexedOffsetArray = self.api.AutoIndexedOffsetArray()
        autoIndexedOffsetArray.read(reader)
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        PackedAutoIndexedOffsetArrayTest._writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        autoIndexedOffsetArray = self.api.AutoIndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            autoIndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        autoIndexedOffsetArray.write(writer)
        self._checkAutoIndexedOffsetArray(autoIndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readAutoIndexedOffsetArray = self.api.AutoIndexedOffsetArray.from_reader(reader)
        self._checkAutoIndexedOffsetArray(readAutoIndexedOffsetArray)
        self.assertTrue(autoIndexedOffsetArray == readAutoIndexedOffsetArray)

    def testWriteFile(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        zserio.serialize_to_file(autoIndexedOffsetArray, BLOB_NAME)

        readAutoIndexedOffsetArray = zserio.deserialize_from_file(self.api.AutoIndexedOffsetArray, BLOB_NAME)
        self.assertEqual(autoIndexedOffsetArray, readAutoIndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.write_bits(0, bitPosition)
        autoIndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        autoIndexedOffsetArray = self._createAutoIndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            autoIndexedOffsetArray.write(writer, zserio_call_initialize_offsets=False)

    @staticmethod
    def _writeAutoIndexedOffsetArrayToStream(writer, writeWrongOffsets):
        writer.write_varsize(NUM_ELEMENTS)
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.write_bits(WRONG_OFFSET, 32)
            else:
                writer.write_bits(currentOffset, 32)
            currentOffset += ALIGNED_ELEMENT_BYTE_SIZE

        writer.write_bits(SPACER_VALUE, 3)

        writer.write_varsize(NUM_ELEMENTS)

        writer.write_bool(True)
        writer.write_bits(PACKED_ARRAY_MAX_BIT_NUMBER, 6)
        writer.alignto(8)
        writer.write_bits(0, ELEMENT_SIZE)
        for _ in range(NUM_ELEMENTS - 1):
            writer.alignto(8)
            writer.write_signed_bits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1)

    def _checkOffsets(self, autoIndexedOffsetArray, offsetShift):
        offsets = autoIndexedOffsetArray.offsets
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for offset in offsets:
            self.assertEqual(expectedOffset, offset)
            expectedOffset += ALIGNED_ELEMENT_BYTE_SIZE

    def _checkAutoIndexedOffsetArray(self, autoIndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(autoIndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, autoIndexedOffsetArray.spacer)

        data = autoIndexedOffsetArray.data
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual(i, data[i])

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
            data.append(i)

        return self.api.AutoIndexedOffsetArray(offsets, SPACER_VALUE, data)

BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_auto_indexed_offset_array.blob")

NUM_ELEMENTS = 5

WRONG_OFFSET = 0

AUTO_ARRAY_LENGTH_BYTE_SIZE = 1
ELEMENT0_OFFSET = (AUTO_ARRAY_LENGTH_BYTE_SIZE + (NUM_ELEMENTS * 4) +
                   (3 + AUTO_ARRAY_LENGTH_BYTE_SIZE * 8 + 1 + 6 + 6) // 8)
ELEMENT_SIZE = 5
ALIGNED_ELEMENT_SIZE = 8
ALIGNED_ELEMENT_BYTE_SIZE = 1

SPACER_VALUE = 7

PACKED_ARRAY_DELTA = 1
PACKED_ARRAY_MAX_BIT_NUMBER = 1

AUTO_INDEXED_OFFSET_ARRAY_BIT_SIZE = (ELEMENT0_OFFSET * 8 + (NUM_ELEMENTS - 1) * ALIGNED_ELEMENT_SIZE +
                                      PACKED_ARRAY_MAX_BIT_NUMBER + 1)
