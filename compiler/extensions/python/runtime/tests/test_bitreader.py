import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import INT64_MIN
from zserio.exception import PythonRuntimeException

class BitStreamReaderTest(unittest.TestCase):

    def testFromBitBuffer(self):
        bitBuffer = BitBuffer(bytes([0xAE, 0xEA, 0x80]), 17)
        reader = BitStreamReader.fromBitBuffer(bitBuffer)
        self.assertEqual(bitBuffer.getBitSize(), reader.getBufferBitSize())
        self.assertEqual(0xAEE, reader.readBits(12))
        self.assertEqual(0x0A, reader.readBits(4))
        self.assertEqual(0x01, reader.readBits(1))
        with self.assertRaises(PythonRuntimeException):
            reader.readBits(1)

    def testFromBitBufferOverflow(self):
        bitBuffer = BitBuffer(bytes([0xFF, 0xFF, 0xF0]), 19)
        reader = BitStreamReader.fromBitBuffer(bitBuffer)
        self.assertEqual(bitBuffer.getBitSize(), reader.getBufferBitSize())
        with self.assertRaises(PythonRuntimeException):
            reader.readBits(20)

    def testReadUnalignedData(self):
        # number expected to read at offset
        testValue = 123

        for offset in range(65):
            buffer = bytearray((8 + offset + 7) // 8)

            # write test value at offset to data buffer
            buffer[offset // 8] = testValue >> (offset % 8)
            if offset % 8 != 0: # don't write behind the buffer
                buffer[offset // 8 + 1] = 0xff & testValue << (8 - offset % 8)

            bitBuffer = BitBuffer(buffer, 8 + offset)
            reader = BitStreamReader.fromBitBuffer(bitBuffer)

            # read offset bits
            self.assertEqual(0, reader.readBits(offset))

            # read magic number
            self.assertEqual(testValue, reader.readBits(8), msg=("Offset: " + str(offset)))

            # check eof
            with self.assertRaises(PythonRuntimeException):
                reader.readBits(1)

    def testReadBits(self):
        data = [0, 1, 255, 128, 127]
        reader = BitStreamReader(bytes(data))
        for byte in data:
            self.assertEqual(byte, reader.readBits(8))

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(-1)

        self.assertEqual(0, reader.readBits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(1) # no more bits available

    def testReadSignedBits(self):
        data = [0, 0xff, 1, 127, 0x80]
        reader = BitStreamReader(bytes(data))
        self.assertEqual(0, reader.readSignedBits(8))
        self.assertEqual(-1, reader.readSignedBits(8)) # 0xff == -1
        self.assertEqual(1, reader.readSignedBits(8))
        self.assertEqual(127, reader.readSignedBits(8))
        self.assertEqual(-128, reader.readSignedBits(8)) # 0x80 == -128

        self.assertEqual(0, reader.readSignedBits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.readSignedBits(1) # no more bits available

        with self.assertRaises(PythonRuntimeException):
            reader.readSignedBits(-1)

    def testReadVarInt16(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarInt16())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarInt16()

    def testReadVarInt32(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarInt32())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarInt32()

    def testReadVarInt64(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarInt64())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarInt64()

    def testReadVarInt(self):
        reader = BitStreamReader(b'\x00\x80')
        self.assertEqual(0, reader.readVarInt())
        self.assertEqual(8, reader.getBitPosition())
        self.assertEqual(INT64_MIN, reader.readVarInt())
        self.assertEqual(16, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarInt()

    def testReadVarUInt16(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarUInt16())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarUInt16()

    def testReadVarUInt32(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarUInt32())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarUInt32()

    def testReadVarUInt64(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarUInt64())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarUInt64()

    def testReadVarUInt(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.readVarUInt())
        self.assertEqual(8, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readVarUInt()

    def testReadFloat16(self):
        reader = BitStreamReader(bytes(2))
        self.assertEqual(0.0, reader.readFloat16())
        self.assertEqual(16, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readFloat16()

    def testReadFloat32(self):
        reader = BitStreamReader(bytes(4))
        self.assertEqual(0.0, reader.readFloat32())
        self.assertEqual(32, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readFloat32()

    def testReadFloat64(self):
        reader = BitStreamReader(bytes(8))
        self.assertEqual(0.0, reader.readFloat64())
        self.assertEqual(64, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readFloat64()

    def testReadString(self):
        reader = BitStreamReader(bytes(b'\x01\x41'))
        self.assertEqual("A", reader.readString())
        self.assertEqual(16, reader.getBitPosition())
        with self.assertRaises(PythonRuntimeException):
            reader.readString()

    def testReadBool(self):
        reader = BitStreamReader(bytes(b'\xA8'))
        self.assertEqual(True, reader.readBool())
        self.assertEqual(False, reader.readBool())
        self.assertEqual(True, reader.readBool())
        self.assertEqual(False, reader.readBool())
        self.assertEqual(True, reader.readBool())
        self.assertEqual(False, reader.readBool())
        self.assertEqual(False, reader.readBool())
        self.assertEqual(False, reader.readBool())
        with self.assertRaises(PythonRuntimeException):
            reader.readBool()

    def testReadBitBuffer(self):
        reader = BitStreamReader(bytes(b'\x0B\xAB\xE1\xE0\x1F\xC0'))
        self.assertEqual(BitBuffer(bytes([0xAB, 0xE0]), 11), reader.readBitBuffer())
        self.assertEqual(BitBuffer(bytes([0x00, 0xFE]), 15), reader.readBitBuffer())
        with self.assertRaises(PythonRuntimeException):
            reader.readBitBuffer()

    def testGetBitPosition(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.getBitPosition())
        reader.readBits(4)
        self.assertEqual(4, reader.getBitPosition())

    def testSetBitPosition(self):
        reader = BitStreamReader(bytes(1))
        reader.setBitPosition(0)
        self.assertEqual(0, reader.getBitPosition())
        reader.setBitPosition(7)
        self.assertEqual(7, reader.getBitPosition())
        reader.setBitPosition(8)
        self.assertEqual(8, reader.getBitPosition())

        with self.assertRaises(PythonRuntimeException):
            reader.setBitPosition(9)
        with self.assertRaises(PythonRuntimeException):
            reader.setBitPosition(-1)

        reader.setBitPosition(0)
        self.assertEqual(0, reader.getBitPosition())

    def testAlignTo(self):
        reader = BitStreamReader(bytes(1))
        reader.alignTo(1)
        self.assertEqual(0, reader.getBitPosition())
        reader.readBits(1)
        self.assertEqual(1, reader.getBitPosition())
        reader.alignTo(1)
        self.assertEqual(1, reader.getBitPosition())
        reader.alignTo(4)
        self.assertEqual(4, reader.getBitPosition())
