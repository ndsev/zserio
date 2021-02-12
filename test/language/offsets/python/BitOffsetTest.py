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
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE, bitOffset.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        createWrongOffsets = False
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 1
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE + 7, bitOffset.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 0
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE, bitOffset.initializeOffsets(bitPosition))
        self._checkBitOffset(bitOffset)

    def testInitializeOffsetsWithPosition(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        bitPosition = 2
        self.assertEqual(self.BIT_OFFSET_BIT_SIZE + bitPosition + 6, bitOffset.initializeOffsets(bitPosition))

        offsetShift = 1
        self._checkOffsets(bitOffset, offsetShift)

    def testRead(self):
        writeWrongOffsets = False
        writer = zserio.BitStreamWriter()
        self._writeBitOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        bitOffset = self.api.BitOffset.fromReader(reader)
        self._checkBitOffset(bitOffset)

    def testReadWrongOffsets(self):
        writeWrongOffsets = True
        writer = zserio.BitStreamWriter()
        self._writeBitOffsetToStream(writer, writeWrongOffsets)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        with self.assertRaises(zserio.PythonRuntimeException):
            bitOffset = self.api.BitOffset.fromReader(reader)
            self._checkBitOffset(bitOffset)

    def testWrite(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitOffset.write(writer)
        self._checkBitOffset(bitOffset)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readBitOffset = self.api.BitOffset.fromReader(reader)
        self._checkBitOffset(readBitOffset)
        self.assertTrue(bitOffset == readBitOffset)

    def testWriteWithPosition(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        bitPosition = 2
        writer.writeBits(0, bitPosition)
        bitOffset.write(writer)

        offsetShift = 1
        self._checkOffsets(bitOffset, offsetShift)

    def testWriteWrongOffsets(self):
        createWrongOffsets = True
        bitOffset = self._createBitOffset(createWrongOffsets)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            bitOffset.write(writer, callInitializeOffsets=False)

    def _writeBitOffsetToStream(self, writer, writeWrongOffsets):
        if writeWrongOffsets:
            writer.writeBits(self.WRONG_FIELD1_OFFSET, 8)
            writer.writeBits(self.WRONG_FIELD2_OFFSET, 16)
            writer.writeBits(self.WRONG_FIELD3_OFFSET, 32)
            writer.writeBits(self.WRONG_FIELD4_OFFSET, 8)
            writer.writeBits(self.WRONG_FIELD5_OFFSET, 15)
            writer.writeBits(self.WRONG_FIELD6_OFFSET, 18)
            writer.writeBits(self.WRONG_FIELD7_OFFSET, 23)
            writer.writeBits(self.WRONG_FIELD8_OFFSET, 8)
        else:
            writer.writeBits(self.FIELD1_OFFSET, 8)
            writer.writeBits(self.FIELD2_OFFSET, 16)
            writer.writeBits(self.FIELD3_OFFSET, 32)
            writer.writeBits(self.FIELD4_OFFSET, 8)
            writer.writeBits(self.FIELD5_OFFSET, 15)
            writer.writeBits(self.FIELD6_OFFSET, 18)
            writer.writeBits(self.FIELD7_OFFSET, 23)
            writer.writeBits(self.FIELD8_OFFSET, 8)

        writer.writeBits(self.FIELD1_VALUE, 1)

        writer.writeBits(0, 7)
        writer.writeBits(self.FIELD2_VALUE, 2)

        writer.writeBits(0, 6)
        writer.writeBits(self.FIELD3_VALUE, 3)

        writer.writeBits(0, 5)
        writer.writeBits(self.FIELD4_VALUE, 4)

        writer.writeBits(0, 4)
        writer.writeBits(self.FIELD5_VALUE, 5)

        writer.writeBits(0, 3)
        writer.writeBits(self.FIELD6_VALUE, 6)

        writer.writeBits(0, 2)
        writer.writeBits(self.FIELD7_VALUE, 7)

        writer.writeBits(0, 1)
        writer.writeBits(self.FIELD8_VALUE, 8)

    def _checkOffsets(self, bitOffset, offsetShift):
        self.assertEqual(self.FIELD1_OFFSET + offsetShift, bitOffset.getField1Offset())
        self.assertEqual(self.FIELD2_OFFSET + offsetShift, bitOffset.getField2Offset())
        self.assertEqual(self.FIELD3_OFFSET + offsetShift, bitOffset.getField3Offset())
        self.assertEqual(self.FIELD4_OFFSET + offsetShift, bitOffset.getField4Offset())
        self.assertEqual(self.FIELD5_OFFSET + offsetShift, bitOffset.getField5Offset())
        self.assertEqual(self.FIELD6_OFFSET + offsetShift, bitOffset.getField6Offset())
        self.assertEqual(self.FIELD7_OFFSET + offsetShift, bitOffset.getField7Offset())
        self.assertEqual(self.FIELD8_OFFSET + offsetShift, bitOffset.getField8Offset())

    def _checkBitOffset(self, bitOffset):
        offsetShift = 0
        self._checkOffsets(bitOffset, offsetShift)

        self.assertEqual(self.FIELD1_VALUE, bitOffset.getField1())
        self.assertEqual(self.FIELD2_VALUE, bitOffset.getField2())
        self.assertEqual(self.FIELD3_VALUE, bitOffset.getField3())
        self.assertEqual(self.FIELD4_VALUE, bitOffset.getField4())
        self.assertEqual(self.FIELD5_VALUE, bitOffset.getField5())
        self.assertEqual(self.FIELD6_VALUE, bitOffset.getField6())
        self.assertEqual(self.FIELD7_VALUE, bitOffset.getField7())
        self.assertEqual(self.FIELD8_VALUE, bitOffset.getField8())

    def _createBitOffset(self, createWrongOffsets):
        bitOffset = self.api.BitOffset()

        if createWrongOffsets:
            bitOffset.setField1Offset(self.WRONG_FIELD1_OFFSET)
            bitOffset.setField2Offset(self.WRONG_FIELD2_OFFSET)
            bitOffset.setField3Offset(self.WRONG_FIELD3_OFFSET)
            bitOffset.setField4Offset(self.WRONG_FIELD4_OFFSET)
            bitOffset.setField5Offset(self.WRONG_FIELD5_OFFSET)
            bitOffset.setField6Offset(self.WRONG_FIELD6_OFFSET)
            bitOffset.setField7Offset(self.WRONG_FIELD7_OFFSET)
            bitOffset.setField8Offset(self.WRONG_FIELD8_OFFSET)
        else:
            bitOffset.setField1Offset(self.FIELD1_OFFSET)
            bitOffset.setField2Offset(self.FIELD2_OFFSET)
            bitOffset.setField3Offset(self.FIELD3_OFFSET)
            bitOffset.setField4Offset(self.FIELD4_OFFSET)
            bitOffset.setField5Offset(self.FIELD5_OFFSET)
            bitOffset.setField6Offset(self.FIELD6_OFFSET)
            bitOffset.setField7Offset(self.FIELD7_OFFSET)
            bitOffset.setField8Offset(self.FIELD8_OFFSET)

        bitOffset.setField1(self.FIELD1_VALUE)
        bitOffset.setField2(self.FIELD2_VALUE)
        bitOffset.setField3(self.FIELD3_VALUE)
        bitOffset.setField4(self.FIELD4_VALUE)
        bitOffset.setField5(self.FIELD5_VALUE)
        bitOffset.setField6(self.FIELD6_VALUE)
        bitOffset.setField7(self.FIELD7_VALUE)
        bitOffset.setField8(self.FIELD8_VALUE)

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
