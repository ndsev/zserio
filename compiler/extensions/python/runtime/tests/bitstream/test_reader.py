import unittest

from zserio import BitStreamReader, PythonRuntimeException
from zserio.bitstream.bitsizeof import INT64_MIN

class BitStreamReaderTest(unittest.TestCase):

    def testReadBits(self):
        data = [0, 1, 255, 128, 127]
        reader = BitStreamReader(bytes(data))
        for byte in data:
            self.assertEquals(byte, reader.readBits(8))

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(-1)

        self.assertEquals(0, reader.readBits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.readBits(1) # no more bits available

    def testReadSignedBits(self):
        data = [0, 0xff, 1, 127, 0x80]
        reader = BitStreamReader(bytes(data))
        self.assertEquals(0, reader.readSignedBits(8))
        self.assertEquals(-1, reader.readSignedBits(8)) # 0xff == -1
        self.assertEquals(1, reader.readSignedBits(8))
        self.assertEquals(127, reader.readSignedBits(8))
        self.assertEquals(-128, reader.readSignedBits(8)) # 0x80 == -128

        self.assertEquals(0, reader.readSignedBits(0)) # read 0 bits

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

