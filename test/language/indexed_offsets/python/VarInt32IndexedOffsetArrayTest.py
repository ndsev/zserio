import zserio

import IndexedOffsets


class VarInt32IndexedOffsetArrayTest(IndexedOffsets.TestCase):
    def testBitSizeOf(self):
        createWrongOffsets = False
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        self.assertEqual(
            VarInt32IndexedOffsetArrayTest._getVarInt32IndexedOffsetArrayBitSize(),
            varInt32IndexedOffsetArray.bitsizeof(),
        )

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(
            VarInt32IndexedOffsetArrayTest._getVarInt32IndexedOffsetArrayBitSize() - bitPosition,
            varInt32IndexedOffsetArray.bitsizeof(bitPosition),
        )

    def testInitializeOffsets(self):
        createWrongOffsets = True
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(
            VarInt32IndexedOffsetArrayTest._getVarInt32IndexedOffsetArrayBitSize(),
            varInt32IndexedOffsetArray.initialize_offsets(bitPosition),
        )
        self._checkVarInt32IndexedOffsetArray(varInt32IndexedOffsetArray)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        bitPosition = 9
        self.assertEqual(
            VarInt32IndexedOffsetArrayTest._getVarInt32IndexedOffsetArrayBitSize() + bitPosition - 1,
            varInt32IndexedOffsetArray.initialize_offsets(bitPosition),
        )

        offsetShift = 1
        self._checkOffsets(varInt32IndexedOffsetArray, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        VarInt32IndexedOffsetArrayTest._writeVarInt32IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        varInt32IndexedOffsetArray = self.api.VarInt32IndexedOffsetArray()
        varInt32IndexedOffsetArray.read(reader)
        self._checkVarInt32IndexedOffsetArray(varInt32IndexedOffsetArray)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        VarInt32IndexedOffsetArrayTest._writeVarInt32IndexedOffsetArrayToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        varInt32IndexedOffsetArray = self.api.VarInt32IndexedOffsetArray()
        with self.assertRaises(zserio.PythonRuntimeException):
            varInt32IndexedOffsetArray.read(reader)

    def testWrite(self):
        createWrongOffsets = True
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        varInt32IndexedOffsetArray.initialize_offsets(writer.bitposition)
        varInt32IndexedOffsetArray.write(writer)
        self._checkVarInt32IndexedOffsetArray(varInt32IndexedOffsetArray)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readVarInt32IndexedOffsetArray = self.api.VarInt32IndexedOffsetArray.from_reader(reader)
        self._checkVarInt32IndexedOffsetArray(readVarInt32IndexedOffsetArray)
        self.assertTrue(varInt32IndexedOffsetArray == readVarInt32IndexedOffsetArray)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 8
        writer.write_bits(0, bitPosition)
        varInt32IndexedOffsetArray.initialize_offsets(writer.bitposition)
        varInt32IndexedOffsetArray.write(writer)

        offsetShift = 1
        self._checkOffsets(varInt32IndexedOffsetArray, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        varInt32IndexedOffsetArray = self._createVarInt32IndexedOffsetArray(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            varInt32IndexedOffsetArray.write(writer)

    @staticmethod
    def _writeVarInt32IndexedOffsetArrayToStream(writer, writeWrongOffsets):
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if writeWrongOffsets and i == NUM_ELEMENTS - 1:
                writer.write_bits(WRONG_OFFSET, 32)
            else:
                writer.write_bits(currentOffset, 32)
            currentOffset += zserio.bitsizeof.bitsizeof_varint32(i) // 8

        writer.write_bits(SPACER_VALUE, 1)

        writer.write_bits(0, 7)
        for i in range(NUM_ELEMENTS):
            writer.write_varint32(i)

    def _checkOffsets(self, varInt32IndexedOffsetArray, offsetShift):
        offsets = varInt32IndexedOffsetArray.offsets
        self.assertEqual(NUM_ELEMENTS, len(offsets))
        expectedOffset = ELEMENT0_OFFSET + offsetShift
        for i in range(NUM_ELEMENTS):
            self.assertEqual(expectedOffset, offsets[i])
            expectedOffset += zserio.bitsizeof.bitsizeof_varint32(i) // 8

    def _checkVarInt32IndexedOffsetArray(self, varInt32IndexedOffsetArray):
        offsetShift = 0
        self._checkOffsets(varInt32IndexedOffsetArray, offsetShift)

        self.assertEqual(SPACER_VALUE, varInt32IndexedOffsetArray.spacer)

        data = varInt32IndexedOffsetArray.data
        self.assertEqual(NUM_ELEMENTS, len(data))
        for i in range(NUM_ELEMENTS):
            self.assertEqual(i, data[i])

    def _createVarInt32IndexedOffsetArray(self, createWrongOffsets):
        offsets = []
        currentOffset = ELEMENT0_OFFSET
        for i in range(NUM_ELEMENTS):
            if createWrongOffsets and i == NUM_ELEMENTS - 1:
                offsets.append(WRONG_OFFSET)
            else:
                offsets.append(currentOffset)
            currentOffset += zserio.bitsizeof.bitsizeof_varint32(i) // 8

        data = []
        for i in range(NUM_ELEMENTS):
            data.append(i)

        return self.api.VarInt32IndexedOffsetArray(offsets, SPACER_VALUE, data)

    @staticmethod
    def _getVarInt32IndexedOffsetArrayBitSize():
        bitSize = ELEMENT0_OFFSET * 8
        for i in range(NUM_ELEMENTS):
            bitSize += zserio.bitsizeof.bitsizeof_varint32(i)

        return bitSize


NUM_ELEMENTS = 5

WRONG_OFFSET = 0

ELEMENT0_OFFSET = NUM_ELEMENTS * 4 + 1
SPACER_VALUE = 1
