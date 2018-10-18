import unittest

from zserio import BitStreamWriter, PythonRuntimeException

class BitStreamWriterTest(unittest.TestCase):
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
