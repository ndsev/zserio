import unittest
import zserio

from testutils import getZserioApi

class NestedOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").nested_offset

    def testBitSizeOf(self):
        createWrongOffsets = False
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE - bitPosition, nestedOffset.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.initializeOffsets(bitPosition))
        self._checkNestedOffset(nestedOffset)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.NEST_OFFSET_BIT_SIZE, nestedOffset.initializeOffsets(bitPosition))
        self._checkNestedOffset(nestedOffset)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        self._writeNestedOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        nestedOffset = self.api.NestedOffset.fromReader(reader)
        self._checkNestedOffset(nestedOffset)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        self._writeNestedOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray())
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.NestedOffset.fromReader(reader)

    def testWrite(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        nestedOffset.write(writer)
        self._checkNestedOffset(nestedOffset)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readNestedOffset = self.api.NestedOffset.fromReader(reader)
        self._checkNestedOffset(readNestedOffset)
        self.assertTrue(nestedOffset == readNestedOffset)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.writeBits(0, bitPosition)
        nestedOffset.write(writer)
        self._checkNestedOffset(nestedOffset)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        nestedOffset = self._createNestedOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            nestedOffset.write(writer, callInitializeOffsets=False)

    def _writeNestedOffsetToStream(self, writer, writeWrongOffsets):
        writer.writeBits(self.WRONG_TERMINATOR_OFFSET if writeWrongOffsets else self.TERMINATOR_OFFSET, 32)
        writer.writeBool(self.BOOL_VALUE)
        writer.writeVarUInt64(self.api.NestedOffsetUnion.CHOICE_nestedOffsetArrayStructure)

        writer.writeBits(self.NUM_ELEMENTS, 8)
        for i in range(self.NUM_ELEMENTS):
            writer.writeBits(self.WRONG_DATA_OFFSET if writeWrongOffsets else self.FIRST_DATA_OFFSET + i * 8,
                             32)
            writer.writeBits(0, 7 if (i == 0) else 1)
            writer.writeBits(i, 31)

        writer.writeBits(self.TERMINATOR_VALUE, 7)

    def _checkNestedOffset(self, nestedOffset):
        self.assertEqual(self.TERMINATOR_OFFSET, nestedOffset.getTerminatorOffset())
        self.assertEqual(self.BOOL_VALUE, nestedOffset.getBoolValue())

        nestedOffsetChoice = nestedOffset.getNestedOffsetChoice()
        self.assertEqual(self.BOOL_VALUE, nestedOffsetChoice.getType())

        nestedOffsetUnion = nestedOffsetChoice.getNestedOffsetUnion()
        self.assertEqual(self.api.NestedOffsetUnion.CHOICE_nestedOffsetArrayStructure,
                         nestedOffsetUnion.choiceTag())

        nestedOffsetArrayStructure = nestedOffsetUnion.getNestedOffsetArrayStructure()
        self.assertEqual(self.NUM_ELEMENTS, nestedOffsetArrayStructure.getNumElements())

        nestedOffsetStructureList = nestedOffsetArrayStructure.getNestedOffsetStructureList()
        self.assertEqual(self.NUM_ELEMENTS, len(nestedOffsetStructureList))
        for i in range(self.NUM_ELEMENTS):
            nestedOffsetStructure = nestedOffsetStructureList[i]
            self.assertEqual(self.FIRST_DATA_OFFSET + i * 8, nestedOffsetStructure.getDataOffset())
            self.assertEqual(i, nestedOffsetStructure.getData())

    def _createNestedOffset(self, createWrongOffsets):
        nestedOffsetStructureList = []
        for i in range(self.NUM_ELEMENTS):
            dataOffset = self.WRONG_DATA_OFFSET if createWrongOffsets else self.FIRST_DATA_OFFSET + i * 8
            nestedOffsetStructureList.append(self.api.NestedOffsetStructure.fromFields(dataOffset, i))

        nestedOffsetArrayStructure = self.api.NestedOffsetArrayStructure.fromFields(self.NUM_ELEMENTS,
                                                                                    nestedOffsetStructureList)

        nestedOffsetUnion = self.api.NestedOffsetUnion()
        nestedOffsetUnion.setNestedOffsetArrayStructure(nestedOffsetArrayStructure)

        nestedOffsetChoice = self.api.NestedOffsetChoice(self.BOOL_VALUE)
        nestedOffsetChoice.setNestedOffsetUnion(nestedOffsetUnion)

        terminatorOffset = self.WRONG_TERMINATOR_OFFSET if createWrongOffsets else self.TERMINATOR_OFFSET
        nestedOffset = self.api.NestedOffset.fromFields(terminatorOffset, self.BOOL_VALUE, nestedOffsetChoice,
                                                        self.TERMINATOR_VALUE)

        return nestedOffset

    BOOL_VALUE = True
    NUM_ELEMENTS = 2

    WRONG_TERMINATOR_OFFSET = 0
    TERMINATOR_OFFSET = 7 + 2 * 8

    WRONG_DATA_OFFSET = 0
    FIRST_DATA_OFFSET = 7 + 4

    TERMINATOR_VALUE = 0x45

    NEST_OFFSET_BIT_SIZE = (7 + 2 * 8) * 8 + 7
