import zserio

import Offsets


class NestedOffsetTest(Offsets.TestCase):
    def testBitSizeOf(self):
        createWrongOffsets = False
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.bitsizeof())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE - bitPosition, nestedOffset.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.initialize_offsets(bitPosition))
        self._checkNestedOffset(nestedOffset)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.initialize_offsets(bitPosition))
        self._checkNestedOffset(nestedOffset)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        self._writeNestedOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        nestedOffset = self.api.NestedOffset.from_reader(reader)
        self._checkNestedOffset(nestedOffset)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        self._writeNestedOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.NestedOffset.from_reader(reader)

    def testWrite(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        nestedOffset.initialize_offsets(writer.bitposition)
        nestedOffset.write(writer)
        self._checkNestedOffset(nestedOffset)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readNestedOffset = self.api.NestedOffset.from_reader(reader)
        self._checkNestedOffset(readNestedOffset)
        self.assertTrue(nestedOffset == readNestedOffset)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.write_bits(0, bitPosition)
        nestedOffset.initialize_offsets(writer.bitposition)
        nestedOffset.write(writer)
        self._checkNestedOffset(nestedOffset)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            nestedOffset.write(writer)

    def _writeNestedOffsetToStream(self, writer, writeWrongOffsets):
        writer.write_bits(self.WRONG_TERMINATOR_OFFSET if writeWrongOffsets else self.TERMINATOR_OFFSET, 32)
        writer.write_bool(self.BOOL_VALUE)
        writer.write_varsize(self.api.NestedOffsetUnion.CHOICE_NESTED_OFFSET_ARRAY_STRUCTURE)

        writer.write_bits(self.NUM_ELEMENTS, 8)
        for i in range(self.NUM_ELEMENTS):
            writer.write_bits(
                self.WRONG_DATA_OFFSET if writeWrongOffsets else self.FIRST_DATA_OFFSET + i * 8, 32
            )
            writer.write_bits(0, 7 if (i == 0) else 1)
            writer.write_bits(i, 31)

        writer.alignto(8)
        writer.write_bits(self.TERMINATOR_VALUE, 7)

    def _checkNestedOffset(self, nestedOffset):
        self.assertEqual(self.TERMINATOR_OFFSET, nestedOffset.terminator_offset)
        self.assertEqual(self.BOOL_VALUE, nestedOffset.bool_value)

        nestedOffsetChoice = nestedOffset.nested_offset_choice
        self.assertEqual(self.BOOL_VALUE, nestedOffsetChoice.type)

        nestedOffsetUnion = nestedOffsetChoice.nested_offset_union
        self.assertEqual(
            self.api.NestedOffsetUnion.CHOICE_NESTED_OFFSET_ARRAY_STRUCTURE, nestedOffsetUnion.choice_tag
        )

        nestedOffsetArrayStructure = nestedOffsetUnion.nested_offset_array_structure
        self.assertEqual(self.NUM_ELEMENTS, nestedOffsetArrayStructure.num_elements)

        nestedOffsetStructureList = nestedOffsetArrayStructure.nested_offset_structure_list
        self.assertEqual(self.NUM_ELEMENTS, len(nestedOffsetStructureList))
        for i in range(self.NUM_ELEMENTS):
            nestedOffsetStructure = nestedOffsetStructureList[i]
            self.assertEqual(self.FIRST_DATA_OFFSET + i * 8, nestedOffsetStructure.data_offset)
            self.assertEqual(i, nestedOffsetStructure.data)

        self.assertEqual(self.TERMINATOR_VALUE, nestedOffset.terminator)

    def _createNestedOffset(self, createWrongOffsets):
        nestedOffsetStructureList = []
        for i in range(self.NUM_ELEMENTS):
            dataOffset = self.WRONG_DATA_OFFSET if createWrongOffsets else self.FIRST_DATA_OFFSET + i * 8
            nestedOffsetStructureList.append(self.api.NestedOffsetStructure(dataOffset, i))

        nestedOffsetArrayStructure = self.api.NestedOffsetArrayStructure(
            self.NUM_ELEMENTS, nestedOffsetStructureList
        )

        nestedOffsetUnion = self.api.NestedOffsetUnion(
            nested_offset_array_structure_=nestedOffsetArrayStructure
        )

        nestedOffsetChoice = self.api.NestedOffsetChoice(
            self.BOOL_VALUE, nested_offset_union_=nestedOffsetUnion
        )

        terminatorOffset = self.WRONG_TERMINATOR_OFFSET if createWrongOffsets else self.TERMINATOR_OFFSET
        nestedOffset = self.api.NestedOffset(
            terminatorOffset, self.BOOL_VALUE, nestedOffsetChoice, self.TERMINATOR_VALUE
        )

        return nestedOffset

    BOOL_VALUE = True
    NUM_ELEMENTS = 2

    WRONG_TERMINATOR_OFFSET = 0
    TERMINATOR_OFFSET = 7 + 2 * 8

    WRONG_DATA_OFFSET = 0
    FIRST_DATA_OFFSET = 7 + 4

    TERMINATOR_VALUE = 0x45

    NEST_OFFSET_BIT_SIZE = (7 + 2 * 8) * 8 + 7
