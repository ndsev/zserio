import unittest
import zserio

from testutils import getZserioApi

class OptionalNestedIndexedOffsetArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "indexed_offsets.zs").optional_nested_indexed_offset_array

    def testBitSizeOfWithOptional(self):
        length = NUM_ELEMENTS
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.bitsizeof())

    def testBbitsizeofWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.bitsizeof())

    def testInitializeOffsetsWithOptional(self):
        length = NUM_ELEMENTS
        createWrongOffsets = True
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitPosition = 0
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.initialize_offsets(bitPosition))
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)

    def testInitializeOffsetsWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitPosition = 0
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.initialize_offsets(bitPosition))

    def testReadWithOptional(self):
        length = NUM_ELEMENTS
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalNestedIndexedOffsetArrayTest._writeOptionalNestedIndexedOffsetArrayToStream(writer, length,
                                                                                            writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray()
        optionalNestedIndexedOffsetArray.read(reader)
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)

    def testReadWithoutOptional(self):
        length = 0
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalNestedIndexedOffsetArrayTest._writeOptionalNestedIndexedOffsetArrayToStream(writer, length,
                                                                                            writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray()
        optionalNestedIndexedOffsetArray.read(reader)
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)

    def testWriteWithOptional(self):
        length = NUM_ELEMENTS
        createWrongOffsets = True
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        writer = zserio.BitStreamWriter()
        optionalNestedIndexedOffsetArray.write(writer)
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readOptionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray.from_reader(reader)
        self._checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length)
        self.assertTrue(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray)

    def testWriteWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitBuffer = zserio.serialize(optionalNestedIndexedOffsetArray)
        readOptionalNestedIndexedOffsetArray = zserio.deserialize(self.api.OptionalNestedIndexedOffsetArray,
                                                                  bitBuffer)
        self._checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length)
        self.assertTrue(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray)

    @staticmethod
    def _writeOptionalNestedIndexedOffsetArrayToStream(writer, length, writeWrongOffsets):
        writer.write_bits(length, 16)

        if length > 0:
            currentOffset = ELEMENT0_OFFSET
            for i in range(length):
                if writeWrongOffsets and i == length - 1:
                    writer.write_bits(WRONG_OFFSET, 32)
                else:
                    writer.write_bits(currentOffset, 32)
                currentOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

            # already aligned
            for i in range(length):
                writer.write_string(DATA[i])

        writer.write_bits(FIELD_VALUE, 6)

    def _checkOffsets(self, optionalNestedIndexedOffsetArray, offsetShift):
        length = optionalNestedIndexedOffsetArray.header.length
        offsets = optionalNestedIndexedOffsetArray.header.offsets
        self.assertEqual(length, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for i in range(length):
            self.assertEqual(expectedOffset, offsets[i])
            expectedOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

    def _checkOptionalNestedIndexedOffsetArray(self, optionalNestedIndexedOffsetArray, length):
        self.assertEqual(length, optionalNestedIndexedOffsetArray.header.length)

        offsetShift = 0
        self._checkOffsets(optionalNestedIndexedOffsetArray, offsetShift)

        if length > 0:
            data = optionalNestedIndexedOffsetArray.data
            self.assertEqual(length, len(data))
            for i in range(length):
                self.assertTrue(DATA[i] == data[i])

        self.assertEqual(FIELD_VALUE, optionalNestedIndexedOffsetArray.field)

    def _createOptionalNestedIndexedOffsetArray(self, length, createWrongOffsets):
        optionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray()

        offsets = []
        currentOffset = ELEMENT0_OFFSET
        for i in range(length):
            if createWrongOffsets and i == length - 1:
                offsets.append(WRONG_OFFSET)
            else:
                offsets.append(currentOffset)
            currentOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

        optionalNestedIndexedOffsetArray.header = self.api.Header(length, offsets)

        if length > 0:
            optionalNestedIndexedOffsetArray.data = DATA

        optionalNestedIndexedOffsetArray.field = FIELD_VALUE

        return optionalNestedIndexedOffsetArray

    @staticmethod
    def _getOptionalNestedIndexedOffsetArrayBitSize(length):
        bitSize = 16 + length * 32
        if length > 0:
            # already aligned
            for i in range(length):
                bitSize += zserio.bitsizeof.bitsizeof_string(DATA[i])
        bitSize += 6

        return bitSize

NUM_ELEMENTS = 5

WRONG_OFFSET = 0
ELEMENT0_OFFSET = 2 + NUM_ELEMENTS * 4

FIELD_VALUE = 63

DATA = ["Green", "Red", "Pink", "Blue", "Black"]
