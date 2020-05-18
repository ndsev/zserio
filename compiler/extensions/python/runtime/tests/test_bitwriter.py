import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitwriter import BitStreamWriter
from zserio.exception import PythonRuntimeException
from zserio.limits import (VARINT16_MIN, VARINT16_MAX, VARINT32_MIN, VARINT32_MAX, VARINT64_MIN, VARINT64_MAX,
                           VARINT_MIN, VARINT_MAX, VARUINT16_MIN, VARUINT16_MAX, VARUINT32_MIN, VARUINT32_MAX,
                           VARUINT64_MIN, VARUINT64_MAX, VARUINT_MIN, VARUINT_MAX, VARSIZE_MIN, VARSIZE_MAX)

class BitStreamWriterTest(unittest.TestCase):

    def testWriteUnalignedData(self):
        # number expected to be written at offset
        testValue = 123

        for offset in range(65):
            writer = BitStreamWriter()

            if offset != 0:
                writer.writeBits(0, offset)
            writer.writeBits(testValue, 8)

            # check written value
            buffer = writer.getByteArray()
            writtenTestValue = buffer[offset // 8] << (offset % 8)
            if offset % 8 != 0:
                writtenTestValue |= buffer[offset // 8 + 1] >> (8 - (offset % 8))
            self.assertEqual(testValue, writtenTestValue, msg=("Offset: " + str(offset)))

    def testWriteBits(self):
        writer = BitStreamWriter()
        writer.writeBits(0, 8)
        writer.writeBits(255, 8)
        writer.writeBits(1, 1)
        writer.writeBits(0x3f, 6)
        writer.writeBits(1, 1)
        self.assertEqual(b'\x00\xff\xff', writer.getByteArray())
        self.assertEqual(3 * 8, writer.getBitPosition())
        writer.writeBits(0xff, 8)
        self.assertEqual(b'\x00\xff\xff\xff', writer.getByteArray())
        self.assertEqual(4 * 8, writer.getBitPosition())
        writer.writeBits(0, 4)
        self.assertEqual(b'\x00\xff\xff\xff\x00', writer.getByteArray())
        self.assertEqual(4 * 8 + 4, writer.getBitPosition())
        writer.writeBits(0x0f, 4)
        self.assertEqual(b'\x00\xff\xff\xff\x0f', writer.getByteArray())
        self.assertEqual(5 * 8, writer.getBitPosition())
        writer.writeBits(0x80, 8)
        self.assertEqual(b'\x00\xff\xff\xff\x0f\x80', writer.getByteArray())
        self.assertEqual(6 * 8, writer.getBitPosition())

        with self.assertRaises(PythonRuntimeException):
            writer.writeBits(1, 0) # zero bits!

        with self.assertRaises(PythonRuntimeException):
            writer.writeBits(1, -1) # negative number of bits!

        with self.assertRaises(PythonRuntimeException):
            writer.writeBits(256, 8) # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.writeBits(-1, 8) # below the lower bound

    def testWriteSignedBits(self):
        writer = BitStreamWriter()
        writer.writeSignedBits(0, 1)
        writer.writeSignedBits(-1, 2)
        writer.writeSignedBits(-1, 5)
        self.assertEqual(b'\x7f', writer.getByteArray())
        self.assertEqual(8, writer.getBitPosition())
        writer.writeSignedBits(-1, 1)
        writer.writeSignedBits(-1, 7)
        self.assertEqual(b'\x7f\xff', writer.getByteArray())
        self.assertEqual(16, writer.getBitPosition())

        with self.assertRaises(PythonRuntimeException):
            writer.writeSignedBits(1, 0) # zero bits!

        with self.assertRaises(PythonRuntimeException):
            writer.writeSignedBits(1, 1) # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.writeSignedBits(128, 8) # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.writeSignedBits(-129, 8) # below the lower bound

    def testWriteVarInt16(self):
        writer = BitStreamWriter()
        writer.writeVarInt16(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt16(VARINT16_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt16(VARINT16_MAX + 1)

    def testWriteVarInt32(self):
        writer = BitStreamWriter()
        writer.writeVarInt32(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt32(VARINT32_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt32(VARINT32_MAX + 1)

    def testWriteVarInt64(self):
        writer = BitStreamWriter()
        writer.writeVarInt64(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt64(VARINT64_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt64(VARINT64_MAX + 1)

    def testWriteVarInt(self):
        writer = BitStreamWriter()
        writer.writeVarInt(0)
        self.assertEqual(b'\x00', writer.getByteArray())
        self.assertEqual(8, writer.getBitPosition())
        writer.writeVarInt(VARINT_MIN)
        self.assertEqual(16, writer.getBitPosition())
        self.assertEqual(b'\x00\x80', writer.getByteArray()) # INT64_MIN is encoded as -0
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt(VARINT_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarInt(VARINT_MAX + 1)

    def testWriteVarUInt16(self):
        writer = BitStreamWriter()
        writer.writeVarUInt16(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt16(VARUINT16_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt16(VARUINT16_MAX + 1)

    def testWriteVarUInt32(self):
        writer = BitStreamWriter()
        writer.writeVarUInt32(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt32(VARUINT32_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt32(VARUINT32_MAX + 1)

    def testWriteVarUInt64(self):
        writer = BitStreamWriter()
        writer.writeVarUInt64(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt64(VARUINT64_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt64(VARUINT64_MAX + 1)

    def testWriteVarUInt(self):
        writer = BitStreamWriter()
        writer.writeVarUInt(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt(VARUINT_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarUInt(VARUINT_MAX + 1)

    def testWriteVarSize(self):
        writer = BitStreamWriter()
        writer.writeVarSize(0)
        self.assertEqual(8, writer.getBitPosition())
        self.assertEqual(b'\x00', writer.getByteArray())
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarSize(VARSIZE_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.writeVarSize(VARSIZE_MAX + 1)

    def testWriteFloat16(self):
        writer = BitStreamWriter()
        writer.writeFloat16(0)
        self.assertEqual(16, writer.getBitPosition())
        self.assertEqual(b'\x00\x00', writer.getByteArray())

    def testWriteFloat32(self):
        writer = BitStreamWriter()
        writer.writeFloat32(0)
        self.assertEqual(32, writer.getBitPosition())
        self.assertEqual(b'\x00\x00\x00\x00', writer.getByteArray())

    def testWriteFloat64(self):
        writer = BitStreamWriter()
        writer.writeFloat64(0)
        self.assertEqual(64, writer.getBitPosition())
        self.assertEqual(b'\x00\x00\x00\x00\x00\x00\x00\x00', writer.getByteArray())

    def testWriteString(self):
        writer = BitStreamWriter()
        writer.writeString("")
        self.assertEqual(8, writer.getBitPosition()) # length 0
        self.assertEqual(b'\x00', writer.getByteArray())

    def testWriteBool(self):
        writer = BitStreamWriter()
        writer.writeBool(True)
        writer.writeBool(False)
        writer.writeBool(True)
        writer.writeBool(False)
        writer.writeBool(True)
        writer.writeBool(False)
        self.assertEqual(6, writer.getBitPosition())
        self.assertEqual(b'\xA8', writer.getByteArray())

    def testWriteBitBuffer(self):
        writer = BitStreamWriter()
        writer.writeBitBuffer(BitBuffer(bytes([0xAB, 0xE0]), 11))
        writer.writeBitBuffer(BitBuffer(bytes([0x00, 0xFE]), 15))
        self.assertEqual(8 + 11 + 8 + 15, writer.getBitPosition())
        self.assertEqual(b'\x0B\xAB\xE1\xE0\x1F\xC0', writer.getByteArray())

    def testGetByteArray(self):
        writer = BitStreamWriter()
        self.assertEqual(b'', writer.getByteArray())

    def testGetBitPosition(self):
        writer = BitStreamWriter()
        self.assertEqual(0, writer.getBitPosition())

    def testAlignTo(self):
        writer = BitStreamWriter()
        writer.alignTo(8)
        self.assertEqual(0, writer.getBitPosition())
        writer.alignTo(2)
        self.assertEqual(0, writer.getBitPosition())
        writer.writeBool(True)
        writer.alignTo(8)
        self.assertEqual(8, writer.getBitPosition())
        writer.writeBool(True)
        writer.alignTo(2)
        self.assertEqual(10, writer.getBitPosition())
