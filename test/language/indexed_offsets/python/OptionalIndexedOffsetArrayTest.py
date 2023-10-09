import zserio

import IndexedOffsets

class OptionalIndexedOffsetArrayTest(IndexedOffsets.TestCase):
    def testBitSizeOfWithOptional(self):
        hasOptional = True
        createWrongOffsets = False
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        self.assertEqual(OptionalIndexedOffsetArrayTest._getOptionalIndexedOffsetArrayBitSize(hasOptional),
                         optionalIndexedOffsetArray.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        hasOptional = False
        createWrongOffsets = False
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        self.assertEqual(OptionalIndexedOffsetArrayTest._getOptionalIndexedOffsetArrayBitSize(hasOptional),
                         optionalIndexedOffsetArray.bitsizeof())

    def testInitializeOffsetsWithOptional(self):
        hasOptional = True
        createWrongOffsets = True
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        bitPosition = 0
        self.assertEqual(OptionalIndexedOffsetArrayTest._getOptionalIndexedOffsetArrayBitSize(hasOptional),
                         optionalIndexedOffsetArray.initialize_offsets(bitPosition))
        self._checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional)

    def testInitializeOffsetsWithoutOptional(self):
        hasOptional = False
        createWrongOffsets = False
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        bitPosition = 0
        self.assertEqual(OptionalIndexedOffsetArrayTest._getOptionalIndexedOffsetArrayBitSize(hasOptional),
                         optionalIndexedOffsetArray.initialize_offsets(bitPosition))

    def testReadWithOptional(self):
        hasOptional = True
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalIndexedOffsetArrayTest._writeOptionalIndexedOffsetArrayToStream(writer, hasOptional,
                                                                                writeWrongOffsets)
        optionalIndexedOffsetArray = self.api.OptionalIndexedOffsetArray()
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalIndexedOffsetArray.read(reader)
        self._checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional)

    def testReadWithoutOptional(self):
        hasOptional = False
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        OptionalIndexedOffsetArrayTest._writeOptionalIndexedOffsetArrayToStream(writer, hasOptional,
                                                                                writeWrongOffsets)
        optionalIndexedOffsetArray = self.api.OptionalIndexedOffsetArray()
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        optionalIndexedOffsetArray.read(reader)
        self._checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional)

    def testWriteWithOptional(self):
        hasOptional = True
        createWrongOffsets = True
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        writer = zserio.BitStreamWriter()
        optionalIndexedOffsetArray.initialize_offsets(writer.bitposition)
        optionalIndexedOffsetArray.write(writer)
        self._checkOptionalIndexedOffsetArray(optionalIndexedOffsetArray, hasOptional)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readOptionalIndexedOffsetArray = self.api.OptionalIndexedOffsetArray.from_reader(reader)
        self._checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional)
        self.assertTrue(optionalIndexedOffsetArray == readOptionalIndexedOffsetArray)

    def testWriteWithoutOptional(self):
        hasOptional = False
        createWrongOffsets = False
        optionalIndexedOffsetArray = self._createOptionalIndexedOffsetArray(hasOptional, createWrongOffsets)
        bitBuffer = zserio.serialize(optionalIndexedOffsetArray)
        readOptionalIndexedOffsetArray = zserio.deserialize(self.api.OptionalIndexedOffsetArray, bitBuffer)
        self._checkOptionalIndexedOffsetArray(readOptionalIndexedOffsetArray, hasOptional)
        self.assertTrue(optionalIndexedOffsetArray == readOptionalIndexedOffsetArray)

    @staticmethod
    def _writeOptionalIndexedOffsetArrayToStream(writer, hasOptional, writeWrongOffsets):
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.write_bits(WRONG_OFFSET, 32)
            else:
                writer.write_bits(currentOffset, 32)
            currentOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

        writer.write_bool(hasOptional)

        if hasOptional:
            writer.write_bits(0, 7)
            for i in range(NUM_ELEMENTS):
                writer.write_string(DATA[i])

        writer.write_bits(FIELD_VALUE, 6)

    def _checkOffsets(self, optionalIndexedOffsetArray, offsetShift):
        offsets = optionalIndexedOffsetArray.offsets
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for i in range(NUM_ELEMENTS):
            self.assertEqual(expectedOffset, offsets[i])
            expectedOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

    def _checkOptionalIndexedOffsetArray(self, optionalIndexedOffsetArray, hasOptional):
        offsetShift = 0
        self._checkOffsets(optionalIndexedOffsetArray, offsetShift)

        self.assertEqual(hasOptional, optionalIndexedOffsetArray.has_optional)

        if hasOptional:
            data = optionalIndexedOffsetArray.data
            self.assertEqual(NUM_ELEMENTS, len(data))
            for i in range(NUM_ELEMENTS):
                self.assertTrue(DATA[i] == data[i])

        self.assertEqual(FIELD_VALUE, optionalIndexedOffsetArray.field)

    def _createOptionalIndexedOffsetArray(self, hasOptional, createWrongOffsets):
        optionalIndexedOffsetArray = self.api.OptionalIndexedOffsetArray()

        offsets = []
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if createWrongOffsets and i == NUM_ELEMENTS - 1:
                offsets.append(WRONG_OFFSET)
            else:
                offsets.append(currentOffset)
            currentOffset += zserio.bitsizeof.bitsizeof_string(DATA[i]) // 8

        optionalIndexedOffsetArray.offsets = offsets
        optionalIndexedOffsetArray.has_optional = hasOptional

        if hasOptional:
            optionalIndexedOffsetArray.data = DATA

        optionalIndexedOffsetArray.field = FIELD_VALUE

        return optionalIndexedOffsetArray

    @staticmethod
    def _getOptionalIndexedOffsetArrayBitSize(hasOptional):
        bitSize = NUM_ELEMENTS * 32 + 1
        if hasOptional:
            bitSize += 7
            for i in range(NUM_ELEMENTS):
                bitSize += zserio.bitsizeof.bitsizeof_string(DATA[i])
        bitSize += 6

        return bitSize

NUM_ELEMENTS = 5

WRONG_OFFSET = 0
ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1

FIELD_VALUE = 63

DATA = ["Green", "Red", "Pink", "Blue", "Black"]
