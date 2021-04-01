import unittest
import zserio

from testutils import getZserioApi

class BitOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").bit_offset

    def testBitSizeOf(self):
        createWrongOffsets = False
        bitOffset = self._createBitOffset(createWrongOffsets)
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE, bitOffset.bitsizeof())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE + 7, bitOffset.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE, bitOffset.initialize_offsets(bitPosition))
        self._checkBitOffset(bitOffset)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE + bitPosition + 6, bitOffset.initialize_offsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(bitOffset, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        self._writeBitOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        bitOffset = self.api.BitOffset.from_reader(reader)
        self._checkBitOffset(bitOffset)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        self._writeBitOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        with self.assertRaises(zserio.PythonRuntimeException):
            bitOffset = self.api.BitOffset.from_reader(reader)
            self._checkBitOffset(bitOffset)

    def testWrite(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitOffset.write(writer)
        self._checkBitOffset(bitOffset)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readBitOffset = self.api.BitOffset.from_reader(reader)
        self._checkBitOffset(readBitOffset)
        self.assertTrue(bitOffset == readBitOffset)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.write_bits(0, bitPosition)
        bitOffset.write(writer)

        offsetShift = 1
        self._checkOffsets(bitOffset, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            bitOffset.write(writer, zserio_call_initialize_offsets=False)

    def _writeBitOffsetToStream(self, writer, writeWrongOffsets):
        if writeWrongOffsets:
            writer.write_bits(self.WRONG_FIELD1_OFFSET, 8)
            writer.write_bits(self.WRONG_FIELD2_OFFSET, 16)
            writer.write_bits(self.WRONG_FIELD3_OFFSET, 32)
            writer.write_bits(self.WRONG_FIELD4_OFFSET, 8)
            writer.write_bits(self.WRONG_FIELD5_OFFSET, 15)
            writer.write_bits(self.WRONG_FIELD6_OFFSET, 18)
            writer.write_bits(self.WRONG_FIELD7_OFFSET, 23)
            writer.write_bits(self.WRONG_FIELD8_OFFSET, 8)
        else:
            writer.write_bits(self.FIELD1_OFFSET, 8)
            writer.write_bits(self.FIELD2_OFFSET, 16)
            writer.write_bits(self.FIELD3_OFFSET, 32)
            writer.write_bits(self.FIELD4_OFFSET, 8)
            writer.write_bits(self.FIELD5_OFFSET, 15)
            writer.write_bits(self.FIELD6_OFFSET, 18)
            writer.write_bits(self.FIELD7_OFFSET, 23)
            writer.write_bits(self.FIELD8_OFFSET, 8)

        writer.write_bits(self.FIELD1_VALUE, 1)

        writer.write_bits(0, 7)
        writer.write_bits(self.FIELD2_VALUE, 2)

        writer.write_bits(0, 6)
        writer.write_bits(self.FIELD3_VALUE, 3)

        writer.write_bits(0, 5)
        writer.write_bits(self.FIELD4_VALUE, 4)

        writer.write_bits(0, 4)
        writer.write_bits(self.FIELD5_VALUE, 5)

        writer.write_bits(0, 3)
        writer.write_bits(self.FIELD6_VALUE, 6)

        writer.write_bits(0, 2)
        writer.write_bits(self.FIELD7_VALUE, 7)

        writer.write_bits(0, 1)
        writer.write_bits(self.FIELD8_VALUE, 8)

    def _checkOffsets(self, bitOffset, offsetShift):
        self.assertEqual(self.FIELD1_OFFSET + offsetShift, bitOffset.field1_offset)
        self.assertEqual(self.FIELD2_OFFSET + offsetShift, bitOffset.field2_offset)
        self.assertEqual(self.FIELD3_OFFSET + offsetShift, bitOffset.field3_offset)
        self.assertEqual(self.FIELD4_OFFSET + offsetShift, bitOffset.field4_offset)
        self.assertEqual(self.FIELD5_OFFSET + offsetShift, bitOffset.field5_offset)
        self.assertEqual(self.FIELD6_OFFSET + offsetShift, bitOffset.field6_offset)
        self.assertEqual(self.FIELD7_OFFSET + offsetShift, bitOffset.field7_offset)
        self.assertEqual(self.FIELD8_OFFSET + offsetShift, bitOffset.field8_offset)

    def _checkBitOffset(self, bitOffset):
        offsetShift = 0
        self._checkOffsets(bitOffset, offsetShift)

        self.assertEqual(self.FIELD1_VALUE, bitOffset.field1)
        self.assertEqual(self.FIELD2_VALUE, bitOffset.field2)
        self.assertEqual(self.FIELD3_VALUE, bitOffset.field3)
        self.assertEqual(self.FIELD4_VALUE, bitOffset.field4)
        self.assertEqual(self.FIELD5_VALUE, bitOffset.field5)
        self.assertEqual(self.FIELD6_VALUE, bitOffset.field6)
        self.assertEqual(self.FIELD7_VALUE, bitOffset.field7)
        self.assertEqual(self.FIELD8_VALUE, bitOffset.field8)

    def _createBitOffset(self, createWrongOffsets):
        bitOffset = self.api.BitOffset()

        if createWrongOffsets:
            bitOffset.field1_offset = self.WRONG_FIELD1_OFFSET
            bitOffset.field2_offset = self.WRONG_FIELD2_OFFSET
            bitOffset.field3_offset = self.WRONG_FIELD3_OFFSET
            bitOffset.field4_offset = self.WRONG_FIELD4_OFFSET
            bitOffset.field5_offset = self.WRONG_FIELD5_OFFSET
            bitOffset.field6_offset = self.WRONG_FIELD6_OFFSET
            bitOffset.field7_offset = self.WRONG_FIELD7_OFFSET
            bitOffset.field8_offset = self.WRONG_FIELD8_OFFSET
        else:
            bitOffset.field1_offset = self.FIELD1_OFFSET
            bitOffset.field2_offset = self.FIELD2_OFFSET
            bitOffset.field3_offset = self.FIELD3_OFFSET
            bitOffset.field4_offset = self.FIELD4_OFFSET
            bitOffset.field5_offset = self.FIELD5_OFFSET
            bitOffset.field6_offset = self.FIELD6_OFFSET
            bitOffset.field7_offset = self.FIELD7_OFFSET
            bitOffset.field8_offset = self.FIELD8_OFFSET

        bitOffset.field1 = self.FIELD1_VALUE
        bitOffset.field2 = self.FIELD2_VALUE
        bitOffset.field3 = self.FIELD3_VALUE
        bitOffset.field4 = self.FIELD4_VALUE
        bitOffset.field5 = self.FIELD5_VALUE
        bitOffset.field6 = self.FIELD6_VALUE
        bitOffset.field7 = self.FIELD7_VALUE
        bitOffset.field8 = self.FIELD8_VALUE

        return bitOffset

    BIT_OFFSET_BIT_SIZE = 192

    WRONG_FIELD1_OFFSET = 0
    WRONG_FIELD2_OFFSET = 0
    WRONG_FIELD3_OFFSET = 0
    WRONG_FIELD4_OFFSET = 0
    WRONG_FIELD5_OFFSET = 0
    WRONG_FIELD6_OFFSET = 0
    WRONG_FIELD7_OFFSET = 0
    WRONG_FIELD8_OFFSET = 0

    FIELD1_OFFSET = 16
    FIELD2_OFFSET = 17
    FIELD3_OFFSET = 18
    FIELD4_OFFSET = 19
    FIELD5_OFFSET = 20
    FIELD6_OFFSET = 21
    FIELD7_OFFSET = 22
    FIELD8_OFFSET = 23

    FIELD1_VALUE = 1
    FIELD2_VALUE = 2
    FIELD3_VALUE = 5
    FIELD4_VALUE = 13
    FIELD5_VALUE = 26
    FIELD6_VALUE = 56
    FIELD7_VALUE = 88
    FIELD8_VALUE = 222
