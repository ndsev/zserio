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
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.bitSizeOf())

    def testBbitSizeOfWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.bitSizeOf())

    def testInitializeOffsetsWithOptional(self):
        length = NUM_ELEMENTS
        createWrongOffsets = True
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitPosition = 0
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition))
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)

    def testInitializeOffsetsWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        bitPosition = 0
        bitSize = OptionalNestedIndexedOffsetArrayTest._getOptionalNestedIndexedOffsetArrayBitSize(length)
        self.assertEqual(bitSize, optionalNestedIndexedOffsetArray.initializeOffsets(bitPosition))

    def testReadWithOptional(self):
        length = NUM_ELEMENTS
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalNestedIndexedOffsetArrayTest._writeOptionalNestedIndexedOffsetArrayToStream(writer, length,
                                                                                            writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        optionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray()
        optionalNestedIndexedOffsetArray.read(reader)
        self._checkOptionalNestedIndexedOffsetArray(optionalNestedIndexedOffsetArray, length)

    def testReadWithoutOptional(self):
        length = 0
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalNestedIndexedOffsetArrayTest._writeOptionalNestedIndexedOffsetArrayToStream(writer, length,
                                                                                            writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
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
        reader = zserio.BitStreamReader(writer.getByteArray())
        readOptionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray.fromReader(reader)
        self._checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length)
        self.assertTrue(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray)

    def testWriteWithoutOptional(self):
        length = 0
        createWrongOffsets = False
        optionalNestedIndexedOffsetArray = self._createOptionalNestedIndexedOffsetArray(length,
                                                                                        createWrongOffsets)
        writer = zserio.BitStreamWriter()
        optionalNestedIndexedOffsetArray.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readOptionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray.fromReader(reader)
        self._checkOptionalNestedIndexedOffsetArray(readOptionalNestedIndexedOffsetArray, length)
        self.assertTrue(optionalNestedIndexedOffsetArray == readOptionalNestedIndexedOffsetArray)

    @staticmethod
    def _writeOptionalNestedIndexedOffsetArrayToStream(writer, length, writeWrongOffsets):
        writer.writeSignedBits(length, 16)

        if length > 0:
            currentOffset = ELEMENT0_OFFSET
            for i in range(length):
                if writeWrongOffsets and i == length - 1:
                    writer.writeBits(WRONG_OFFSET, 32)
                else:
                    writer.writeBits(currentOffset, 32)
                currentOffset += zserio.bitsizeof.getBitSizeOfString(DATA[i]) // 8

            # already aligned
            for i in range(length):
                writer.writeString(DATA[i])

        writer.writeBits(FIELD_VALUE, 6)

    def _checkOffsets(self, optionalNestedIndexedOffsetArray, offsetShift):
        length = optionalNestedIndexedOffsetArray.getHeader().getLength()
        offsets = optionalNestedIndexedOffsetArray.getHeader().getOffsets()
        self.assertEqual(length, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for i in range(length):
            self.assertEqual(expectedOffset, offsets[i])
            expectedOffset += zserio.bitsizeof.getBitSizeOfString(DATA[i]) // 8

    def _checkOptionalNestedIndexedOffsetArray(self, optionalNestedIndexedOffsetArray, length):
        self.assertEqual(length, optionalNestedIndexedOffsetArray.getHeader().getLength())

        offsetShift = 0
        self._checkOffsets(optionalNestedIndexedOffsetArray, offsetShift)

        if length > 0:
            data = optionalNestedIndexedOffsetArray.getData()
            self.assertEqual(length, len(data))
            for i in range(length):
                self.assertTrue(DATA[i] == data[i])

        self.assertEqual(FIELD_VALUE, optionalNestedIndexedOffsetArray.getField())

    def _createOptionalNestedIndexedOffsetArray(self, length, createWrongOffsets):
        optionalNestedIndexedOffsetArray = self.api.OptionalNestedIndexedOffsetArray()

        offsets = []
        currentOffset = ELEMENT0_OFFSET
        for i in range(length):
            if createWrongOffsets and i == length - 1:
                offsets.append(WRONG_OFFSET)
            else:
                offsets.append(currentOffset)
            currentOffset += zserio.bitsizeof.getBitSizeOfString(DATA[i]) // 8

        optionalNestedIndexedOffsetArray.setHeader(self.api.Header(length, offsets))

        if length > 0:
            optionalNestedIndexedOffsetArray.setData(DATA)

        optionalNestedIndexedOffsetArray.setField(FIELD_VALUE)

        return optionalNestedIndexedOffsetArray

    @staticmethod
    def _getOptionalNestedIndexedOffsetArrayBitSize(length):
        bitSize = 16 + length * 32
        if length > 0:
            # already aligned
            for i in range(length):
                bitSize += zserio.bitsizeof.getBitSizeOfString(DATA[i])
        bitSize += 6

        return bitSize

NUM_ELEMENTS = 5

WRONG_OFFSET = 0
ELEMENT0_OFFSET = 2 + NUM_ELEMENTS * 4

FIELD_VALUE = 63

DATA = ["Green", "Red", "Pink", "Blue", "Black"]
