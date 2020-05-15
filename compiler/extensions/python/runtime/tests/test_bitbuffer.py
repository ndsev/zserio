import unittest

from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException

class BitStreamReaderTest(unittest.TestCase):

    def testBufferConstructor(self):
        byteSize = 2
        bitBuffer = BitBuffer(bytes([1, 2]))
        self.assertEqual(8 * byteSize, bitBuffer.getBitSize())

        emptyBitSize = 0
        emptyBitBuffer = BitBuffer(bytes([]))
        self.assertEqual(emptyBitSize, emptyBitBuffer.getBitSize())

    def testBufferBitSizeConstructor(self):
        bitSize = 11
        bitBuffer = BitBuffer(bytes([0x01, 0xE0]), bitSize)
        self.assertEqual(bitSize, bitBuffer.getBitSize())

        emptyBitSize = 0
        emptyBitBuffer = BitBuffer(bytes([]), emptyBitSize)
        self.assertEqual(emptyBitSize, emptyBitBuffer.getBitSize())

        outOfRangeBitSize = 9
        with self.assertRaises(PythonRuntimeException):
            BitBuffer([1], outOfRangeBitSize) # throws!

    def testEq(self):
        bitSize = 11
        bitBuffer1 = BitBuffer(bytes([0xAB, 0xE0]), bitSize)
        bitBuffer2 = BitBuffer(bytes([0xAB, 0xF0]), bitSize)
        self.assertEqual(bitBuffer1, bitBuffer2)

        bitBuffer3 = BitBuffer(bytes([0xAB, 0xFF]), bitSize)
        self.assertEqual(bitBuffer1, bitBuffer3)

        bitBuffer4 = BitBuffer(bytes([0xAB, 0xC0]), bitSize)
        self.assertNotEqual(bitBuffer1, bitBuffer4)

        bitBuffer5 = BitBuffer(bytes([0xBA, 0xE0]), bitSize)
        self.assertNotEqual(bitBuffer1, bitBuffer5)

        bitBuffer6 = BitBuffer(bytes([0xAB]))
        self.assertNotEqual(bitBuffer1, bitBuffer6)

        bitBuffer7 = BitBuffer(bytes())
        self.assertNotEqual(bitBuffer1, bitBuffer7)

        self.assertNotEqual(bitBuffer1, 1)

    def testHashCode(self):
        bitSize = 11
        bitBuffer1 = BitBuffer(bytes([0xAB, 0xE0]), bitSize)
        bitBuffer2 = BitBuffer(bytes([0xAB, 0xF0]), bitSize)
        self.assertEqual(hash(bitBuffer1), hash(bitBuffer2))

        bitBuffer3 = BitBuffer(bytes([0xAB, 0xFF]), bitSize)
        self.assertEqual(hash(bitBuffer1), hash(bitBuffer3))

        bitBuffer4 = BitBuffer(bytes([0xAB, 0xC0]), bitSize)
        self.assertNotEqual(hash(bitBuffer1), hash(bitBuffer4))

        bitBuffer5 = BitBuffer(bytes([0xBA, 0xE0]), bitSize)
        self.assertNotEqual(hash(bitBuffer1), hash(bitBuffer5))

        bitBuffer6 = BitBuffer(bytes([0xAB]))
        self.assertNotEqual(hash(bitBuffer1), hash(bitBuffer6))

        bitBuffer7 = BitBuffer(bytes())
        self.assertNotEqual(hash(bitBuffer1), hash(bitBuffer7))

    def testGetBuffer(self):
        bitSize = 11
        buffer = bytes([0xAB, 0xE0])
        bitBuffer = BitBuffer(buffer, bitSize)
        self.assertEqual(buffer, bitBuffer.getBuffer())

    def testGetBitSize(self):
        bitSize = 11
        bitBuffer = BitBuffer(bytes([0xAB, 0xE0]), bitSize)
        self.assertEqual(bitSize, bitBuffer.getBitSize())

    def testGetByteSize(self):
        bitSize = 11
        buffer = bytes([0xAB, 0xE0])
        byteSize = len(buffer)
        bitBuffer = BitBuffer(buffer, bitSize)
        self.assertEqual(byteSize, bitBuffer.getByteSize())
